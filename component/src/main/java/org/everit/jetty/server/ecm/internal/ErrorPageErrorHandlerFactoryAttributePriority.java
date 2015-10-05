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
 * Constants of ErrorPageErrorHandlerFactory attribute priority.
 */
public final class ErrorPageErrorHandlerFactoryAttributePriority {

  public static final int P01_SERVICE_DESCRIPTION = 1;

  public static final int P02_ERROR_PAGES = 2;

  public static final int P03_CACHE_CONTROL = 3;

  public static final int P04_SHOW_MESSAGE_IN_TITLE = 4;

  public static final int P05_SHOW_STACKS = 5;

  private ErrorPageErrorHandlerFactoryAttributePriority() {
  }
}
