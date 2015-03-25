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

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.AttributeOrder;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.ReferenceConfigurationType;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.Update;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.component.ServiceHolder;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.osgi.jetty.server.SecurityHandlerFactory;
import org.everit.osgi.jetty.server.ServletContextHandlerFactory;
import org.everit.osgi.jetty.server.SessionManagerFactory;
import org.everit.osgi.jetty.server.component.ServletContextHandlerFactoryConstants;
import org.everit.osgi.jetty.server.component.internal.servletcontext.FilterHolderManager;
import org.everit.osgi.jetty.server.component.internal.servletcontext.FilterMappingKey;
import org.everit.osgi.jetty.server.component.internal.servletcontext.FilterMappingManager;
import org.everit.osgi.jetty.server.component.internal.servletcontext.HolderKey;
import org.everit.osgi.jetty.server.component.internal.servletcontext.ServletHolderManager;
import org.everit.osgi.jetty.server.component.internal.servletcontext.ServletMappingKey;
import org.everit.osgi.jetty.server.component.internal.servletcontext.ServletMappingManager;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * ECM based configurable component that registers one or more instantiated
 * {@link ServletContextHandler} OSGi services. The component handles filter and servlet reference
 * changes dynamically.
 */
@Component(componentId = ServletContextHandlerFactoryConstants.FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    localizationBase = "OSGI-INF/metatype/servletContextHandlerFactory")
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@Service(ServletContextHandlerFactory.class)
@AttributeOrder({
    ServletContextHandlerFactoryConstants.SERVICE_REF_SERVLETS + ".clause",
    ServletContextHandlerFactoryConstants.SERVICE_REF_FILTERS + ".clause",
    ServletContextHandlerFactoryConstants.ATTR_SESSIONS,
    ServletContextHandlerFactoryConstants.SERVICE_REF_SESSION_MANAGER_FACTORY + ".target",
    ServletContextHandlerFactoryConstants.ATTR_SECURITY,
    ServletContextHandlerFactoryConstants.SERVICE_REF_SECURITY_HANDLER_FACTORY + ".target" })
public class ServletContextHandlerFactoryComponent implements ServletContextHandlerFactory {

  private final WeakHashMap<CustomServletHandler, Boolean> activeServletHandlers =
      new WeakHashMap<>();

  private final FilterHolderManager filterHolderManager = new FilterHolderManager();

  private HolderKey<Filter>[] filterKeys;

  private FilterMappingKey[] filterMappingKeys;

  private final FilterMappingManager filterMappingManager = new FilterMappingManager();

  private boolean security = false;

  private SecurityHandlerFactory securityHandlerFactory;

  private final ServletHolderManager servletHolderManager = new ServletHolderManager();

  private HolderKey<Servlet>[] servletKeys;

  private ServletMappingKey[] servletMappingKeys;

  private final ServletMappingManager servletMappingManager = new ServletMappingManager();

  private SessionManagerFactory sessionManagerFactory;

  private boolean sessions = true;

  @Activate
  public void activate() {
    servletHolderManager.updatePrviousKeys(servletKeys);
    filterHolderManager.updatePrviousKeys(filterKeys);
  }

  private Set<CustomServletHandler> cloneActiveServletHandlerSet() {
    Set<CustomServletHandler> result = null;
    while (result == null) {
      Set<CustomServletHandler> keySet = activeServletHandlers.keySet();
      try {
        result = new HashSet<CustomServletHandler>(keySet);
      } catch (ConcurrentModificationException e) {
        // Do nothing
      }
    }
    return result;
  }

  @Override
  public synchronized ServletContextHandler createHandler(final HandlerContainer parent,
      final String contextPath) {
    CustomServletHandler servletHandler = new CustomServletHandler();

    SessionHandler sessionHandler = null;
    if (sessionManagerFactory != null) {
      sessionHandler = new SessionHandler();
      SessionManager sessionManager = sessionManagerFactory.createSessionManager(sessionHandler);
      sessionHandler.setSessionManager(sessionManager);
    }

    int sessionsFlag = (sessions) ? ServletContextHandler.SESSIONS
        : ServletContextHandler.NO_SESSIONS;

    int securityFlag = (security) ? ServletContextHandler.SECURITY
        : ServletContextHandler.NO_SECURITY;

    int options = securityFlag | sessionsFlag;

    ServletContextHandler servletContextHandler = new ServletContextHandler(parent,
        contextPath, sessionHandler, null, servletHandler, null, options);

    updateServletHandlerWithDynamicSettings(servletHandler);

    activeServletHandlers.put(servletHandler, Boolean.TRUE);
    return servletContextHandler;
  }

  public SecurityHandlerFactory getSecurityHandlerFactory() {
    // TODO remove when used
    return securityHandlerFactory;
  }

  private FilterMappingKey[] resolveFilterMappingKeys(final ServiceHolder<Filter>[] filters) {
    FilterMappingKey[] result = new FilterMappingKey[filters.length];
    for (int i = 0; i < filters.length; i++) {
      ServiceHolder<Filter> serviceHolder = filters[i];
      result[i] = new FilterMappingKey(serviceHolder);
    }
    return result;
  }

  private <E> HolderKey<E>[] resolveHolderKeys(final ServiceHolder<E>[] serviceHolders) {
    @SuppressWarnings("unchecked")
    HolderKey<E>[] result = new HolderKey[serviceHolders.length];

    for (int i = 0; i < serviceHolders.length; i++) {
      ServiceHolder<E> serviceHolder = serviceHolders[i];
      result[i] = new HolderKey<E>(serviceHolder);
    }
    return result;
  }

  private ServletMappingKey[] resolveServletMappingKeys(final ServiceHolder<Servlet>[] servlets) {
    ServletMappingKey[] result = new ServletMappingKey[servlets.length];
    for (int i = 0; i < servlets.length; i++) {
      ServiceHolder<Servlet> serviceHolder = servlets[i];
      result[i] = new ServletMappingKey(serviceHolder);
    }
    return result;
  }

  @ServiceRef(referenceId = ServletContextHandlerFactoryConstants.SERVICE_REF_FILTERS,
      configurationType = ReferenceConfigurationType.CLAUSE, optional = true, dynamic = true)
  public synchronized void setFilters(final ServiceHolder<Filter>[] filters) {
    filterKeys = resolveHolderKeys(filters);
    filterMappingKeys = resolveFilterMappingKeys(filters);
  }

  @BooleanAttribute(attributeId = ServletContextHandlerFactoryConstants.ATTR_SECURITY,
      defaultValue = false)
  public void setSecurity(final boolean security) {
    this.security = security;
  }

  @ServiceRef(
      referenceId = ServletContextHandlerFactoryConstants.SERVICE_REF_SECURITY_HANDLER_FACTORY,
      optional = true)
  public void setSecurityHandlerFactory(final SecurityHandlerFactory securityHandlerFactory) {
    this.securityHandlerFactory = securityHandlerFactory;
  }

  @ServiceRef(referenceId = ServletContextHandlerFactoryConstants.SERVICE_REF_SERVLETS,
      configurationType = ReferenceConfigurationType.CLAUSE, optional = true, dynamic = true)
  public void setServlets(final ServiceHolder<Servlet>[] servlets) {
    servletKeys = resolveHolderKeys(servlets);
    servletMappingKeys = resolveServletMappingKeys(servlets);
  }

  @ServiceRef(
      referenceId = ServletContextHandlerFactoryConstants.SERVICE_REF_SESSION_MANAGER_FACTORY,
      optional = true)
  public void setSessionManagerFactory(final SessionManagerFactory sessionManagerFactory) {
    this.sessionManagerFactory = sessionManagerFactory;
  }

  @BooleanAttribute(attributeId = ServletContextHandlerFactoryConstants.ATTR_SESSIONS,
      defaultValue = true)
  public void setSessions(final boolean sessions) {
    this.sessions = sessions;
  }

  /**
   * Updates the dynamic references (filters and servlets with their mappings) on all of the created
   * and still used (the instances are referenced) {@link ServletContextHandler} instances.
   */
  @Update
  public synchronized void update() {

    Set<CustomServletHandler> clonedActiveServletHandlers = cloneActiveServletHandlerSet();
    for (CustomServletHandler servletHandler : clonedActiveServletHandlers) {
      updateServletHandlerWithDynamicSettings(servletHandler);
    }
    servletHolderManager.updatePrviousKeys(servletKeys);
    servletMappingManager.updatePrviousKeys(servletMappingKeys);
    filterHolderManager.updatePrviousKeys(filterKeys);
    filterMappingManager.updatePrviousKeys(filterMappingKeys);
  }

  private void updateServletHandlerWithDynamicSettings(final CustomServletHandler servletHandler) {

    ServletHolder[] servletHolders = servletHolderManager.generateUpgradedElementArray(servletKeys,
        servletHandler.getServlets());

    ServletMapping[] servletMappings = servletMappingManager.generateUpgradedElementArray(
        servletMappingKeys, servletHandler.getServletMappings());

    FilterHolder[] filterHolders = filterHolderManager.generateUpgradedElementArray(filterKeys,
        servletHandler.getFilters());

    FilterMapping[] filterMappings = filterMappingManager.generateUpgradedElementArray(
        filterMappingKeys, servletHandler.getFilterMappings());

    servletHandler.updateServletsAndFilters(servletHolders, servletMappings, filterHolders,
        filterMappings);
  }
}
