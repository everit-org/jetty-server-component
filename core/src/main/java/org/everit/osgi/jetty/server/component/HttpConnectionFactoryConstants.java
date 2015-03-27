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
 * Constants for HttpConnectionFactory component.
 */
public final class HttpConnectionFactoryConstants {

  public static final String ATTR_DELAY_DISPATCH_UNTIL_CONTENT = "delayDispatchUntilContent";

  public static final String ATTR_INPUT_BUFFER_SIZE = "inputBufferSize";

  public static final int DEFAULT_INPUT_BUFFER_SIZE = 8192;

  public static final String FACTORY_PID =
      "org.everit.osgi.jetty.server.component.HttpConnectionFactoryFactory";

  public static final String SERVICE_REF_CUSTOMIZERS = "customizers";

  private HttpConnectionFactoryConstants() {
  }
}
