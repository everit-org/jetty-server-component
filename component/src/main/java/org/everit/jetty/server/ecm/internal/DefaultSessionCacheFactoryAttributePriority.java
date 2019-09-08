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
