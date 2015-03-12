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
package org.everit.osgi.jetty.server.component;

/**
 * Constants of the usage of Jetty Server Component.
 */
public final class JettyServerConstants {

  // TODO move to ServletContextHandlerFactoryConstants
  public static final Object ATTR_DISPATCHER = "dispatcher";

  // TODO move to ServletContextHandlerFactoryConstants
  public static final String ATTR_MAPPING = "mapping";

  public static final String CONNECTOR_REF_CLAUSE_ATTR_HOST = "host";

  public static final String CONNECTOR_REF_CLAUSE_ATTR_PORT = "port";

  public static final String CONTEXT_CLAUSE_ATTR_CONTEXTPATH = "contextPath";

  public static final String SERVICE_REF_NETWORK_CONNECTOR_FACTORIES = "networkConnectorFactories";

  public static final String SERVICE_REF_SERVLET_CONTEXT_HANDLER_FACTORIES =
      "servletContextHandlerFactories";

  private JettyServerConstants() {
  }
}
