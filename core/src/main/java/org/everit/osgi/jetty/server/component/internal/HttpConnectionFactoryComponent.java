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

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.component.ComponentContext;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.osgi.framework.ServiceRegistration;

import aQute.bnd.annotation.headers.ProvideCapability;

@Component(componentId = "org.everit.osgi.jetty.server.component.HttpConnectionFactory",
    configurationPolicy = ConfigurationPolicy.FACTORY)
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
public class HttpConnectionFactoryComponent {

  @BooleanAttribute(setter = "setDelayDispatchUntilContent")
  private boolean delayDispatchUntilContent = false;

  private ServiceRegistration<ConnectionFactory> serviceRegistration;

  @Activate
  public void activate(final ComponentContext<HttpConnectionFactoryComponent> componentContext) {
    HttpConfiguration httpConfiguration = new HttpConfiguration();

    httpConfiguration.setDelayDispatchUntilContent(delayDispatchUntilContent);
    HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);

    Dictionary<String, Object> properties = new Hashtable<String, Object>(
        componentContext.getProperties());

    serviceRegistration = componentContext.registerService(
        ConnectionFactory.class, httpConnectionFactory, properties);
  }

  @Deactivate
  public void deactivate() {
    if (serviceRegistration != null) {
      serviceRegistration.unregister();
    }
  }

  public void setDelayDispatchUntilContent(final boolean delayDispatchUntilContent) {
    this.delayDispatchUntilContent = delayDispatchUntilContent;
  }
}
