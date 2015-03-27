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

import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;

/**
 * Wraps a {@link ConnectionFactory} and provides connections in the way that remembers all
 * referenced {@link EndPoint}s so they can be closed in case of a dynamic update.
 *
 * @param <T>
 *          The type of the {@link ConnectionFactory}.s
 */
public class CustomConnectionFactory<T extends ConnectionFactory> implements ConnectionFactory {

  private final WeakHashMap<Connection, Boolean> referencedConnections =
      new WeakHashMap<Connection, Boolean>();

  private final T wrapped;

  public CustomConnectionFactory(final T wrapped) {
    this.wrapped = wrapped;
  }

  private synchronized Set<Connection> cloneReferencedEndPoints() {
    Set<Connection> result = null;
    while (result == null) {
      try {
        result = new HashSet<Connection>(referencedConnections.keySet());
      } catch (ConcurrentModificationException e) {
        // TODO probably some warn logging would be nice
      }
    }
    return result;
  }

  /**
   * Closes all endpoints that are referenced from anywhere.
   */
  public void closeAllReferencedEndpoint() {
    Set<Connection> connections = cloneReferencedEndPoints();
    for (Connection connection : connections) {
      EndPoint endPoint = connection.getEndPoint();
      endPoint.close();
    }
  }

  @Override
  public String getProtocol() {
    return wrapped.getProtocol();
  }

  public T getWrapped() {
    return wrapped;
  }

  @Override
  public synchronized Connection newConnection(final Connector connector, final EndPoint endPoint) {
    Connection result = wrapped.newConnection(connector, endPoint);
    referencedConnections.put(result, Boolean.TRUE);
    return result;
  }

}
