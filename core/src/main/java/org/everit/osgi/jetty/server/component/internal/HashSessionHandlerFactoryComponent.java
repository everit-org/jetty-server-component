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

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;

import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.everit.osgi.ecm.annotation.AttributeOrder;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.IntegerAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.osgi.jetty.server.SessionHandlerFactory;
import org.everit.osgi.jetty.server.component.HashSessionHandlerFactoryConstants;
import org.everit.osgi.jetty.server.component.SessionHandlerConstants;
import org.osgi.framework.Constants;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * Configurable component that creates a {@link SessionHandler} based on {@link HashSessionManager}
 * implementation.
 */
@Component(componentId = HashSessionHandlerFactoryConstants.FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    localizationBase = "OSGI-INF/metatype/sessionHandlerFactory")
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@StringAttributes({
    @StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION, optional = true) })
@AttributeOrder({ SessionHandlerConstants.ATTR_MAX_INACTIVE_INTERVAL,
    SessionHandlerConstants.SERVICE_REF_SESSION_LISTENERS + ".target",
    SessionHandlerConstants.SERVICE_REF_SESSION_ATTRIBUTE_LISTENERS + ".target",
    SessionHandlerConstants.SERVICE_REF_SESSION_ID_LISTENERS + ".target",
    Constants.SERVICE_DESCRIPTION, })
@Service
public class HashSessionHandlerFactoryComponent implements SessionHandlerFactory {

  private int maxInactiveInterval;

  private final WeakHashMap<HashSessionManager, Boolean> referencedSessionManagers =
      new WeakHashMap<>();

  private HttpSessionAttributeListener[] sessionAttributeListeners;

  private HttpSessionIdListener[] sessionIdListeners;

  private HttpSessionListener[] sessionListeners;

  private void addListeners(final HashSessionManager sessionManager) {
    for (HttpSessionListener sessionListener : sessionListeners) {
      sessionManager.addEventListener(sessionListener);
    }

    for (HttpSessionAttributeListener sessionAttributeListener : sessionAttributeListeners) {
      sessionManager.addEventListener(sessionAttributeListener);
    }

    for (HttpSessionIdListener sessionIdListener : sessionIdListeners) {
      sessionManager.addEventListener(sessionIdListener);
    }
  }

  private synchronized Set<HashSessionManager> cloneReferencedConnectionFactories() {
    Set<HashSessionManager> result = null;
    while (result == null) {
      try {
        result = new HashSet<>(referencedSessionManagers.keySet());
      } catch (ConcurrentModificationException e) {
        // TODO probably some warn logging would be nice
      }
    }
    return result;
  }

  @Override
  public synchronized SessionHandler createSessionHandler() {
    HashSessionManager sessionManager = new HashSessionManager();

    sessionManager.setMaxInactiveInterval(maxInactiveInterval);

    addListeners(sessionManager);

    // TODO add more configuration possibilities (also for the id manager)

    SessionHandler sessionHandler = new SessionHandler(sessionManager);

    return sessionHandler;
  }

  /**
   * Sets the session-timeout on the component and on all referenced session managers.
   */
  @IntegerAttribute(attributeId = SessionHandlerConstants.ATTR_MAX_INACTIVE_INTERVAL,
      defaultValue = SessionHandlerConstants.DEFAULT_MAX_INACTIVE_INTERVAL, dynamic = true)
  public synchronized void setMaxInactiveInterval(final int maxInactiveInterval) {
    this.maxInactiveInterval = maxInactiveInterval;
    for (HashSessionManager sessionManager : cloneReferencedConnectionFactories()) {
      sessionManager.setMaxInactiveInterval(maxInactiveInterval);
    }
  }

  @ServiceRef(referenceId = SessionHandlerConstants.SERVICE_REF_SESSION_ATTRIBUTE_LISTENERS)
  public void setSessionAttributeListeners(
      final HttpSessionAttributeListener[] sessionAttributeListeners) {
    this.sessionAttributeListeners = sessionAttributeListeners;
  }

  @ServiceRef(referenceId = SessionHandlerConstants.SERVICE_REF_SESSION_ID_LISTENERS)
  public void setSessionIdListeners(final HttpSessionIdListener[] sessionIdListeners) {
    this.sessionIdListeners = sessionIdListeners;
  }

  @ServiceRef(referenceId = SessionHandlerConstants.SERVICE_REF_SESSION_LISTENERS)
  public void setSessionListeners(final HttpSessionListener[] sessionListeners) {
    this.sessionListeners = sessionListeners;
  }

}
