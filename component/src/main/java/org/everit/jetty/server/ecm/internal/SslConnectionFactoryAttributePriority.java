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
 * Constants of SslConnectionFactory attribute priority.
 */
public final class SslConnectionFactoryAttributePriority {

  public static final int P01_SERVICE_DESCRIPTION = 1;

  public static final int P02_KEYSTORE = 2;

  public static final int P03_KEYSTORE_PASSWORD = 3;

  public static final int P04_CERT_ALIAS = 4;

  public static final int P05_KEY_MANAGER_PASSWORD = 5;

  public static final int P06_INCLUDE_PROTOCOLS = 6;

  private SslConnectionFactoryAttributePriority() {
  }
}
