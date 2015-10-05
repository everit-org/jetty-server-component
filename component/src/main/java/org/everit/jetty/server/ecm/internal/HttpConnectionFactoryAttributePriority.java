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
 * Constants of HttpConnectionFactory attribute priority.
 */
public final class HttpConnectionFactoryAttributePriority {

  public static final int P01_SERVICE_DESCRIPTION = 1;

  public static final int P02_CUSTOMIZERS = 2;

  public static final int P03_SEND_DATE_HEADER = 3;

  public static final int P04_SEND_SERVER_VERSION = 4;

  public static final int P05_SEND_X_POWERED_BY = 5;

  public static final int P06_REQUEST_HEADER_SIZE = 6;

  public static final int P07_INPUT_BUFFER_SIZE = 7;

  public static final int P08_RESPONSE_HEADER_SIZE = 8;

  public static final int P09_OUTPUT_BUFFER_SIZE = 9;

  public static final int P10_OUTPUT_AGGREGATION_SIZE = 10;

  public static final int P11_HEADER_CACHE_SIZE = 11;

  public static final int P12_SECURE_SCHEME = 12;

  public static final int P13_SECURE_PORT = 13;

  public static final int P14_DELAY_DISPATCH_UNTIL_CONTENT = 14;

  private HttpConnectionFactoryAttributePriority() {
  }
}
