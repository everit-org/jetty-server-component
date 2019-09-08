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
