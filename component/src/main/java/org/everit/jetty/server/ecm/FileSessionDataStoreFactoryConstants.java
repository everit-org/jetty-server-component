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

/**
 * Constants of FileSessionDataSourceFactory component.
 */
public final class FileSessionDataStoreFactoryConstants {

  public static final String ATTR_DELETE_UNRESTORABLE_FILES = "deleteUnrestorableFiles";

  public static final String ATTR_STORE_DIR = "storeDir";

  public static final boolean DEFAULT_DELETE_UNRESTORABLE_FILES = false;

  public static final String SERVICE_FACTORY_PID =
      "org.everit.jetty.server.ecm.FileSessionDataStoreFactory";

  private FileSessionDataStoreFactoryConstants() {
  }
}
