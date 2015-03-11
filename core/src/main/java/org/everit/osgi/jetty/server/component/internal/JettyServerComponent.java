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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Generated;

import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.ReferenceConfigurationType;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.component.ComponentContext;
import org.everit.osgi.ecm.component.ConfigurationException;
import org.everit.osgi.ecm.component.ServiceHolder;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.osgi.jetty.server.component.JettyServerConstants;
import org.everit.osgi.jetty.server.component.NetworkConnectorFactory;
import org.everit.osgi.jetty.server.component.ServletContextHandlerFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import aQute.bnd.annotation.headers.ProvideCapability;

@Component(componentId = "org.everit.osgi.jetty.server.component.JettyServer",
    configurationPolicy = ConfigurationPolicy.FACTORY)
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
public class JettyServerComponent {

  private static class ConnectorFactoryKey {

    public String host;

    public int port;

    public ServiceReference<NetworkConnectorFactory> serviceReference;

    public ConnectorFactoryKey(final ServiceReference<NetworkConnectorFactory> serviceReference,
        final String host, final int port) {
      this.serviceReference = serviceReference;
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
      if (host == null) {
        if (other.host != null) {
          return false;
        }
      } else if (!host.equals(other.host)) {
        return false;
      }
      if (port != other.port) {
        return false;
      }
      if (serviceReference == null) {
        if (other.serviceReference != null) {
          return false;
        }
      } else if (!serviceReference.equals(other.serviceReference)) {
        return false;
      }
      return true;
    }

    @Override
    @Generated("eclipse")
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((host == null) ? 0 : host.hashCode());
      result = prime * result + port;
      result = prime * result + ((serviceReference == null) ? 0 : serviceReference.hashCode());
      return result;
    }

  }

  private ComponentContext<JettyServerComponent> componentContext;

  @ServiceRef(setter = "setNetworkConnectorFactories",
      configurationType = ReferenceConfigurationType.CLAUSE, optional = false, dynamic = true)
  private ServiceHolder<NetworkConnectorFactory>[] networkConnectorFactories;

  private final HashMap<ConnectorFactoryKey, NetworkConnector> registeredConnectors =
      new HashMap<>();

  private Server server;

  private ServiceRegistration<Server> serviceRegistration;

  @ServiceRef(setter = "setServletContextHandlerFactories", optional = true)
  private ServiceHolder<ServletContextHandlerFactory>[] servletContextHandlerFactories;

  @Activate
  public void activate(final ComponentContext<JettyServerComponent> componentContext) {

    this.componentContext = componentContext;
    server = new Server();
    ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();

    server.setHandler(contextHandlerCollection);

    updateConnectorFactoriesOnServer(networkConnectorFactories);

    addServletContextsToServer(contextHandlerCollection);

    Dictionary<String, Object> serviceProps = new Hashtable<String, Object>(
        componentContext.getProperties());

    try {
      server.start();
    } catch (Exception e) {
      fail(e);
      return;
    }
    serviceRegistration = componentContext.registerService(Server.class, server, serviceProps);
  }

  private void addNewConnectors(
      final Map<ConnectorFactoryKey, NetworkConnectorFactory> newConnectors) {
    Set<Entry<ConnectorFactoryKey, NetworkConnectorFactory>> entrySet = newConnectors.entrySet();
    for (Entry<ConnectorFactoryKey, NetworkConnectorFactory> entry : entrySet) {
      NetworkConnectorFactory factory = entry.getValue();
      ConnectorFactoryKey factoryParams = entry.getKey();

      NetworkConnector connector = factory.createNetworkConnector(server, factoryParams.host,
          factoryParams.port);

      server.addConnector(connector);
      if (server.isStarted() && !connector.isStarted()) {
        try {
          connector.start();
        } catch (Exception e) {
          fail(e);
          return;
        }
      }
      registeredConnectors.put(factoryParams, connector);
    }
  }

  private void addServletContextsToServer(final ContextHandlerCollection contextHandlerCollection) {

    for (ServiceHolder<ServletContextHandlerFactory> holder : servletContextHandlerFactories) {
      Map<String, Object> attributes = holder.getAttributes();
      Object contextPath = attributes.get(JettyServerConstants.ATTR_CONTEXTPATH);

      if (contextPath == null) {
        throw new ConfigurationException("'" + JettyServerConstants.ATTR_CONTEXTPATH
            + "' attribute must be provided in clause");
      }

      contextHandlerCollection.addHandler(holder.getService().createHandler(
          contextHandlerCollection));
    }
  }

  @Deactivate
  public void deactivate() {
    if (serviceRegistration != null) {
      serviceRegistration.unregister();
    }

    if (server != null) {
      try {
        server.stop();
        server.destroy();
      } catch (Exception e) {
        // TODO
        throw new RuntimeException(e);
      }
    }

  }

  private void deleteConnectors(
      final HashMap<ConnectorFactoryKey, NetworkConnector> connectorsToDelete) {
    Set<Entry<ConnectorFactoryKey, NetworkConnector>> connectorToDeleteSet = connectorsToDelete
        .entrySet();
    for (Entry<ConnectorFactoryKey, NetworkConnector> connectorToDelete : connectorToDeleteSet) {
      registeredConnectors.remove(connectorToDelete.getKey());
      server.removeConnector(connectorToDelete.getValue());
    }
  }

  private void fail(final Throwable e) {

    if (server != null) {
      try {
        server.stop();
        server.destroy();
      } catch (Exception stopE) {
        e.addSuppressed(stopE);
      }
      componentContext.fail(e);
    }
  }

  private String resolveHostFromAttributes(final Map<String, Object> attributes) {
    Object hostValue = attributes.get(JettyServerConstants.ATTR_HOST);
    if (hostValue == null) {
      return null;
    }
    return String.valueOf(hostValue);
  }

  private int resolvePortFromAttributes(final String referenceId,
      final Map<String, Object> attributes) {

    Object portValue = attributes.get(JettyServerConstants.ATTR_PORT);
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

  public void setNetworkConnectorFactories(
      final ServiceHolder<NetworkConnectorFactory>[] networkConnectorFactories) {
    updateConnectorFactories(networkConnectorFactories);
  }

  public void setServletContextHandlerFactories(
      final ServiceHolder<ServletContextHandlerFactory>[] servletContextHandlerFactories) {
    this.servletContextHandlerFactories = servletContextHandlerFactories;
  }

  private synchronized void updateConnectorFactories(
      final ServiceHolder<NetworkConnectorFactory>[] pNetworkConnectorFactories) {
    if (server == null) {
      this.networkConnectorFactories = pNetworkConnectorFactories;
    } else {
      updateConnectorFactoriesOnServer(pNetworkConnectorFactories);
    }
  }

  private void updateConnectorFactoriesOnServer(
      final ServiceHolder<NetworkConnectorFactory>[] newNetworkConnectorFactories) {
    @SuppressWarnings("unchecked")
    HashMap<ConnectorFactoryKey, NetworkConnector> connectorsToDelete =
        (HashMap<ConnectorFactoryKey, NetworkConnector>) registeredConnectors.clone();

    Map<ConnectorFactoryKey, NetworkConnectorFactory> newConnectors = new HashMap<>();

    for (ServiceHolder<NetworkConnectorFactory> serviceHolder : newNetworkConnectorFactories) {
      NetworkConnectorFactory connectorFactory = serviceHolder.getService();
      Map<String, Object> attributes = serviceHolder.getAttributes();
      String host = resolveHostFromAttributes(attributes);
      int port = resolvePortFromAttributes(serviceHolder.getReferenceId(), attributes);

      ServiceReference<NetworkConnectorFactory> reference = serviceHolder.getReference();
      ConnectorFactoryKey factoryKey = new ConnectorFactoryKey(reference, host, port);

      if (connectorsToDelete.containsKey(factoryKey)) {
        connectorsToDelete.remove(factoryKey);
      } else {
        newConnectors.put(factoryKey, connectorFactory);
      }

    }

    deleteConnectors(connectorsToDelete);

    addNewConnectors(newConnectors);

  }
}
