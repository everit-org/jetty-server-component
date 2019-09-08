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
