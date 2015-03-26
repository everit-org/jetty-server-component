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

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
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
@Service
public class HttpConnectionFactoryFactoryComponent implements ConnectionFactoryFactory {

  @BooleanAttribute(setter = "setDelayDispatchUntilContent", defaultValue = false)
  private boolean delayDispatchUntilContent = false;

  @Override
  public ConnectionFactory createConnectionFactory() {
    HttpConfiguration httpConfiguration = new HttpConfiguration();

    httpConfiguration.setDelayDispatchUntilContent(delayDispatchUntilContent);
    HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);

    return httpConnectionFactory;
  }

  public void setDelayDispatchUntilContent(final boolean delayDispatchUntilContent) {
    // TODO apply to all created connection factories
    this.delayDispatchUntilContent = delayDispatchUntilContent;
  }
}
