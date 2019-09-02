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

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.everit.jetty.server.ConnectionFactoryFactory;
import org.everit.jetty.server.NetworkConnectorFactory;
import org.everit.jetty.server.ReferencedEndPointsCloseable;
import org.everit.jetty.server.ecm.ServerConnectorFactoryConstants;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.Update;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.IntegerAttribute;
import org.everit.osgi.ecm.annotation.attribute.LongAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.extender.ExtendComponent;
import org.osgi.framework.Constants;

/**
 * ECM based configurable component that can set up and register {@link NetworkConnectorFactory}s.
 *
 */
@ExtendComponent
@Component(componentId = ServerConnectorFactoryConstants.SERVICE_FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    label = "Everit Jetty Server Connector Factory")
@StringAttributes({
    @StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION, optional = true,
        priority = ServerConnectorFactoryAttributePriority.P01_SERVICE_DESCRIPTION,
        label = "Service description",
        description = "Optional description for Server Connector Factory instance.") })
@Service
public class ServerConnectorFactoryComponent implements NetworkConnectorFactory {

  /**
   * Default implementation of ConnectionFactory for the simplest HTTP calls.
   */
  private static class DefaultConnectionFactoryFactory implements ConnectionFactoryFactory {

    @Override
    public ConnectionFactory createConnectionFactory(final String nextProtocol) {
      return new CustomHttpConnectionFactory(new HttpConfiguration());
    }

  }

  private int acceptorPriorityDelta;

  private int acceptQueueSize;

  private boolean closeEndpointsAfterDynamicUpdate;

  private ConnectionFactoryFactory[] connectionFactoryFactories;

  private long idleTimeout = ServerConnectorFactoryConstants.DEFAULT_IDLE_TIMEOUT;

  private boolean inheritChannel;

  private String name;

  private final WeakHashMap<ServerConnector, Boolean> providedConnectors =
      new WeakHashMap<>();

  private boolean reuseAddress;

  private boolean updateConnectionFactories = false;

  @Activate
  public void activate() {
    this.closeEndpointsAfterDynamicUpdate = false;
    this.updateConnectionFactories = false;
  }

  private synchronized Set<ServerConnector> activeServerConnectors() {
    Set<ServerConnector> result = null;
    while (result == null) {
      try {
        result = new HashSet<>(this.providedConnectors.keySet());
      } catch (ConcurrentModificationException e) {
        // TODO probably some warn logging would be nice
      }
    }
    return result;
  }

  @Override
  public ServerConnector createNetworkConnector(final Server server, final String host,
      final int port) {

    ServerConnector result = new ServerConnector(server);

    Collection<ConnectionFactory> connectionFactories = generateConnectionFactories();
    result.setConnectionFactories(connectionFactories);
    result.setDefaultProtocol(connectionFactories.iterator().next().getProtocol());
    result.setAcceptorPriorityDelta(this.acceptorPriorityDelta);
    result.setAcceptQueueSize(this.acceptQueueSize);
    result.setIdleTimeout(this.idleTimeout);
    result.setInheritChannel(this.inheritChannel);
    result.setName(this.name);
    result.setReuseAddress(this.reuseAddress);
    result.setHost(host);
    result.setPort(port);
    putIntoProvidedConnectors(result);
    return result;
  }

  private Collection<ConnectionFactory> generateConnectionFactories() {
    int n = this.connectionFactoryFactories.length;
    ConnectionFactory[] result = new ConnectionFactory[n];
    String nextProtocol = null;
    for (int i = n - 1; i >= 0; i--) {
      ConnectionFactoryFactory connectionFactoryFactory = this.connectionFactoryFactories[i];

      ConnectionFactory connectionFactory = connectionFactoryFactory
          .createConnectionFactory(nextProtocol);

      result[i] = connectionFactory;
      nextProtocol = connectionFactory.getProtocol();
    }
    return Arrays.asList(result);
  }

  private synchronized void putIntoProvidedConnectors(final ServerConnector result) {
    this.providedConnectors.put(result, Boolean.TRUE);
  }

  /**
   * Setter that also updates the property on the connector without restarting it.
   */
  @IntegerAttribute(attributeId = ServerConnectorFactoryConstants.ATTR_ACCEPTOR_PRIORITY_DELTA,
      defaultValue = 0, dynamic = true,
      priority = ServerConnectorFactoryAttributePriority.P09_ACCEPTOR_PRIORITY_DELTA,
      label = "Acceptor thread priority delta",
      description = "This allows the acceptor thread to run at a different priority. Typically "
          + "this would be used to lower the priority to give preference to handling previously "
          + "accepted connections rather than accepting new connections.")
  public synchronized void setAcceptorPriorityDelta(final int acceptorPriorityDelta) {
    this.acceptorPriorityDelta = acceptorPriorityDelta;
    for (ServerConnector serverConnector : activeServerConnectors()) {
      serverConnector.setAcceptorPriorityDelta(acceptorPriorityDelta);
    }

  }

  /**
   * Setter that also updates the property on the connector without restarting it.
   */
  @IntegerAttribute(attributeId = ServerConnectorFactoryConstants.ATTR_ACCEPT_QUEUE_SIZE,
      defaultValue = 0, priority = ServerConnectorFactoryAttributePriority.P06_ACCEPT_QUEUE_SIZE,
      label = "Accept queue size",
      description = "The accept queue size (also known as accept backlog).")
  public synchronized void setAcceptQueueSize(final int acceptQueueSize) {
    this.acceptQueueSize = acceptQueueSize;
  }

  /**
   * Setter that also updates the property on the connector without restarting it.
   */
  @ServiceRef(
      referenceId = ServerConnectorFactoryConstants.ATTR_CONNECTION_FACTORY_FACTORIES,
      dynamic = true, optional = true,
      attributePriority = ServerConnectorFactoryAttributePriority.P02_CONNECTION_FACTORY_FACTORIES,
      label = "ConnectionFactory factories (target)",
      description = "OSGi filter expressions that point to OSGi services that implement the "
          + "ConnectionFactoryFactory interface. In case no service reference is specified, a "
          + "standard HttpConnectionFactory is used.")
  public synchronized void setConnectionFactoryFactories(
      final ConnectionFactoryFactory[] connectionFactoryFactories) {

    if (connectionFactoryFactories == null || connectionFactoryFactories.length == 0) {
      this.connectionFactoryFactories =
          new ConnectionFactoryFactory[] { new DefaultConnectionFactoryFactory() };
    } else {
      this.connectionFactoryFactories = connectionFactoryFactories.clone();
    }

    this.updateConnectionFactories = true;
    this.closeEndpointsAfterDynamicUpdate = true;
  }

  /**
   * Setter that also updates the property on the connector without restarting it.
   */
  @LongAttribute(attributeId = ServerConnectorFactoryConstants.ATTR_IDLE_TIMEOUT,
      defaultValue = ServerConnectorFactoryConstants.DEFAULT_IDLE_TIMEOUT, dynamic = true,
      priority = ServerConnectorFactoryAttributePriority.P03_IDLE_TIMEOUT, label = "Idle timeout",
      description = "Sets the maximum Idle time for a connection. This value is interpreted as "
          + "the maximum time between some progress being made on the connection. So if a single "
          + "byte is read or written, then the timeout is reset.")
  public synchronized void setIdleTimeout(final long idleTimeout) {
    this.idleTimeout = idleTimeout;
    for (ServerConnector serverConnector : activeServerConnectors()) {
      serverConnector.setIdleTimeout(idleTimeout);
    }
    this.closeEndpointsAfterDynamicUpdate = true;

  }

  @BooleanAttribute(attributeId = ServerConnectorFactoryConstants.ATTR_INHERIT_CHANNEL,
      defaultValue = ServerConnectorFactoryConstants.DEFAULT_INHERIT_CHANNEL,
      priority = ServerConnectorFactoryAttributePriority.P07_INHERIT_CHANNEL,
      label = "Inherit channel",
      description = "Whether this connector uses a channel inherited from the JVM. If true, the "
          + "connector first tries to inherit from a channel provided by the system. If there is "
          + "no inherited channel available, or if the inherited channel is not usable, then it "
          + "will fall back using ServerSocketChannel. Use it with xinetd/inetd, to launch an "
          + "instance of Jetty on demand. The port used to access pages on the Jetty instance is "
          + "the same as the port used to launch Jetty.")
  public void setInheritChannel(final boolean inheritChannel) {
    this.inheritChannel = inheritChannel;
  }

  @StringAttribute(attributeId = ServerConnectorFactoryConstants.ATTR_NAME, optional = true,
      priority = ServerConnectorFactoryAttributePriority.P04_NAME, label = "Name",
      description = "Set a connector name. A context may be configured with virtual hosts in the "
          + "form \"@contextname\" and will only serve requests from the named connector.")
  public void setName(final String name) {
    this.name = name;
  }

  @BooleanAttribute(attributeId = ServerConnectorFactoryConstants.ATTR_REUSE_ADDRESS,
      defaultValue = ServerConnectorFactoryConstants.DEFAULT_REUSE_ADDRESS,
      priority = ServerConnectorFactoryAttributePriority.P05_REUSE_ADDRESS, label = "Reuse address",
      description = "Whether the server socket reuses addresses.")
  public void setReuseAddress(final boolean reuseAddress) {
    this.reuseAddress = reuseAddress;
  }

  /**
   * Updates all connection factories if necessary and closes all endpoints if necessary.
   */
  @Update
  public void update() {
    if (this.closeEndpointsAfterDynamicUpdate) {
      for (ServerConnector serverConnector : activeServerConnectors()) {
        Collection<ConnectionFactory> previousConnectionFactories = new HashSet<>(
            serverConnector.getConnectionFactories());

        if (this.updateConnectionFactories) {
          Collection<ConnectionFactory> connectionFactories = generateConnectionFactories();
          serverConnector.setConnectionFactories(connectionFactories);
          serverConnector.setDefaultProtocol(connectionFactories.iterator().next().getProtocol());
        }

        // Closing all endpoints
        for (ConnectionFactory connectionFactory : previousConnectionFactories) {

          if (connectionFactory instanceof ReferencedEndPointsCloseable) {
            ((ReferencedEndPointsCloseable) connectionFactory).closeReferencedEndpoints();
          }
        }
      }
    }

    this.closeEndpointsAfterDynamicUpdate = false;
    this.updateConnectionFactories = false;
  }
}
