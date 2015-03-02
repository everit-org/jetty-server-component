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

import java.util.EnumSet;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.ReferenceConfigurationType;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.component.ConfigurationException;
import org.everit.osgi.ecm.component.ServiceHolder;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.osgi.jetty.server.component.JettyComponentConstants;
import org.everit.osgi.jetty.server.component.ServletContextHandlerFactory;

import aQute.bnd.annotation.headers.ProvideCapability;

@Component(componentId = "org.everit.osgi.jetty.server.component.ServletContextHandlerFactory",
    configurationPolicy = ConfigurationPolicy.FACTORY)
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
public class ServletContextHandlerFactoryComponent implements ServletContextHandlerFactory {

  @ServiceRef(setter = "setFilters", configurationType = ReferenceConfigurationType.CLAUSE,
      optional = true)
  private ServiceHolder<Filter>[] filters;

  @ServiceRef(setter = "setSecurityHandler", optional = true)
  private SecurityHandler securityHandler;

  @ServiceRef(setter = "setServlets", configurationType = ReferenceConfigurationType.CLAUSE,
      optional = true)
  private ServiceHolder<Servlet>[] servlets;

  @ServiceRef(setter = "setSessionManager", optional = true)
  private SessionManager sessionManager;

  private void addFiltersToHandler(final ServletContextHandler servletHandler) {
    for (ServiceHolder<Filter> serviceHolder : filters) {
      Filter filter = serviceHolder.getService();
      Map<String, Object> attributes = serviceHolder.getAttributes();

      String mapping = resolveMapping("filter", attributes);

      EnumSet<DispatcherType> dispatcherTypes = resolveDispatcherTypes("filter", attributes);

      servletHandler.addFilter(new FilterHolder(filter), String.valueOf(mapping), dispatcherTypes);

    }
  }

  private void addServletsToHandler(final ServletContextHandler servletHandler) {
    for (ServiceHolder<Servlet> serviceHolder : servlets) {
      Servlet servlet = serviceHolder.getService();
      Map<String, Object> attributes = serviceHolder.getAttributes();

      String mapping = resolveMapping("servlet", attributes);

      servletHandler.addServlet(new ServletHolder(servlet), String.valueOf(mapping));

    }
  }

  @Override
  public ServletContextHandler createHandler(final HandlerContainer parent) {
    ServletContextHandler servletContextHandler = new ServletContextHandler();

    addFiltersToHandler(servletContextHandler);
    addServletsToHandler(servletContextHandler);

    if (sessionManager != null) {
      servletContextHandler.setSessionHandler(new SessionHandler(sessionManager));
    }

    if (securityHandler != null) {
      servletContextHandler.setSecurityHandler(securityHandler);
    }
    return servletContextHandler;
  }

  private EnumSet<DispatcherType> resolveDispatcherTypes(final String attributeName,
      final Map<String, Object> clauseAttributes) {
    EnumSet<DispatcherType> dispatcherTypes;
    Object dispatcherAttr = clauseAttributes.get(JettyComponentConstants.ATTR_DISPATCHER);

    if (dispatcherAttr == null) {
      dispatcherTypes = EnumSet.allOf(DispatcherType.class);
    } else {
      dispatcherTypes = EnumSet.noneOf(DispatcherType.class);

      String[] dispatcherTypeStringArray = String.valueOf(dispatcherAttr).split(",");
      for (String dispatcherTypeString : dispatcherTypeStringArray) {
        try {
          DispatcherType dispatcherType = DispatcherType.valueOf(dispatcherTypeString);
          dispatcherTypes.add(dispatcherType);
        } catch (IllegalArgumentException e) {
          throw new ConfigurationException(
              "Invalid dispatcherType in '" + attributeName + "' configuration: "
                  + dispatcherTypeString, e);
        }
      }
    }
    return dispatcherTypes;
  }

  private String resolveMapping(final String referenceName,
      final Map<String, Object> clauseAttributes) {
    Object mappingAttrValue = clauseAttributes.get(JettyComponentConstants.ATTR_MAPPING);
    if (mappingAttrValue == null) {
      throw new ConfigurationException("Attribute " + JettyComponentConstants.ATTR_MAPPING
          + " must be specified for every " + referenceName + " clause");
    }

    String mapping = String.valueOf(mappingAttrValue);
    return mapping;
  }

  public void setFilters(final ServiceHolder<Filter>[] filters) {
    this.filters = filters;
  }

  public void setSecurityHandler(final SecurityHandler securityHandler) {
    this.securityHandler = securityHandler;
  }

  public void setServlets(final ServiceHolder<Servlet>[] servlets) {
    this.servlets = servlets;
  }

  public void setSessionManager(final SessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }
}
