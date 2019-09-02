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

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;

import org.eclipse.jetty.server.session.SessionHandler;
import org.everit.jetty.server.SessionHandlerFactory;
import org.everit.jetty.server.ecm.SessionHandlerFactoryConstants;
import org.everit.jetty.server.ecm.SessionHandlerConstants;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.IntegerAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.extender.ExtendComponent;
import org.osgi.framework.Constants;

/**
 * Configurable component that creates a {@link SessionHandler} instance.
 */
@ExtendComponent
@Component(componentId = SessionHandlerFactoryConstants.SERVICE_FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    label = "Everit Jetty Hash SessionHandler Factory")
@StringAttributes({
    @StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION, optional = true,
        priority = HashSessionHandlerFactoryAttributePriority.P01_SERVICE_DESCRIPTION,
        label = "Service description",
        description = "Optional description for SessionHandler Factory service.") })
@Service
public class SessionHandlerFactoryComponent implements SessionHandlerFactory {

  private boolean checkingRemoteSessionIdEncoding;

  private String cookieName;

  private boolean httpOnly;

  private int maxInactiveInterval;

  private boolean nodeIdInSessionId;

  private int refreshCookieAge;

  private boolean secureRequestOnly;

  private HttpSessionAttributeListener[] sessionAttributeListeners;

  private String sessionIdParameterName;

  private HttpSessionListener[] sessionListeners;

  private boolean usingCookies;

  @Override
  public synchronized SessionHandler createSessionHandler() {

    SessionHandler sessionHandler = new SessionHandler();
    sessionHandler.setCheckingRemoteSessionIdEncoding(this.checkingRemoteSessionIdEncoding);
    sessionHandler.setHttpOnly(this.httpOnly);
    sessionHandler.setMaxInactiveInterval(this.maxInactiveInterval);
    sessionHandler.setNodeIdInSessionId(this.nodeIdInSessionId);
    sessionHandler.setRefreshCookieAge(this.refreshCookieAge);
    sessionHandler.setSecureRequestOnly(this.secureRequestOnly);
    // TODO sessionHandler.setSessionCache(cache);
    sessionHandler.setSessionCookie(this.cookieName);
    // TODO sessionHandler.setSessionIdManager(metaManager);
    sessionHandler.setSessionIdPathParameterName(this.sessionIdParameterName);
    sessionHandler.setUsingCookies(this.usingCookies);

    sessionHandler.clearEventListeners();
    if (this.sessionAttributeListeners != null) {
      for (HttpSessionAttributeListener sessionAttributeListener : this.sessionAttributeListeners) {
        sessionHandler.addEventListener(sessionAttributeListener);
      }
    }

    if (this.sessionListeners != null) {
      for (HttpSessionListener sessionListener : this.sessionListeners) {
        sessionHandler.addEventListener(sessionListener);
      }
    }

    // TODO handle more listener types

    return sessionHandler;
  }

  @BooleanAttribute(attributeId = SessionHandlerConstants.ATTR_CHECKING_REMOTE_SESSION_ID_ENCODING,
      defaultValue = SessionHandlerConstants.DEFAULT_CHECKING_REMOTE_SESSION_ID_ENCODING,
      priority = HashSessionHandlerFactoryAttributePriority.P12_CHECKING_REMOTE_SESSION_ID_ENCODING,
      label = "Checking remote session id encoding",
      description = "True if absolute URLs are check for remoteness before being session encoded.")
  public void setCheckingRemoteSessionIdEncoding(final boolean checkingRemoteSessionIdEncoding) {
    this.checkingRemoteSessionIdEncoding = checkingRemoteSessionIdEncoding;
  }

  @StringAttribute(attributeId = SessionHandlerConstants.ATTR_COOKIE_NAME,
      defaultValue = SessionHandler.__DefaultSessionCookie,
      priority = HashSessionHandlerFactoryAttributePriority.P08_COOKIE_NAME, label = "Cookie name",
      description = "The name of the cookie.")
  public void setCookieName(final String cookieName) {
    this.cookieName = cookieName;
  }

  @BooleanAttribute(attributeId = SessionHandlerConstants.ATTR_HTTP_ONLY,
      defaultValue = SessionHandlerConstants.DEFAULT_HTTP_ONLY,
      priority = HashSessionHandlerFactoryAttributePriority.P16_HTTP_ONLY, label = "HTTP only",
      description = "")
  public void setHttpOnly(final boolean httpOnly) {
    this.httpOnly = httpOnly;
  }

  /**
   * Sets the session-timeout on the component and on all referenced session managers.
   */
  @IntegerAttribute(attributeId = SessionHandlerConstants.ATTR_MAX_INACTIVE_INTERVAL,
      defaultValue = SessionHandlerConstants.DEFAULT_MAX_INACTIVE_INTERVAL, dynamic = true,
      priority = HashSessionHandlerFactoryAttributePriority.P02_MAX_INACTIVE_INTERVAL,
      label = "Max inactive interval",
      description = "The max period of inactivity, after which the session is invalidated, "
          + "in seconds.")
  public synchronized void setMaxInactiveInterval(final int maxInactiveInterval) {
    this.maxInactiveInterval = maxInactiveInterval;
  }

  @BooleanAttribute(attributeId = SessionHandlerConstants.ATTR_NODE_IN_SESSION_ID,
      defaultValue = SessionHandlerConstants.DEFAULT_NODE_IN_SESSION_ID,
      priority = HashSessionHandlerFactoryAttributePriority.P18_NODE_IN_SESSION_ID,
      label = "Node in session id",
      description = "Wether the cluster node id (worker id) will be returned as part of the "
          + "session id by HttpSession.getId() or not.")
  public void setNodeIdInSessionId(final boolean nodeIdInSessionId) {
    this.nodeIdInSessionId = nodeIdInSessionId;
  }

  @IntegerAttribute(attributeId = SessionHandlerConstants.ATTR_REFRESH_COOKIE_AGE,
      defaultValue = SessionHandlerConstants.DEFAULT_REFRESH_COOKIE_AGE,
      priority = HashSessionHandlerFactoryAttributePriority.P19_REFRESH_COOKIE_AGE,
      label = "Refresh cookie age",
      description = "Time before a session cookie is re-set in seconds.")
  public void setRefreshCookieAge(final int refreshCookieAge) {
    this.refreshCookieAge = refreshCookieAge;
  }

  @BooleanAttribute(attributeId = SessionHandlerConstants.ATTR_SECURE_REQUEST_ONLY,
      defaultValue = SessionHandlerConstants.DEFAULT_SECURE_REQUEST_ONLY,
      priority = HashSessionHandlerFactoryAttributePriority.P06_SECURE_REQUEST_ONLY,
      label = "Secure request only",
      description = "HTTPS request. Can be overridden by setting "
          + "SessionCookieConfig.setSecure(true), in which case the session cookie will be marked "
          + "as secure on both HTTPS and HTTP.")
  public void setSecureRequestOnly(final boolean secureRequestOnly) {
    this.secureRequestOnly = secureRequestOnly;
  }

  @ServiceRef(referenceId = SessionHandlerConstants.ATTR_SESSION_ATTRIBUTE_LISTENERS,
      optional = true,
      attributePriority = HashSessionHandlerFactoryAttributePriority.P04_SESSION_ATTRIBUTE_LISTENERS, // CS_DISABLE_LINE_LENGTH
      label = "Session attribute listeners (target)",
      description = "Zero or more filter expression for HttpSessionAttributeListener services")
  public void setSessionAttributeListeners(
      final HttpSessionAttributeListener[] sessionAttributeListeners) {
    this.sessionAttributeListeners = sessionAttributeListeners;
  }

  @StringAttribute(attributeId = SessionHandlerConstants.SESSION_ID_PARAMETER_NAME,
      defaultValue = SessionHandler.__DefaultSessionIdPathParameterName,
      priority = HashSessionHandlerFactoryAttributePriority.P10_SESSION_ID_PARAMETER_NAME,
      label = "Session id parameter name",
      description = "The URL path parameter name for session id URL rewriting "
          + "(\"none\" for no rewriting).")
  public void setSessionIdParameterName(final String sessionIdParameterName) {
    this.sessionIdParameterName = sessionIdParameterName;
  }

  @ServiceRef(referenceId = SessionHandlerConstants.ATTR_SESSION_LISTENERS, optional = true,
      attributePriority = HashSessionHandlerFactoryAttributePriority.P03_SESSION_LISTENERS,
      label = "Session listeners (target)",
      description = "Zero or more filter expression for HttpSessionListener services.")
  public void setSessionListeners(final HttpSessionListener[] sessionListeners) {
    this.sessionListeners = sessionListeners;
  }

  @BooleanAttribute(attributeId = SessionHandlerConstants.ATTR_USING_COOKIES,
      defaultValue = SessionHandlerConstants.DEFAULT_USING_COOKIES,
      priority = HashSessionHandlerFactoryAttributePriority.P07_USING_COOKIES,
      label = "Using cookies")
  public void setUsingCookies(final boolean usingCookies) {
    this.usingCookies = usingCookies;
  }

}
