package org.everit.jetty.server.ecm;

/**
 * Constants of JDBCSessionDataSourceFactory component.
 */
public final class JDBCSessionDataStoreFactoryConstants {

  public static final String ATTR_ACCESS_TIME_COLUMN = "accessTimeColumn";

  public static final String ATTR_BLOB_TYPE = "blobType";

  public static final String ATTR_CONTEXT_PATH_COLUMN = "contextPathColumn";

  public static final String ATTR_COOKIE_TIME_COLUMN = "cookieTimeColumn";

  public static final String ATTR_CREATE_TIME_COLUMN = "createTimeColumn";

  public static final String ATTR_DATASTORE = "dataSource";

  public static final String ATTR_EXPIRY_TIME_COLUMN = "expiryTimeColumn";

  public static final String ATTR_ID_COLUMN = "idColumn";

  public static final String ATTR_LAST_ACCESS_TIME_COLUMN = "lastAccessTimeColumn";

  public static final String ATTR_LAST_NODE_COLUMN = "lastNodeColumn";

  public static final String ATTR_LAST_SAVED_TIME_COLUMN = "lastSavedTimeColumn";

  public static final String ATTR_LONG_TYPE = "longType";

  public static final String ATTR_MAP_COLUMN = "mapColumn";

  public static final String ATTR_MAX_INTERVAL_COLUMN = "maxIntervalColumn";

  public static final String ATTR_SCHEMA_NAME = "schemaName";

  public static final String ATTR_STRING_TYPE = "varchar";

  public static final String ATTR_TABLE_NAME = "tableName";

  public static final String ATTR_VIRTUAL_HOST_COLUMN = "virtualHostColumn";

  public static final String DEFAULT_ACCESS_TIME_COLUMN = "accessTime";

  public static final String DEFAULT_CONTEXT_PATH_COLUMN = "contextPath";

  public static final String DEFAULT_COOKIE_TIME_COLUMN = "cookieTime";

  public static final String DEFAULT_CREATE_TIME_COLUMN = "createTime";

  public static final String DEFAULT_EXPIRY_TIME_COLUMN = "expiryTime";

  public static final String DEFAULT_ID_COLUMN = "sessionId";

  public static final String DEFAULT_LAST_ACCESS_TIME_COLUMN = "lastAccessTime";

  public static final String DEFAULT_LAST_NODE_COLUMN = "lastNode";

  public static final String DEFAULT_LAST_SAVED_TIME_COLUMN = "lastSavedTime";

  public static final String DEFAULT_MAP_COLUMN = "map";

  public static final String DEFAULT_MAX_INTERVAL_COLUMN = "maxInterval";

  public static final String DEFAULT_TABLE_NAME = "JettySessions";

  public static final String DEFAULT_VIRTUAL_HOST_COLUMN = "virtualHost";

  public static final float P30_BLOB_TYPE = 30;

  public static final float P31_LONG_TYPE = 31;

  public static final float P32_STRING_TYPE = 32;

  public static final float P33_SCHEMA_NAME = 33;

  public static final float P34_TABLE_NAME = 34;

  public static final float P35_ID_COLUMN = 35;

  public static final float P36_ACCESS_TIME_COLUMN = 36;

  public static final float P37_CONTEXT_PATH_COLUMN = 37;

  public static final float P38_COOKIE_TIME_COLUMN = 38;

  public static final float P39_CREATE_TIME_COLUMN = 39;

  public static final float P40_EXPIRY_TIME_COLUMN = 40;

  public static final float P41_LAST_ACCESS_TIME_COLUMN = 41;

  public static final float P42_LAST_NODE_COLUMN = 42;

  public static final float P43_LAST_SAVED_TIME_COLUMN = 43;

  public static final float P44_MAP_COLUMN = 44;

  public static final float P45_MAX_INTERVAL_COLUMN = 45;

  public static final float P46_VIRTUAL_HOST_COLUMN = 46;

  public static final String SERVICE_FACTORY_PID =
      "org.everit.jetty.server.ecm.JDBCSessionDataStoreFactory";

  private JDBCSessionDataStoreFactoryConstants() {
  }
}
