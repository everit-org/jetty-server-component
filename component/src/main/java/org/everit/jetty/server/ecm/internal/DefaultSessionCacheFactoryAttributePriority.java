package org.everit.jetty.server.ecm.internal;

/**
 * Constants of SessionCacheFactory attribute priority.
 */
public final class DefaultSessionCacheFactoryAttributePriority {

  public static final int P01_SERVICE_DESCRIPTION = 1;

  public static final float P1_SESSION_DATA_STORE_FACTORY = 1;

  public static final float P2_EVICTION_TIMEOUT = 2;

  public static final float P3_SAVE_ON_CREATE = 3;

  public static final float P4_SAVE_ON_INACTIVE_EVICTION = 4;

  public static final float P5_REMOVE_UNLOADABLE_SESSIONS = 5;

  private DefaultSessionCacheFactoryAttributePriority() {
  }
}
