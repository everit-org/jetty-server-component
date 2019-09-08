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
 * Constants of FileSessionDataStoreFactory attribute priority.
 */
public final class FileSessionDataStoreFactoryAttributePriority {

  public static final float P02_STORE_DIR = 2;

  public static final float P03_DELETE_UNRESTORABLE_FILES = 3;

  private FileSessionDataStoreFactoryAttributePriority() {
  }
}
