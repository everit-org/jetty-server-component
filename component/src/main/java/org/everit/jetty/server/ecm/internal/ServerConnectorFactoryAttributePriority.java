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

/**
 * Constants of ServerConnectorFactory attribute priority.
 */
public final class ServerConnectorFactoryAttributePriority {

  public static final int P01_SERVICE_DESCRIPTION = 1;

  public static final int P02_CONNECTION_FACTORY_FACTORIES = 2;

  public static final int P03_IDLE_TIMEOUT = 3;

  public static final int P04_NAME = 4;

  public static final int P05_REUSE_ADDRESS = 5;

  public static final int P06_ACCEPT_QUEUE_SIZE = 6;

  public static final int P07_INHERIT_CHANNEL = 7;

  public static final int P08_LINGER_TIME = 8;

  public static final int P09_ACCEPTOR_PRIORITY_DELTA = 9;

  public static final int P10_SELECTOR_PRIORITY_DELTA = 10;

  private ServerConnectorFactoryAttributePriority() {
  }
}
