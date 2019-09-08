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
