/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.jetty.server.ecm.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConfiguration.Customizer;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.everit.jetty.server.ConnectionFactoryFactory;
import org.everit.jetty.server.ecm.HttpConnectionFactoryFactoryConstants;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.Update;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.IntegerAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.extender.ExtendComponent;
import org.osgi.framework.Constants;

/**
 * ECM based configurable component that can start one or more {@link HttpConnectionFactory}s and
 * register them as OSGi services.
 */
@ExtendComponent
@Component(componentId = HttpConnectionFactoryFactoryConstants.SERVICE_FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    label = "Everit Jetty HttpConnectionFactory Factory",
    description = "Component to create HTTPConnectionFactory instances.")
@StringAttributes({
    @StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION, optional = true,
        priority = HttpConnectionFactoryAttributePriority.P01_SERVICE_DESCRIPTION,
        label = "Service description",
        description = "Optional description for the instantiated HttpConnectionFactory Factory.") })
@Service
public class HttpConnectionFactoryFactoryComponent implements ConnectionFactoryFactory {

  private final WeakHashMap<CloseableHttpConfigurationProvider, Boolean> activeConnectionFactories =
      new WeakHashMap<>();

  private boolean closeAllEndpointsAfterDynamicUpdate = false;

  private Customizer[] customizers;

  private boolean delayDispatchUntilContent = false;

  private boolean h2c;

  private int headerCacheSize;

  private int inputBufferSize;

  private Integer outputAggregationSize;

  private int outputBufferSize;

  private int requestHeaderSize;

  private int responseHeaderSize;

  private int securePort;

  private String secureScheme;

  private boolean sendDateHeader;

  private boolean sendServerVersion;

  private boolean sendXPoweredBy;

  private Set<CloseableHttpConfigurationProvider> cloneActiveHttpConnectionFactories() {
    Set<CloseableHttpConfigurationProvider> connectionFactories =
        cloneReferencedConnectionFactories();

    Set<CloseableHttpConfigurationProvider> result = new HashSet<>();

    for (CloseableHttpConfigurationProvider customConnectionFactory : connectionFactories) {
      result.add(customConnectionFactory);
    }

    return result;

  }

  private synchronized Set<CloseableHttpConfigurationProvider> cloneReferencedConnectionFactories() {
    Set<CloseableHttpConfigurationProvider> result = null;
    while (result == null) {
      try {
        result = new HashSet<>(this.activeConnectionFactories.keySet());
      } catch (ConcurrentModificationException e) {
        // TODO probably some warn logging would be nice
      }
    }
    return result;
  }

  @Override
  public synchronized ConnectionFactory createConnectionFactory(final String nextProtocol) {
    HttpConfiguration httpConfiguration = new HttpConfiguration();
    httpConfiguration.setDelayDispatchUntilContent(this.delayDispatchUntilContent);

    if (this.customizers != null) {
      httpConfiguration.setCustomizers(Arrays.asList(this.customizers));
    } else {
      List<Customizer> defaultCustomizers = new ArrayList<>();
      httpConfiguration.setCustomizers(defaultCustomizers);
    }

    httpConfiguration.setHeaderCacheSize(this.headerCacheSize);

    httpConfiguration.setOutputBufferSize(this.outputBufferSize);

    if (this.outputAggregationSize != null) {
      httpConfiguration.setOutputAggregationSize(this.outputAggregationSize);
    }

    httpConfiguration.setRequestHeaderSize(this.requestHeaderSize);
    httpConfiguration.setResponseHeaderSize(this.responseHeaderSize);
    httpConfiguration.setSecurePort(this.securePort);
    httpConfiguration.setSecureScheme(this.secureScheme);
    httpConfiguration.setSendDateHeader(this.sendDateHeader);
    httpConfiguration.setSendServerVersion(this.sendServerVersion);
    httpConfiguration.setSendXPoweredBy(this.sendXPoweredBy);

    CloseableHttpConfigurationProvider httpConnectionFactory = null;
    if (this.h2c) {
      httpConnectionFactory = new ClosableH2CConnectionFactory(httpConfiguration);
    } else {
      httpConnectionFactory = new CustomHttpConnectionFactory(httpConfiguration);
    }

    httpConnectionFactory.setInputBufferSize(this.inputBufferSize);

    this.activeConnectionFactories.put(httpConnectionFactory, true);
    return httpConnectionFactory;
  }

  /**
   * Updates the customizers on the component and all connection factories dynamically.
   **/
  @ServiceRef(referenceId = HttpConnectionFactoryFactoryConstants.ATTR_CUSTOMIZERS,
      optional = true, dynamic = true,
      attributePriority = HttpConnectionFactoryAttributePriority.P02_CUSTOMIZERS,
      label = "Customizers (target)",
      description = "Customizers are invoked for every request received. Customizers are often "
          + "used to interpret optional headers (eg ForwardedRequestCustomizer) or optional "
          + "protocol semantics (eg SecureRequestCustomizer).")
  public synchronized void setCustomizers(final Customizer[] customizers) {
    this.customizers = customizers;
    for (CloseableHttpConfigurationProvider httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setCustomizers(Arrays.asList(customizers));
    }
  }

  /**
   * Updates delayDispatchUntilContent on the component and all active connection factories.
   */
  @BooleanAttribute(
      attributeId = HttpConnectionFactoryFactoryConstants.ATTR_DELAY_DISPATCH_UNTIL_CONTENT,
      defaultValue = false, dynamic = true,
      priority = HttpConnectionFactoryAttributePriority.P14_DELAY_DISPATCH_UNTIL_CONTENT,
      label = "Delay dispatch until content",
      description = "If true, delay the application dispatch until content is available")
  public synchronized void setDelayDispatchUntilContent(final boolean delayDispatchUntilContent) {
    this.delayDispatchUntilContent = delayDispatchUntilContent;
    for (CloseableHttpConfigurationProvider httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setDelayDispatchUntilContent(
          delayDispatchUntilContent);
    }
    this.closeAllEndpointsAfterDynamicUpdate = true;
  }

  @BooleanAttribute(attributeId = "h2c", defaultValue = false, dynamic = false,
      priority = HttpConnectionFactoryAttributePriority.P15_H2C_SUPPORT,
      label = "Plain text HTTP/2 (h2c)",
      description = "If true, create plain text HTTP/2 (h2c) connections instead of HTTP1.1.")
  public synchronized void setH2c(boolean h2c) {
    this.h2c = h2c;
  }

  /**
   * Updates header cache size on component and all created connection factory.
   */
  @IntegerAttribute(attributeId = HttpConnectionFactoryFactoryConstants.ATTR_HEADER_CACHE_SIZE,
      defaultValue = HttpConnectionFactoryFactoryConstants.DEFAULT_HEADER_CACHE_SIZE,
      dynamic = true, priority = HttpConnectionFactoryAttributePriority.P11_HEADER_CACHE_SIZE,
      label = "Header cache size",
      description = "The maximum allowed size in bytes for a HTTP header field cache.")
  public void setHeaderCacheSize(final int headerCacheSize) {
    this.headerCacheSize = headerCacheSize;
    for (CloseableHttpConfigurationProvider httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setHeaderCacheSize(headerCacheSize);
    }
    this.closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Sets input buffer size on component and calls
   * {@link HttpConnectionFactory#setInputBufferSize(int)} on every generated
   * {@link HttpConnectionFactory}.
   */
  @IntegerAttribute(attributeId = HttpConnectionFactoryFactoryConstants.ATTR_INPUT_BUFFER_SIZE,
      defaultValue = HttpConnectionFactoryFactoryConstants.DEFAULT_INPUT_BUFFER_SIZE,
      dynamic = true, priority = HttpConnectionFactoryAttributePriority.P07_INPUT_BUFFER_SIZE,
      label = "Input buffer size",
      description = "Size of input buffer of the created connections")
  public void setInputBufferSize(final int inputBufferSize) {
    this.inputBufferSize = inputBufferSize;
    for (CloseableHttpConfigurationProvider httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.setInputBufferSize(inputBufferSize);
    }
    this.closeAllEndpointsAfterDynamicUpdate = true;
  }

  @IntegerAttribute(
      attributeId = HttpConnectionFactoryFactoryConstants.ATTR_OUTPUT_AGGREGATION_SIZE,
      optional = true, dynamic = true,
      priority = HttpConnectionFactoryAttributePriority.P10_OUTPUT_AGGREGATION_SIZE,
      label = "Output aggregation size",
      description = "The size of the buffer into which response content is aggregated before being "
          + "sent to the client. A larger buffer can improve performance by allowing a content"
          + " producer to run without blocking, however larger buffers consume more memory and may "
          + "induce some latency before a client starts processing the content. The default value "
          + "of this property is \"output buffer size / 4\".")
  public void setOutputAggregationSize(final Integer outputAggregationSize) {
    this.outputAggregationSize = outputAggregationSize;
  }

  @IntegerAttribute(attributeId = HttpConnectionFactoryFactoryConstants.ATTR_OUTPUT_BUFFER_SIZE,
      defaultValue = HttpConnectionFactoryFactoryConstants.DEFAULT_OUTPUT_BUFFER_SIZE,
      dynamic = true, priority = HttpConnectionFactoryAttributePriority.P09_OUTPUT_BUFFER_SIZE,
      label = "Output buffer size",
      description = "The size of the buffer into which response content is aggregated before being "
          + "sent to the client. A larger buffer can improve performance by allowing a content "
          + "producer to run without blocking, however larger buffers consume more memory and may "
          + "induce some latency before a client starts processing the content.")
  public void setOutputBufferSize(final int outputBufferSize) {
    this.outputBufferSize = outputBufferSize;
  }

  /**
   * Sets requestHeaderSize on component and all already created connection factories.
   */
  @IntegerAttribute(attributeId = HttpConnectionFactoryFactoryConstants.ATTR_REQUEST_HEADER_SIZE,
      defaultValue = HttpConnectionFactoryFactoryConstants.DEFAULT_REQUEST_HEADER_SIZE,
      dynamic = true, priority = HttpConnectionFactoryAttributePriority.P06_REQUEST_HEADER_SIZE,
      label = "Request header size",
      description = "The maximum size of a request header. Larger headers will allow for more "
          + "and/or larger cookies plus larger form content encoded in a URL. However, larger "
          + "headers consume more memory and can make a server more vulnerable to denial of "
          + "service attacks.")
  public synchronized void setRequestHeaderSize(final int requestHeaderSize) {
    this.requestHeaderSize = requestHeaderSize;
    for (CloseableHttpConfigurationProvider httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setRequestHeaderSize(requestHeaderSize);
    }
    this.closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Sets responseHeaderSize on component and all already created connection factories.
   */
  @IntegerAttribute(attributeId = HttpConnectionFactoryFactoryConstants.ATTR_RESPONSE_HEADER_SIZE,
      defaultValue = HttpConnectionFactoryFactoryConstants.DEFAULT_RESPONSE_HEADER_SIZE,
      dynamic = true, priority = HttpConnectionFactoryAttributePriority.P08_RESPONSE_HEADER_SIZE,
      label = "Response header size",
      description = "The maximum size of a response header. Larger headers will allow for more "
          + "and/or larger cookies and longer HTTP headers (eg for redirection). However, larger "
          + "headers will also consume more memory.")
  public synchronized void setResponseHeaderSize(final int responseHeaderSize) {
    this.responseHeaderSize = responseHeaderSize;
    for (CloseableHttpConfigurationProvider httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setResponseHeaderSize(responseHeaderSize);
    }
    this.closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Sets securePort an component and all already created connection factories.
   */
  @IntegerAttribute(attributeId = HttpConnectionFactoryFactoryConstants.ATTR_SECURE_PORT,
      defaultValue = HttpConnectionFactoryFactoryConstants.DEFAULT_SECURE_PORT, dynamic = true,
      priority = HttpConnectionFactoryAttributePriority.P13_SECURE_PORT, label = "Secure port",
      description = "The TCP/IP port used for CONFIDENTIAL and INTEGRAL redirections.")
  public synchronized void setSecurePort(final int securePort) {
    this.securePort = securePort;
    for (CloseableHttpConfigurationProvider httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setSecurePort(securePort);
    }
    this.closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Sets secureScheme an component and all already created connection factories.
   */
  @StringAttribute(attributeId = HttpConnectionFactoryFactoryConstants.ATTR_SECURE_SCHEME,
      defaultValue = HttpConnectionFactoryFactoryConstants.DEFAULT_SECURE_SCHEME, dynamic = true,
      priority = HttpConnectionFactoryAttributePriority.P12_SECURE_SCHEME, label = "Secure scheme",
      description = "The URI scheme used for CONFIDENTIAL and INTEGRAL redirections.")
  public synchronized void setSecureScheme(final String secureScheme) {
    this.secureScheme = secureScheme;
    for (CloseableHttpConfigurationProvider httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setSecureScheme(secureScheme);
    }
    this.closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Sets sendDateHeader an component and all already created connection factories.
   */
  @BooleanAttribute(attributeId = HttpConnectionFactoryFactoryConstants.ATTR_SEND_DATE_HEADER,
      defaultValue = HttpConnectionFactoryFactoryConstants.DEFAULT_SEND_DATE_HEADER, dynamic = true,
      priority = HttpConnectionFactoryAttributePriority.P03_SEND_DATE_HEADER,
      label = "Send date header",
      description = "")
  public synchronized void setSendDateHeader(final boolean sendDateHeader) {
    this.sendDateHeader = sendDateHeader;
    for (CloseableHttpConfigurationProvider httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setSendDateHeader(sendDateHeader);
    }
    this.closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Sets the sendServerVersion flag on the component and all referenced connections.
   */
  @BooleanAttribute(attributeId = HttpConnectionFactoryFactoryConstants.ATTR_SEND_SERVER_VERSION,
      defaultValue = HttpConnectionFactoryFactoryConstants.DEFAULT_SEND_SERVER_VERSION,
      dynamic = true, priority = HttpConnectionFactoryAttributePriority.P04_SEND_SERVER_VERSION,
      label = "Send server version",
      description = "")
  public void setSendServerVersion(final boolean sendServerVersion) {
    this.sendServerVersion = sendServerVersion;
    for (CloseableHttpConfigurationProvider httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setSendServerVersion(sendServerVersion);
    }
    this.closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Sets the sendServerVersion flag on the component and all referenced connections.
   */
  @BooleanAttribute(attributeId = HttpConnectionFactoryFactoryConstants.ATTR_SEND_X_POWERED_BY,
      defaultValue = HttpConnectionFactoryFactoryConstants.DEFAULT_SEND_X_POWERED_BY,
      dynamic = true, priority = HttpConnectionFactoryAttributePriority.P05_SEND_X_POWERED_BY,
      label = "Send x-powered-by",
      description = "")
  public void setSendXPoweredBy(final boolean sendXPoweredBy) {
    this.sendXPoweredBy = sendXPoweredBy;
    for (CloseableHttpConfigurationProvider httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setSendXPoweredBy(sendXPoweredBy);
    }
    this.closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Updates the outputBufferSize and outputAggregationSize on all active connection factories.
   */
  @Update
  public synchronized void update() {
    boolean closeAllEndpoints = this.closeAllEndpointsAfterDynamicUpdate;
    for (CloseableHttpConfigurationProvider connectionFactory : cloneReferencedConnectionFactories()) {
      HttpConfiguration httpConfiguration = connectionFactory.getHttpConfiguration();
      if (httpConfiguration.getOutputBufferSize() != this.outputBufferSize) {
        httpConfiguration.setOutputBufferSize(this.outputBufferSize);
        closeAllEndpoints = true;
      }
      if (this.outputAggregationSize != null
          && httpConfiguration.getOutputAggregationSize() != this.outputAggregationSize
              .intValue()) {
        httpConfiguration.setOutputAggregationSize(this.outputAggregationSize);
        closeAllEndpoints = true;
      }
      if (closeAllEndpoints) {
        connectionFactory.closeReferencedEndpoints();
      }
    }

    this.closeAllEndpointsAfterDynamicUpdate = false;
  }
}
