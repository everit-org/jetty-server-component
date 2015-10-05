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
 * Constants of HashSessionHandlerFactory attribute priority.
 */
public final class HashSessionHandlerFactoryAttributePriority {

  public static final int P01_SERVICE_DESCRIPTION = 1;

  public static final int P02_MAX_INACTIVE_INTERVAL = 2;

  public static final int P03_SESSION_LISTENERS = 3;

  public static final int P04_SESSION_ATTRIBUTE_LISTENERS = 4;

  public static final int P05_SESSION_ID_LISTENERS = 5;

  public static final int P06_SECURE_REQUEST_ONLY = 6;

  public static final int P07_USING_COOKIES = 7;

  public static final int P08_COOKIE_NAME = 8;

  public static final int P09_USING_URLS = 9;

  public static final int P10_SESSION_ID_PARAMETER_NAME = 10;

  public static final int P11_WORKER_NAME = 11;

  public static final int P12_CHECKING_REMOTE_SESSION_ID_ENCODING = 12;

  public static final int P13_SAVE_PERIOD = 13;

  public static final int P14_STORE_DIRECTORY = 14;

  public static final int P15_DELETE_UNRESTORABLE_SESSIONS = 15;

  public static final int P16_HTTP_ONLY = 16;

  public static final int P17_IDLE_SAVE_PERIOD = 17;

  public static final int P18_NODE_IN_SESSION_ID = 18;

  public static final int P19_REFRESH_COOKIE_AGE = 19;

  public static final int P20_SCAVENGE_PERIOD = 20;

  public static final int P21_RESEED = 21;

  public static final int P22_RANDOM = 22;

  public static final int P23_LAZY_LOAD = 23;

  private HashSessionHandlerFactoryAttributePriority() {
  }
}
