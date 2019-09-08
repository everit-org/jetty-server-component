package org.everit.jetty.server.ecm;

import org.eclipse.jetty.server.session.SessionDataStore;

/**
 * Constants that help the usage of all {@link SessionDataStore} components.
 */
public final class CommonSessionDataStoreFactoryConstants {

  public static final String ATTR_GRACE_PERIOD_SEC = "gracePeriodSec";

  public static final String ATTR_SAVE_PERIOD_SEC = "savePeriodSec";

  /**
   * One hour.
   */
  public static final int DEFAULT_GRACE_PERIOD_SEC = 60 * 60;

  public static final int DEFAULT_SAVE_PERIOD_SEC = 0;

  private CommonSessionDataStoreFactoryConstants() {
  }
}
