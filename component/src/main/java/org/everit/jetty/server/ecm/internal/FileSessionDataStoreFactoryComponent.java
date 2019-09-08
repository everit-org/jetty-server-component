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

import java.io.File;

import org.eclipse.jetty.server.session.AbstractSessionDataStore;
import org.eclipse.jetty.server.session.FileSessionDataStore;
import org.everit.jetty.server.SessionDataStoreFactory;
import org.everit.jetty.server.ecm.FileSessionDataStoreFactoryConstants;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.extender.ExtendComponent;

/**
 * Configurable component that creates a {@link FileSessionDataStore} instance.
 */
@ExtendComponent
@Component(componentId = FileSessionDataStoreFactoryConstants.SERVICE_FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    label = "Everit Jetty FileSessionDataStore Factory")
@Service(SessionDataStoreFactory.class)
public class FileSessionDataStoreFactoryComponent extends AbstractSessionDataStoreFactoryComponent {

  private boolean deleteUnrestorableFiles = false;

  private String storeDir;

  @Override
  protected AbstractSessionDataStore doCreateSessionDataStore() {
    FileSessionDataStore fileSessionDataStore = new FileSessionDataStore();

    fileSessionDataStore.setStoreDir(new File(this.storeDir));
    fileSessionDataStore.setDeleteUnrestorableFiles(this.deleteUnrestorableFiles);

    return fileSessionDataStore;
  }

  @BooleanAttribute(
      attributeId = FileSessionDataStoreFactoryConstants.ATTR_DELETE_UNRESTORABLE_FILES,
      defaultValue = FileSessionDataStoreFactoryConstants.DEFAULT_DELETE_UNRESTORABLE_FILES,
      priority = FileSessionDataStoreFactoryAttributePriority.P03_DELETE_UNRESTORABLE_FILES,
      label = "Delete unrestorable files",
      description = "Whether to delete thos files that are not restorable for the session or not.")
  public void setDeleteUnrestorableFiles(boolean deleteUnrestorableFiles) {
    this.deleteUnrestorableFiles = deleteUnrestorableFiles;
  }

  @StringAttribute(attributeId = FileSessionDataStoreFactoryConstants.ATTR_STORE_DIR,
      priority = FileSessionDataStoreFactoryAttributePriority.P02_STORE_DIR,
      label = "Store directory",
      description = "Directory where sessions are stored.",
      optional = false)
  public void setStoreDir(String storeDir) {
    this.storeDir = storeDir;
  }
}
