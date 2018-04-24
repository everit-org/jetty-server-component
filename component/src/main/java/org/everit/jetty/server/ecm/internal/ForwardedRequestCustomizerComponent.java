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

import java.util.Hashtable;

import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration.Customizer;
import org.everit.jetty.server.ecm.ForwardedRequestCustomizerConstants;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.ManualService;
import org.everit.osgi.ecm.annotation.ManualServices;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.component.ComponentContext;
import org.everit.osgi.ecm.extender.ExtendComponent;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

/**
 * Component that wraps {@link ForwardedRequestCustomizer} and registers it as an OSGi service.
 */
@ExtendComponent
@Component(componentId = ForwardedRequestCustomizerConstants.SERVICE_FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    label = "Everit Jetty Forwarded Request Customizer")
@StringAttributes({
    @StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION, optional = true,
        label = "Service description",
        description = "Optional description for Forwarded Request Customizer instance.") })
@ManualServices(@ManualService(Customizer.class))
public class ForwardedRequestCustomizerComponent {

  private String forwardedCipherSuite;

  private String forwardedHostHeader;

  private String forwardedProtoHeader;

  private String forwardedRemoteAddressHeader;

  private String forwardedServerHeader;

  private String forwardedSslSessionId;

  private String hostHeader;

  private ServiceRegistration<Customizer> serviceRegistration;

  /**
   * Instantiates a {@link ForwardedRequestCustomizer} based on the configuration and registers it
   * as an OSGi service.
   */
  @Activate
  public void activate(
      final ComponentContext<ForwardedRequestCustomizerComponent> componentContext) {
    ForwardedRequestCustomizer customizer = new ForwardedRequestCustomizer();
    if (this.forwardedCipherSuite != null) {
      customizer.setForwardedCipherSuiteHeader(this.forwardedCipherSuite);
    }
    customizer.setForwardedForHeader(this.forwardedRemoteAddressHeader);
    customizer.setForwardedHostHeader(this.forwardedHostHeader);
    customizer.setForwardedProtoHeader(this.forwardedProtoHeader);
    customizer.setForwardedServerHeader(this.forwardedServerHeader);

    if (this.forwardedSslSessionId != null) {
      customizer.setForwardedSslSessionIdHeader(this.forwardedSslSessionId);
    }
    if (this.hostHeader != null) {
      customizer.setHostHeader(this.hostHeader);
    }

    this.serviceRegistration = componentContext.registerService(Customizer.class, customizer,
        new Hashtable<>(componentContext.getProperties()));
  }

  @Deactivate
  public void deactivate() {
    this.serviceRegistration.unregister();
  }

  @StringAttribute(attributeId = ForwardedRequestCustomizerConstants.ATTR_FORWARDED_CIPHER_SUITE,
      optional = true, label = "Cipher Suite HTTP Header",
      description = "The header name holding a forwarded cipher suite")
  public void setForwardedCipherSuite(final String forwardedCipherSuite) {
    this.forwardedCipherSuite = forwardedCipherSuite;
  }

  @StringAttribute(attributeId = ForwardedRequestCustomizerConstants.ATTR_FORWARDED_HOST_HEADER,
      defaultValue = "X-Forwarded-Host", label = "Host HTTP Header",
      description = "A de facto standard for identifying the original host requested by the "
          + "client in the Host HTTP request header, since the host name and/or port of the "
          + "reverse proxy (load balancer) may differ from the origin server handling the request.")
  public void setForwardedHostHeader(final String forwardedHostHeader) {
    this.forwardedHostHeader = forwardedHostHeader;
  }

  @StringAttribute(attributeId = ForwardedRequestCustomizerConstants.ATTR_FORWARDED_PROTO_HEADER,
      defaultValue = "X-Forwarded-Proto", label = "Proto HTTP Header",
      description = "A de facto standard for identifying the originating protocol of an HTTP "
          + "request, since a reverse proxy (or a load balancer) may communicate with a web "
          + "server using HTTP even if the request to the reverse proxy is HTTPS.")
  public void setForwardedProtoHeader(final String forwardedProtoHeader) {
    this.forwardedProtoHeader = forwardedProtoHeader;
  }

  @StringAttribute(
      attributeId = ForwardedRequestCustomizerConstants.ATTR_FORWARDED_REMOTE_ADDRESS_HEADER,
      defaultValue = "X-Forwarded-For", label = "Remote Address HTTP Header",
      description = "A de facto standard for identifying the originating IP address of a "
          + "client connecting to a web server through an HTTP proxy or load balancer.")
  public void setForwardedRemoteAddressHeader(final String forwardedRemoteAddressHeader) {
    this.forwardedRemoteAddressHeader = forwardedRemoteAddressHeader;
  }

  @StringAttribute(attributeId = ForwardedRequestCustomizerConstants.ATTR_FORWARDED_SERVER_HEADER,
      defaultValue = "X-Forwarded-Server", label = "Server HTTP Header",
      description = "HTTP header that contains the hostname of the proxy server.")
  public void setForwardedServerHeader(final String forwardedServerHeader) {
    this.forwardedServerHeader = forwardedServerHeader;
  }

  @StringAttribute(attributeId = ForwardedRequestCustomizerConstants.ATTR_FORWARDED_SSL_SESSION_ID,
      optional = true, label = "SSL Session Id HTTP Header",
      description = " The header name holding a forwarded SSL Session ID")
  public void setForwardedSslSessionId(final String forwardedSslSessionId) {
    this.forwardedSslSessionId = forwardedSslSessionId;
  }

  @StringAttribute(attributeId = ForwardedRequestCustomizerConstants.ATTR_HOST_HEADER,
      optional = true, label = "Forced Host HTTP Header",
      description = " The value of the host header to force.")
  public void setHostHeader(final String hostHeader) {
    this.hostHeader = hostHeader;
  }
}
