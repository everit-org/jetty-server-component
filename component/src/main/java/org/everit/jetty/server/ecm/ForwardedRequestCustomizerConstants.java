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
package org.everit.jetty.server.ecm;

/**
 * Constants that are available for ForwardedRequestCustomizer component.
 */
public final class ForwardedRequestCustomizerConstants {

  public static final String ATTR_FORWARDED_CIPHER_SUITE = "forwardedCipherSuite";

  public static final String ATTR_FORWARDED_HOST_HEADER = "forwardedHostHeader";

  public static final String ATTR_FORWARDED_PROTO_HEADER = "forwardedProtoHeader";

  public static final String ATTR_FORWARDED_REMOTE_ADDRESS_HEADER = "forwardedRemoteAddressHeader";

  public static final String ATTR_FORWARDED_SERVER_HEADER = "forwardedServerHeader";

  public static final String ATTR_FORWARDED_SSL_SESSION_ID = "forwardedSslSessionId";

  public static final String ATTR_HOST_HEADER = "hostHeader";

  public static final String SERVICE_FACTORY_PID =
      "org.everit.jetty.server.ecm.ForwardedRequestCustomizer";

  private ForwardedRequestCustomizerConstants() {
  }
}
