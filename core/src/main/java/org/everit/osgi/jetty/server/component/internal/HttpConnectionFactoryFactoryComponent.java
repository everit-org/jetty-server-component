/*
 * Copyright (C) 2015 Everit Kft. (http://www.everit.org)
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
package org.everit.osgi.jetty.server.component.internal;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConfiguration.Customizer;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.everit.osgi.ecm.annotation.AttributeOrder;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.Update;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.IntegerAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.osgi.jetty.server.ConnectionFactoryFactory;
import org.everit.osgi.jetty.server.component.HttpConnectionFactoryConstants;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * ECM based configurable component that can start one or more {@link HttpConnectionFactory}s and
 * register them as OSGi services.
 */
@Component(componentId = HttpConnectionFactoryConstants.FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    localizationBase = "OSGI-INF/metatype/httpConnectionFactoryFactory")
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@AttributeOrder({ HttpConnectionFactoryConstants.SERVICE_REF_CUSTOMIZERS + ".target",
    HttpConnectionFactoryConstants.ATTR_SEND_SERVER_VERSION,
    HttpConnectionFactoryConstants.ATTR_SEND_DATE_HEADER,
    HttpConnectionFactoryConstants.ATTR_REQUEST_HEADER_SIZE,
    HttpConnectionFactoryConstants.ATTR_INPUT_BUFFER_SIZE,
    HttpConnectionFactoryConstants.ATTR_RESPONSE_HEADER_SIZE,
    HttpConnectionFactoryConstants.ATTR_OUTPUT_BUFFER_SIZE,
    HttpConnectionFactoryConstants.ATTR_OUTPUT_AGGREGATION_SIZE,
    HttpConnectionFactoryConstants.ATTR_HEADER_CACHE_SIZE,
    HttpConnectionFactoryConstants.ATTR_SECURE_SCHEME,
    HttpConnectionFactoryConstants.ATTR_SECURE_PORT,
    HttpConnectionFactoryConstants.ATTR_DELAY_DISPATCH_UNTIL_CONTENT, })
@Service
public class HttpConnectionFactoryFactoryComponent implements ConnectionFactoryFactory {

  private final WeakHashMap<CustomConnectionFactory<HttpConnectionFactory>, Boolean> activeConnectionFactories = new WeakHashMap<>(); // CS_DISABLE_LINE_LENGTH

  private boolean closeAllEndpointsAfterDynamicUpdate = false;

  private Customizer[] customizers;

  private boolean delayDispatchUntilContent = false;

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

  private synchronized Set<CustomConnectionFactory<HttpConnectionFactory>> cloneActiveConnectionFactories() { // CS_DISABLE_LINE_LENGTH
    Set<CustomConnectionFactory<HttpConnectionFactory>> result = null;
    while (result == null) {
      try {
        result = new HashSet<>(activeConnectionFactories.keySet());
      } catch (ConcurrentModificationException e) {
        // TODO probably some warn logging would be nice
      }
    }
    return result;
  }

  private Set<HttpConnectionFactory> cloneActiveHttpConnectionFactories() {
    Set<CustomConnectionFactory<HttpConnectionFactory>> connectionFactories =
        cloneActiveConnectionFactories();

    Set<HttpConnectionFactory> result = new HashSet<HttpConnectionFactory>();

    for (CustomConnectionFactory<HttpConnectionFactory> customConnectionFactory : connectionFactories) { // CS_DISABLE_LINE_LENGTH
      result.add(customConnectionFactory.getWrapped());
    }

    return result;

  }

  @Override
  public synchronized ConnectionFactory createConnectionFactory(final String nextProtocol) {
    HttpConfiguration httpConfiguration = new HttpConfiguration();
    httpConfiguration.setDelayDispatchUntilContent(delayDispatchUntilContent);
    httpConfiguration.setCustomizers(Arrays.asList(customizers));
    httpConfiguration.setHeaderCacheSize(headerCacheSize);

    httpConfiguration.setOutputBufferSize(outputBufferSize);

    if (outputAggregationSize != null) {
      httpConfiguration.setOutputAggregationSize(outputAggregationSize);
    }

    httpConfiguration.setRequestHeaderSize(requestHeaderSize);
    httpConfiguration.setResponseHeaderSize(responseHeaderSize);
    httpConfiguration.setSecurePort(securePort);
    httpConfiguration.setSecureScheme(secureScheme);
    httpConfiguration.setSendDateHeader(sendDateHeader);
    httpConfiguration.setSendServerVersion(sendServerVersion);

    HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);
    httpConnectionFactory.setInputBufferSize(inputBufferSize);

    CustomConnectionFactory<HttpConnectionFactory> customConnectionFactory =
        new CustomConnectionFactory<HttpConnectionFactory>(httpConnectionFactory);

    activeConnectionFactories.put(customConnectionFactory, true);
    return customConnectionFactory;
  }

  @Override
  public String getProtocol() {
    return HttpVersion.HTTP_1_1.toString();
  }

  /**
   * Updates the customizers on the component and all connection factories dynamically.
   **/
  @ServiceRef(referenceId = HttpConnectionFactoryConstants.SERVICE_REF_CUSTOMIZERS,
      optional = true, dynamic = true)
  public synchronized void setCustomizers(final Customizer[] customizers) {
    this.customizers = customizers;
    for (HttpConnectionFactory httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setCustomizers(Arrays.asList(customizers));
    }
  }

  /**
   * Updates delayDispatchUntilContent on the component and all active connection factories.
   */
  @BooleanAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_DELAY_DISPATCH_UNTIL_CONTENT,
      defaultValue = false, dynamic = true)
  public synchronized void setDelayDispatchUntilContent(final boolean delayDispatchUntilContent) {
    this.delayDispatchUntilContent = delayDispatchUntilContent;
    for (HttpConnectionFactory httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setDelayDispatchUntilContent(
          delayDispatchUntilContent);
    }
    closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Updates header cache size on component and all created connection factory.
   */
  @IntegerAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_HEADER_CACHE_SIZE,
      defaultValue = HttpConnectionFactoryConstants.DEFAULT_HEADER_CACHE_SIZE, dynamic = true)
  public void setHeaderCacheSize(final int headerCacheSize) {
    this.headerCacheSize = headerCacheSize;
    for (HttpConnectionFactory httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setHeaderCacheSize(headerCacheSize);
    }
    closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Sets input buffer size on component and calls
   * {@link HttpConnectionFactory#setInputBufferSize(int)} on every generated
   * {@link HttpConnectionFactory}.
   */
  @IntegerAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_INPUT_BUFFER_SIZE,
      defaultValue = HttpConnectionFactoryConstants.DEFAULT_INPUT_BUFFER_SIZE, dynamic = true)
  public void setInputBufferSize(final int inputBufferSize) {
    this.inputBufferSize = inputBufferSize;
    for (HttpConnectionFactory httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.setInputBufferSize(inputBufferSize);
    }
    closeAllEndpointsAfterDynamicUpdate = true;
  }

  @IntegerAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_OUTPUT_AGGREGATION_SIZE,
      optional = true, dynamic = true)
  public void setOutputAggregationSize(final Integer outputAggregationSize) {
    this.outputAggregationSize = outputAggregationSize;
  }

  @IntegerAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_OUTPUT_BUFFER_SIZE,
      defaultValue = HttpConnectionFactoryConstants.DEFAULT_OUTPUT_BUFFER_SIZE, dynamic = true)
  public void setOutputBufferSize(final int outputBufferSize) {
    this.outputBufferSize = outputBufferSize;
  }

  /**
   * Sets requestHeaderSize on component and all already created connection factories.
   */
  @IntegerAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_REQUEST_HEADER_SIZE,
      defaultValue = HttpConnectionFactoryConstants.DEFAULT_REQUEST_HEADER_SIZE, dynamic = true)
  public synchronized void setRequestHeaderSize(final int requestHeaderSize) {
    this.requestHeaderSize = requestHeaderSize;
    for (HttpConnectionFactory httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setRequestHeaderSize(requestHeaderSize);
    }
    closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Sets responseHeaderSize on component and all already created connection factories.
   */
  @IntegerAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_RESPONSE_HEADER_SIZE,
      defaultValue = HttpConnectionFactoryConstants.DEFAULT_RESPONSE_HEADER_SIZE, dynamic = true)
  public synchronized void setResponseHeaderSize(final int responseHeaderSize) {
    this.responseHeaderSize = responseHeaderSize;
    for (HttpConnectionFactory httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setResponseHeaderSize(responseHeaderSize);
    }
    closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Sets securePort an component and all already created connection factories.
   */
  @IntegerAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_SECURE_PORT,
      defaultValue = HttpConnectionFactoryConstants.DEFAULT_SECURE_PORT, dynamic = true)
  public synchronized void setSecurePort(final int securePort) {
    this.securePort = securePort;
    for (HttpConnectionFactory httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setSecurePort(securePort);
    }
    closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Sets secureScheme an component and all already created connection factories.
   */
  @StringAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_SECURE_SCHEME,
      defaultValue = HttpConnectionFactoryConstants.DEFAULT_SECURE_SCHEME, dynamic = true)
  public synchronized void setSecureScheme(final String secureScheme) {
    this.secureScheme = secureScheme;
    for (HttpConnectionFactory httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setSecureScheme(secureScheme);
    }
    closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Sets sendDateHeader an component and all already created connection factories.
   */
  @BooleanAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_SEND_DATE_HEADER,
      defaultValue = HttpConnectionFactoryConstants.DEFAULT_SEND_DATE_HEADER, dynamic = true)
  public synchronized void setSendDateHeader(final boolean sendDateHeader) {
    this.sendDateHeader = sendDateHeader;
    for (HttpConnectionFactory httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setSendDateHeader(sendDateHeader);
    }
    closeAllEndpointsAfterDynamicUpdate = true;
  }

  @BooleanAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_SEND_SERVER_VERSION,
      defaultValue = HttpConnectionFactoryConstants.DEFAULT_SEND_SERVER_VERSION, dynamic = true)
  public void setSendServerVersion(final boolean sendServerVersion) {
    this.sendServerVersion = sendServerVersion;
    for (HttpConnectionFactory httpConnectionFactory : cloneActiveHttpConnectionFactories()) {
      httpConnectionFactory.getHttpConfiguration().setSendServerVersion(sendServerVersion);
    }
    closeAllEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Updates the outputBufferSize and outputAggregationSize on all active connection factories.
   */
  @Update
  public synchronized void update() {
    boolean closeAllEndpoints = closeAllEndpointsAfterDynamicUpdate;
    for (CustomConnectionFactory<HttpConnectionFactory> connectionFactory : cloneActiveConnectionFactories()) { // CS_DISABLE_LINE_LENGTH
      HttpConnectionFactory httpConnectionFactory = connectionFactory.getWrapped();
      HttpConfiguration httpConfiguration = httpConnectionFactory.getHttpConfiguration();
      if (httpConfiguration.getOutputBufferSize() != outputBufferSize) {
        httpConfiguration.setOutputBufferSize(outputBufferSize);
        closeAllEndpoints = true;
      }
      if (outputAggregationSize != null
          && httpConfiguration.getOutputAggregationSize() != outputAggregationSize.intValue()) {
        httpConfiguration.setOutputAggregationSize(outputAggregationSize);
        closeAllEndpoints = true;
      }
      if (closeAllEndpoints) {
        connectionFactory.closeAllReferencedEndpoint();
      }
    }

    closeAllEndpointsAfterDynamicUpdate = false;
  }
}
