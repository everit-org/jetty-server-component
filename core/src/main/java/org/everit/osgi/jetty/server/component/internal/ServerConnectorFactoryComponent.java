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

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.everit.osgi.ecm.annotation.AttributeOrder;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.IntegerAttribute;
import org.everit.osgi.ecm.annotation.attribute.LongAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.osgi.jetty.server.component.NetworkConnectorFactory;
import org.everit.osgi.jetty.server.component.ServerConnectorFactoryConstants;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * ECM based configurable component that can set up and register {@link NetworkConnectorFactory}s.
 *
 */
@Component(componentId = "org.everit.osgi.jetty.server.component.ServerConnectorFactory",
    configurationPolicy = ConfigurationPolicy.FACTORY,
    localizationBase = "OSGI-INF/metatype/serverConnectorFactory")
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@AttributeOrder({ ServerConnectorFactoryConstants.SERVICE_REF_CONNECTION_FACTORIES + ".target",
    ServerConnectorFactoryConstants.PROP_IDLE_TIMEOUT,
    ServerConnectorFactoryConstants.PROP_NAME,
    ServerConnectorFactoryConstants.PROP_REUSE_ADDRESS,
    ServerConnectorFactoryConstants.PROP_ACCEPT_QUEUE_SIZE,
    ServerConnectorFactoryConstants.PROP_INHERIT_CHANNEL,
    ServerConnectorFactoryConstants.PROP_LINGER_TIME,
    ServerConnectorFactoryConstants.PROP_ACCEPTOR_PRIORITY_DELTA,
    ServerConnectorFactoryConstants.PROP_SELECTOR_PRIORITY_DELTA, })
@Service
public class ServerConnectorFactoryComponent implements NetworkConnectorFactory {

  private int acceptorPriorityDelta;

  private int acceptQueueSize;

  private ConnectionFactory[] connectionFactories;

  private String defaultProtocol;

  private long idleTimeout = ServerConnectorFactoryConstants.DEFAULT_IDLE_TIMEOUT;

  private boolean inheritChannel;

  private int lingerTime;

  private String name;

  private final WeakHashMap<ServerConnector, Boolean> providedConnectors =
      new WeakHashMap<ServerConnector, Boolean>();

  private boolean reuseAddress;

  private int selectorPriorityDelta;

  private void applyDefaultProtocolOnServerConnector(final ServerConnector result) {
    if (defaultProtocol != null) {
      result.setDefaultProtocol(defaultProtocol);
    } else if (connectionFactories.length > 0) {
      result.setDefaultProtocol(connectionFactories[0].getProtocol());
    }
  }

  @Override
  public ServerConnector createNetworkConnector(final Server server, final String host,
      final int port) {

    ServerConnector result = new ServerConnector(server);

    // TODO Validate if there are multiple connection factories with the same protocol
    result.setConnectionFactories(Arrays.asList(connectionFactories));
    result.setAcceptorPriorityDelta(acceptorPriorityDelta);
    result.setAcceptQueueSize(acceptQueueSize);
    result.setIdleTimeout(idleTimeout);
    result.setInheritChannel(inheritChannel);
    result.setName(name);
    result.setReuseAddress(reuseAddress);
    result.setSelectorPriorityDelta(selectorPriorityDelta);
    result.setSoLingerTime(lingerTime);

    applyDefaultProtocolOnServerConnector(result);

    result.setHost(host);
    result.setPort(port);
    putIntoProvidedConnectors(result);
    return result;
  }

  private synchronized Iterator<ServerConnector> providedServerConnectorIterator() {
    Iterator<ServerConnector> result = null;
    while (result == null) {
      try {
        result = new HashSet<ServerConnector>(providedConnectors.keySet()).iterator();
      } catch (ConcurrentModificationException e) {
        // TODO probably some warn logging would be nice
      }
    }
    return result;
  }

  private synchronized void putIntoProvidedConnectors(final ServerConnector result) {
    providedConnectors.put(result, Boolean.TRUE);
  }

  /**
   * Setter that also updates the property on the connector without restarting it.
   */
  @IntegerAttribute(attributeId = ServerConnectorFactoryConstants.PROP_ACCEPTOR_PRIORITY_DELTA,
      defaultValue = 0, dynamic = true)
  public synchronized void setAcceptorPriorityDelta(final int acceptorPriorityDelta) {
    Iterator<ServerConnector> providedServerConnectorIterator = providedServerConnectorIterator();
    while (providedServerConnectorIterator.hasNext()) {
      ServerConnector serverConnector = providedServerConnectorIterator.next();
      serverConnector.setAcceptorPriorityDelta(acceptorPriorityDelta);
    }
    this.acceptorPriorityDelta = acceptorPriorityDelta;
  }

  /**
   * Setter that also updates the property on the connector without restarting it.
   */
  @IntegerAttribute(attributeId = ServerConnectorFactoryConstants.PROP_ACCEPT_QUEUE_SIZE,
      defaultValue = 0, dynamic = true)
  public synchronized void setAcceptQueueSize(final int acceptQueueSize) {
    Iterator<ServerConnector> providedServerConnectorIterator = providedServerConnectorIterator();
    while (providedServerConnectorIterator.hasNext()) {
      ServerConnector serverConnector = providedServerConnectorIterator.next();
      serverConnector.setAcceptQueueSize(acceptQueueSize);
    }
    this.acceptQueueSize = acceptQueueSize;
  }

  /**
   * Setter that also updates the property on the connector without restarting it.
   */
  @ServiceRef(referenceId = ServerConnectorFactoryConstants.SERVICE_REF_CONNECTION_FACTORIES,
      dynamic = true)
  public synchronized void setConnectionFactories(final ConnectionFactory[] connectionFactories) {
    Iterator<ServerConnector> providedServerConnectorIterator = providedServerConnectorIterator();
    while (providedServerConnectorIterator.hasNext()) {
      ServerConnector serverConnector = providedServerConnectorIterator.next();
      serverConnector.setConnectionFactories(Arrays.asList(connectionFactories));
    }
    this.connectionFactories = connectionFactories;
  }

  /**
   * Setter that also updates the property on the connector without restarting it.
   */
  @StringAttribute(attributeId = ServerConnectorFactoryConstants.PROP_DEFAULT_PROTOCOL,
      optional = true, dynamic = true)
  public synchronized void setDefaultProtocol(final String defaultProtocol) {
    Iterator<ServerConnector> providedServerConnectorIterator = providedServerConnectorIterator();
    while (providedServerConnectorIterator.hasNext()) {
      ServerConnector serverConnector = providedServerConnectorIterator.next();
      serverConnector.setDefaultProtocol(defaultProtocol);
    }
    this.defaultProtocol = defaultProtocol;
  }

  /**
   * Setter that also updates the property on the connector without restarting it.
   */
  @LongAttribute(attributeId = ServerConnectorFactoryConstants.PROP_IDLE_TIMEOUT,
      defaultValue = ServerConnectorFactoryConstants.DEFAULT_IDLE_TIMEOUT)
  public synchronized void setIdleTimeout(final long idleTimeout) {
    Iterator<ServerConnector> providedServerConnectorIterator = providedServerConnectorIterator();
    while (providedServerConnectorIterator.hasNext()) {
      ServerConnector serverConnector = providedServerConnectorIterator.next();
      serverConnector.setIdleTimeout(idleTimeout);
    }
    this.idleTimeout = idleTimeout;
  }

  @BooleanAttribute(attributeId = ServerConnectorFactoryConstants.PROP_INHERIT_CHANNEL,
      defaultValue = ServerConnectorFactoryConstants.DEFAULT_INHERIT_CHANNEL)
  public void setInheritChannel(final boolean inheritChannel) {
    this.inheritChannel = inheritChannel;
  }

  @IntegerAttribute(attributeId = ServerConnectorFactoryConstants.PROP_LINGER_TIME,
      defaultValue = ServerConnectorFactoryConstants.DEFAULT_LINGER_TIME)
  public synchronized void setLingerTime(final int lingerTime) {
    this.lingerTime = lingerTime;
  }

  @StringAttribute(attributeId = ServerConnectorFactoryConstants.PROP_NAME, optional = true)
  public void setName(final String name) {
    this.name = name;
  }

  @BooleanAttribute(attributeId = ServerConnectorFactoryConstants.PROP_REUSE_ADDRESS,
      defaultValue = ServerConnectorFactoryConstants.DEFAULT_REUSE_ADDRESS)
  public void setReuseAddress(final boolean reuseAddress) {
    this.reuseAddress = reuseAddress;
  }

  @IntegerAttribute(attributeId = ServerConnectorFactoryConstants.PROP_SELECTOR_PRIORITY_DELTA,
      defaultValue = ServerConnectorFactoryConstants.DEFAULT_SELECTOR_PRIORITY_DELTA,
      dynamic = true)
  public synchronized void setSelectorPriorityDelta(final int selectorPriorityDelta) {
    this.selectorPriorityDelta = selectorPriorityDelta;
  }

}
