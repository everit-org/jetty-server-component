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

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.jetty.server.HttpConfiguration.Customizer;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.everit.jetty.server.ecm.SecureRequestCustomizerConstants;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.ManualService;
import org.everit.osgi.ecm.annotation.ManualServices;
import org.everit.osgi.ecm.component.ComponentContext;
import org.everit.osgi.ecm.extender.ExtendComponent;
import org.osgi.framework.ServiceRegistration;

/**
 * A very simple component that registers a {@link SecureRequestCustomizer} instance as an OSGi
 * service.
 */
@ExtendComponent
@Component(componentId = SecureRequestCustomizerConstants.SERVICE_FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.IGNORE,
    label = "Everit Jetty Secure Request Customizer",
    description = "The component automatically registers a SecureRequestCustomizer OSGi service. "
        + "Customizer that extracts the attribute from an SSLContext and sets them on the request "
        + "with ServletRequest.setAttribute(String, Object) according to Servlet Specification "
        + "Requirements.")
@ManualServices(@ManualService({ Customizer.class, SecureRequestCustomizer.class }))
public class SecureRequestCustomizerComponent {

  private ServiceRegistration<?> serviceRegistration;

  /**
   * Creates a new {@link SecureRequestCustomizer} and registers it as an OSGi service.
   */
  @Activate
  public void activate(final ComponentContext<SecureRequestCustomizerComponent> componentContext) {
    SecureRequestCustomizer secureRequestCustomizer = new SecureRequestCustomizer();

    Dictionary<String, Object> properties = new Hashtable<>(componentContext.getProperties());
    this.serviceRegistration = componentContext.registerService(
        new String[] { Customizer.class.getName(), SecureRequestCustomizer.class.getName() },
        secureRequestCustomizer, properties);
  }

  @Deactivate
  public void deactivate() {
    this.serviceRegistration.unregister();
  }
}
