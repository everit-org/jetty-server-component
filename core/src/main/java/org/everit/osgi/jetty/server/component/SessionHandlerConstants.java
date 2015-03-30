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
 * Common constants that are available in session manager implementations.
 */
public final class SessionHandlerConstants {

  public static final String ATTR_MAX_INACTIVE_INTERVAL = "maxInactiveInterval";

  public static final int DEFAULT_MAX_INACTIVE_INTERVAL = 30;

  public static final String SERVICE_REF_SESSION_ATTRIBUTE_LISTENERS = "sessionAttributeListeners";

  public static final String SERVICE_REF_SESSION_ID_LISTENERS = "sessionIdListeners";

  public static final String SERVICE_REF_SESSION_LISTENERS = "sessionListeners";

  private SessionHandlerConstants() {
  }

}
