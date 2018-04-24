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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Generated;

import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.everit.jetty.server.NetworkConnectorFactory;
import org.everit.jetty.server.ServletContextHandlerFactory;
import org.everit.jetty.server.ecm.JettyServerConstants;
import org.everit.jetty.server.ecm.JettyServerException;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.ManualService;
import org.everit.osgi.ecm.annotation.ManualServices;
import org.everit.osgi.ecm.annotation.ReferenceConfigurationType;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.component.ComponentContext;
import org.everit.osgi.ecm.component.ConfigurationException;
import org.everit.osgi.ecm.component.ServiceHolder;
import org.everit.osgi.ecm.extender.ExtendComponent;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * ECM based configurable component that can start one or more Jetty {@link Server}s.
 */
@ExtendComponent
@Component(componentId = JettyServerConstants.SERVICE_FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    label = "Everit Jetty Server",
    description = "Configurable component that can start Jetty server instances. In case a Jetty "
        + "server is started, the server instance is registered as an OSGi service.")
@StringAttributes({
    @StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION, optional = true,
        priority = JettyServerComponent.P01_SERVICE_DESCRIPTION, label = "Service description",
        description = "Optional description for the instantiated Jetty server.") })
@ManualServices(@ManualService({ Server.class }))
public class JettyServerComponent {

  /**
   * Helper class to identify connector references with their settings in hash based collections.
   */
  private static class ConnectorFactoryKey {

    public final String connectorId;

    public final String host;

    public final int port;

    public final ServiceReference<NetworkConnectorFactory> serviceReference;

    ConnectorFactoryKey(final ServiceHolder<NetworkConnectorFactory> serviceHolder,
        final String host, final int port) {
      this.serviceReference = serviceHolder.getReference();
      this.connectorId = serviceHolder.getReferenceId();
      this.host = host;
      this.port = port;
    }

    @Override
    @Generated("eclipse")
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      ConnectorFactoryKey other = (ConnectorFactoryKey) obj;
      if (this.connectorId == null) {
        if (other.connectorId != null) {
          return false;
        }
      } else if (!this.connectorId.equals(other.connectorId)) {
        return false;
      }
      if (this.host == null) {
        if (other.host != null) {
          return false;
        }
      } else if (!this.host.equals(other.host)) {
        return false;
      }
      if (this.port != other.port) {
        return false;
      }
      if (this.serviceReference == null) {
        if (other.serviceReference != null) {
          return false;
        }
      } else if (!this.serviceReference.equals(other.serviceReference)) {
        return false;
      }
      return true;
    }

    @Override
    @Generated("eclipse")
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + ((this.connectorId == null) ? 0 : this.connectorId.hashCode());
      result = (prime * result) + ((this.host == null) ? 0 : this.host.hashCode());
      result = (prime * result) + this.port;
      result = (prime * result)
          + ((this.serviceReference == null) ? 0 : this.serviceReference.hashCode());
      return result;
    }

  }

  /**
   * Container class that holds a context handler and the original context path that was used to
   * create or update the handler.
   *
   */
  private static class ContextWithPath {

    public final String contextPath;

    public final ServletContextHandler handler;

    ContextWithPath(final ServletContextHandler handler,
        final String contextPath) {
      this.handler = handler;
      this.contextPath = contextPath;
    }

  }

  /**
   * Helper class to identify contexts with their settings in hash based collections.
   */
  private static class ServletContextFactoryKey {

    public final String contextId;

    public final ServiceReference<ServletContextHandlerFactory> serviceReference;

    ServletContextFactoryKey(
        final ServiceHolder<ServletContextHandlerFactory> serviceHolder) {
      this.serviceReference = serviceHolder.getReference();
      this.contextId = serviceHolder.getReferenceId();
    }

    @Override
    @Generated("eclipse")
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      ServletContextFactoryKey other = (ServletContextFactoryKey) obj;
      if (this.contextId == null) {
        if (other.contextId != null) {
          return false;
        }
      } else if (!this.contextId.equals(other.contextId)) {
        return false;
      }
      if (this.serviceReference == null) {
        if (other.serviceReference != null) {
          return false;
        }
      } else if (!this.serviceReference.equals(other.serviceReference)) {
        return false;
      }
      return true;
    }

    @Override
    @Generated("eclipse")
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + ((this.contextId == null) ? 0 : this.contextId.hashCode());
      result = (prime * result)
          + ((this.serviceReference == null) ? 0 : this.serviceReference.hashCode());
      return result;
    }

  }

  public static final int P01_SERVICE_DESCRIPTION = 1;

  public static final int P02_NETWORK_CONNECTOR_FACTORIES = 2;

  public static final int P03_SERVLET_CONTEXT_HANDLER_FACTORIES = 3;

  private CustomContextHandlerCollection contextHandlerCollection;

  private ServiceHolder<NetworkConnectorFactory>[] networkConnectorFactories;

  private final HashMap<ConnectorFactoryKey, NetworkConnector> registeredConnectors =
      new HashMap<>();

  private final HashMap<ServletContextFactoryKey, ContextWithPath> registeredServletContexts =
      new HashMap<>();

  private Server server;

  private ServiceRegistration<Server> serviceRegistration;

  private ServiceHolder<ServletContextHandlerFactory>[] servletContextHandlerFactories;

  /**
   * Activate method of the component that sets up and starts a server.
   */
  @Activate
  public void activate(final ComponentContext<JettyServerComponent> componentContext) {
    this.server = new Server();
    this.contextHandlerCollection = new CustomContextHandlerCollection();

    this.server.setHandler(this.contextHandlerCollection);

    updateConnectorFactoriesOnServer();

    updateServletContextHandlerFactoriesOnServer();

    Dictionary<String, Object> serviceProps = new Hashtable<>(
        componentContext.getProperties());

    try {
      this.server.start();
    } catch (Exception e) {
      fail(e);
      return;
    }
    this.serviceRegistration =
        componentContext.registerService(Server.class, this.server, serviceProps);
  }

  private void addNewConnectors(
      final Map<ConnectorFactoryKey, NetworkConnectorFactory> newConnectors) {
    Set<Entry<ConnectorFactoryKey, NetworkConnectorFactory>> entrySet = newConnectors.entrySet();
    for (Entry<ConnectorFactoryKey, NetworkConnectorFactory> entry : entrySet) {
      NetworkConnectorFactory factory = entry.getValue();
      ConnectorFactoryKey factoryParams = entry.getKey();

      NetworkConnector connector = factory.createNetworkConnector(this.server, factoryParams.host,
          factoryParams.port);

      this.server.addConnector(connector);
      if (this.server.isStarted() && !connector.isStarted()) {
        try {
          connector.start();
          this.server.manage(connector);
        } catch (Exception e) {
          fail(e);
          return;
        }
      }
      this.registeredConnectors.put(factoryParams, connector);
    }
  }

  /**
   * Deactivate method that stops the server if it is running.
   */
  @Deactivate
  public void deactivate() {
    if (this.serviceRegistration != null) {
      this.serviceRegistration.unregister();
    }

    if ((this.server != null) && !this.server.isStopped()) {
      try {
        this.server.stop();
        this.server.destroy();
      } catch (Exception e) {
        throw new JettyServerException(e);
      }
    }

  }

  private void deleteConnectors(
      final HashMap<ConnectorFactoryKey, NetworkConnector> connectorsToDelete) {
    Set<Entry<ConnectorFactoryKey, NetworkConnector>> connectorToDeleteSet = connectorsToDelete
        .entrySet();
    for (Entry<ConnectorFactoryKey, NetworkConnector> connectorToDelete : connectorToDeleteSet) {
      this.registeredConnectors.remove(connectorToDelete.getKey());
      this.server.removeConnector(connectorToDelete.getValue());
    }
  }

  private void fail(final Throwable e) {
    try {
      deactivate();
    } catch (RuntimeException re) {
      e.addSuppressed(re);
    }

    if (e instanceof RuntimeException) {
      throw (RuntimeException) e;
    }
    throw new JettyServerException(e);
  }

  private String resolveContextPath(final ServiceHolder<ServletContextHandlerFactory> holder) {
    Map<String, Object> attributes = holder.getAttributes();
    Object contextPath = attributes.get(JettyServerConstants.CONTEXT_CLAUSE_ATTR_CONTEXTPATH);

    if (contextPath == null) {
      throw new ConfigurationException("'" + JettyServerConstants.CONTEXT_CLAUSE_ATTR_CONTEXTPATH
          + "' attribute must be provided in clause of ServletContextHandlerFactory");
    }
    return String.valueOf(contextPath);
  }

  private String resolveHostFromAttributes(final Map<String, Object> attributes) {
    Object hostValue = attributes.get(JettyServerConstants.CONNECTOR_REF_CLAUSE_ATTR_HOST);
    if (hostValue == null) {
      return null;
    }
    return String.valueOf(hostValue);
  }

  private int resolvePortFromAttributes(final String referenceId,
      final Map<String, Object> attributes) {

    Object portValue = attributes.get(JettyServerConstants.CONNECTOR_REF_CLAUSE_ATTR_PORT);
    if (portValue == null) {
      return 0;
    }

    try {
      return Integer.parseInt(String.valueOf(portValue));
    } catch (NumberFormatException e) {
      throw new ConfigurationException("Invalid value for connector port of reference: "
          + referenceId, e);
    }
  }

  private void setAndManageNewHandlers(final ServletContextHandler[] newHandlers) {
    this.contextHandlerCollection.setHandlers(newHandlers);
    for (ServletContextHandler newHandler : newHandlers) {
      if (!newHandler.isStarted() && this.server.isStarted()) {
        try {
          newHandler.start();
          this.contextHandlerCollection.manage(newHandler);
        } catch (Exception e) {
          throw new JettyServerException(e);
        }
      }
    }
  }

  @ServiceRef(referenceId = JettyServerConstants.ATTR_NETWORK_CONNECTOR_FACTORIES,
      configurationType = ReferenceConfigurationType.CLAUSE, optional = false, dynamic = true,
      attributePriority = P02_NETWORK_CONNECTOR_FACTORIES,
      label = "NetworkConnector Factories (clause)",
      description = "Zero or more clauses to install Network Connectors based on their factory "
          + "services. Supported attributes: host, port.")
  public void setNetworkConnectorFactories(
      final ServiceHolder<NetworkConnectorFactory>[] networkConnectorFactories) {
    updateConnectorFactories(networkConnectorFactories);
  }

  @ServiceRef(referenceId = JettyServerConstants.ATTR_SERVLET_CONTEXT_HANDLER_FACTORIES,
      configurationType = ReferenceConfigurationType.CLAUSE, dynamic = true,
      attributePriority = P03_SERVLET_CONTEXT_HANDLER_FACTORIES,
      label = "ServletContextHandler Factories (clause)",
      description = "Zero or more clauses to install Servlet Contexts based on their factory "
          + "services. Supported attributes: contextPath")
  public void setServletContextHandlerFactories(
      final ServiceHolder<ServletContextHandlerFactory>[] servletContextHandlerFactories) {
    updateServletContextAndHandleFailure(servletContextHandlerFactories);
  }

  private synchronized void updateConnectorFactories(
      final ServiceHolder<NetworkConnectorFactory>[] pNetworkConnectorFactories) {
    this.networkConnectorFactories = pNetworkConnectorFactories;
    if (this.server != null) {
      updateConnectorFactoriesOnServer();
    }
  }

  private void updateConnectorFactoriesOnServer() {
    @SuppressWarnings("unchecked")
    HashMap<ConnectorFactoryKey, NetworkConnector> connectorsToDelete =
        (HashMap<ConnectorFactoryKey, NetworkConnector>) this.registeredConnectors.clone();

    Map<ConnectorFactoryKey, NetworkConnectorFactory> newConnectors = new HashMap<>();

    for (ServiceHolder<NetworkConnectorFactory> serviceHolder : this.networkConnectorFactories) {
      NetworkConnectorFactory connectorFactory = serviceHolder.getService();
      Map<String, Object> attributes = serviceHolder.getAttributes();
      String host = resolveHostFromAttributes(attributes);
      int port = resolvePortFromAttributes(serviceHolder.getReferenceId(), attributes);

      ConnectorFactoryKey factoryKey = new ConnectorFactoryKey(serviceHolder, host, port);

      if (connectorsToDelete.containsKey(factoryKey)) {
        connectorsToDelete.remove(factoryKey);
      } else {
        newConnectors.put(factoryKey, connectorFactory);
      }

    }

    deleteConnectors(connectorsToDelete);

    addNewConnectors(newConnectors);

  }

  private void updateServletContextAndHandleFailure(
      final ServiceHolder<ServletContextHandlerFactory>[] pServletContextHandlerFactories) {

    this.servletContextHandlerFactories = pServletContextHandlerFactories;
    if (this.server != null) {
      updateServletContextHandlerFactoriesOnServer();
    }
  }

  private void updateServletContextHandlerFactoriesOnServer() {
    Set<ServletContextFactoryKey> unregisteredContexts = new HashSet<>(
        this.registeredServletContexts.keySet());

    ServletContextHandler[] newHandlers =
        new ServletContextHandler[this.servletContextHandlerFactories.length];

    this.contextHandlerCollection.setMapContextsCallIgnored(true);
    for (int i = 0; i < this.servletContextHandlerFactories.length; i++) {
      ServiceHolder<ServletContextHandlerFactory> holder = this.servletContextHandlerFactories[i];
      String contextPath = resolveContextPath(holder);
      ServletContextFactoryKey factoryKey = new ServletContextFactoryKey(holder);

      ContextWithPath contextWithPath = this.registeredServletContexts.get(factoryKey);
      if (contextWithPath != null) {
        if (!contextPath.equals(contextWithPath.contextPath)) {
          contextWithPath.handler.setContextPath(contextPath);
          this.registeredServletContexts.put(factoryKey,
              new ContextWithPath(contextWithPath.handler,
                  contextPath));
        }
        unregisteredContexts.remove(factoryKey);
        newHandlers[i] = contextWithPath.handler;
      } else {
        ServletContextHandler handler =
            holder.getService().createHandler(this.contextHandlerCollection,
                contextPath);

        newHandlers[i] = handler;
        this.registeredServletContexts.put(factoryKey, new ContextWithPath(handler, contextPath));
      }
    }

    // Remove handlers that are not in the new configuration
    for (ServletContextFactoryKey key : unregisteredContexts) {
      this.registeredServletContexts.remove(key);
    }

    // Set mapContext function back as setHandlers will call it
    this.contextHandlerCollection.setMapContextsCallIgnored(false);
    setAndManageNewHandlers(newHandlers);
  }
}
