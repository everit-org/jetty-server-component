/*
 * Copyright © 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.jetty.server.ecm;

import org.everit.jetty.server.SessionCacheFactory;

/**
 * Constants that help the usage of {@link SessionCacheFactory} component.
 */
public final class DefaultSessionCacheFactoryConstants {

  public static final String ATTR_EVICTION_TIMEOUT = "evictionTimeout";

  public static final String ATTR_REMOVE_UNLOADABLE_SESSIONS = "removeUnloadableSessions";

  public static final String ATTR_SAVE_ON_CREATE = "saveOnCreate";

  public static final String ATTR_SAVE_ON_INACTIVE_EVICTION = "saveOnInactiveEviction";

  public static final String ATTR_SESSION_DATA_STORE_FACTORY = "sessionDataStoreFactory";

  public static final String SERVICE_FACTORY_PID =
      "org.everit.jetty.server.ecm.DefaultSessionCacheFactory";

  private DefaultSessionCacheFactoryConstants() {
  }
}
