package org.everit.jetty.server.ecm.internal;

import java.util.function.Consumer;

import javax.sql.DataSource;

import org.eclipse.jetty.server.session.AbstractSessionDataStore;
import org.eclipse.jetty.server.session.DatabaseAdaptor;
import org.eclipse.jetty.server.session.FileSessionDataStore;
import org.eclipse.jetty.server.session.JDBCSessionDataStore;
import org.eclipse.jetty.server.session.JDBCSessionDataStore.SessionTableSchema;
import org.everit.jetty.server.SessionDataStoreFactory;
import org.everit.jetty.server.ecm.JDBCSessionDataStoreFactoryConstants;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.extender.ExtendComponent;

/**
 * Configurable component that creates a {@link FileSessionDataStore} instance.
 */
@ExtendComponent
@Component(componentId = JDBCSessionDataStoreFactoryConstants.SERVICE_FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    label = "Everit Jetty JDBCSessionDataStore Factory")
@Service(SessionDataStoreFactory.class)
public class JDBCSessionDataStoreFactoryComponent extends AbstractSessionDataStoreFactoryComponent {

  private static <T> void setIfNotNull(T value, Consumer<T> setter) {
    if (value != null) {
      setter.accept(value);
    }
  }

  private String accessTimeColumn;

  private String blobType;

  private String contextPathColumn;

  private String cookieTimeColumn;

  private String createTimeColumn;

  private DataSource dataSource;

  private String expiryTimeColumn;

  private String idColumn;

  private String lastAccessTimeColumn;

  private String lastNodeColumn;

  private String lastSavedTimeColumn;

  private String longType;

  private String mapColumn;

  private String maxIntervalColumn;

  private String schemaName;

  private String stringType;

  private String tableName;

  private String virtualHostColumn;

  @Override
  protected AbstractSessionDataStore doCreateSessionDataStore() {
    JDBCSessionDataStore jdbcSessionDataStore = new JDBCSessionDataStore();

    DatabaseAdaptor dbAdaptor = new DatabaseAdaptor();
    dbAdaptor.setDatasource(this.dataSource);
    dbAdaptor.setBlobType(this.blobType);
    dbAdaptor.setLongType(this.longType);
    dbAdaptor.setStringType(this.stringType);

    SessionTableSchema sessionTableSchema = new SessionTableSchema();

    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.schemaName,
        sessionTableSchema::setSchemaName);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.tableName,
        sessionTableSchema::setTableName);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.idColumn,
        sessionTableSchema::setIdColumn);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.accessTimeColumn,
        sessionTableSchema::setAccessTimeColumn);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.contextPathColumn,
        sessionTableSchema::setContextPathColumn);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.cookieTimeColumn,
        sessionTableSchema::setCookieTimeColumn);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.createTimeColumn,
        sessionTableSchema::setCreateTimeColumn);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.expiryTimeColumn,
        sessionTableSchema::setExpiryTimeColumn);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.lastAccessTimeColumn,
        sessionTableSchema::setLastAccessTimeColumn);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.lastNodeColumn,
        sessionTableSchema::setLastNodeColumn);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.lastSavedTimeColumn,
        sessionTableSchema::setLastSavedTimeColumn);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.mapColumn,
        sessionTableSchema::setMapColumn);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.maxIntervalColumn,
        sessionTableSchema::setMaxIntervalColumn);
    JDBCSessionDataStoreFactoryComponent.setIfNotNull(this.virtualHostColumn,
        sessionTableSchema::setVirtualHostColumn);

    jdbcSessionDataStore.setSessionTableSchema(sessionTableSchema);

    jdbcSessionDataStore.setDatabaseAdaptor(dbAdaptor);
    return jdbcSessionDataStore;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_ACCESS_TIME_COLUMN,
      priority = JDBCSessionDataStoreFactoryConstants.P36_ACCESS_TIME_COLUMN,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_ACCESS_TIME_COLUMN,
      label = "Access time column",
      description = "Name of the database table column where access time of the session is stored."
          + " Default: accessTime")
  public void setAccessTimeColumn(String accessTimeColumn) {
    this.accessTimeColumn = accessTimeColumn;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_BLOB_TYPE,
      priority = JDBCSessionDataStoreFactoryConstants.P30_BLOB_TYPE,
      optional = true,
      label = "Blob type",
      description = "Type of blob column. This is an optional field. If not set, type 'bytea'"
          + " is used for postgres, type 'blob' is used otherwise.")
  public void setBlobType(String blobType) {
    this.blobType = blobType;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_CONTEXT_PATH_COLUMN,
      priority = JDBCSessionDataStoreFactoryConstants.P37_CONTEXT_PATH_COLUMN,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_CONTEXT_PATH_COLUMN,
      label = "Context path column",
      description = "Name of the database table column where the context path of the session"
          + " is stored. Default: contextPath")
  public void setContextPathColumn(String contextPathColumn) {
    this.contextPathColumn = contextPathColumn;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_COOKIE_TIME_COLUMN,
      priority = JDBCSessionDataStoreFactoryConstants.P38_COOKIE_TIME_COLUMN,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_COOKIE_TIME_COLUMN,
      label = "Cookie time column",
      description = "Name of the database table column where the cookie time of the session"
          + " is stored. Default: cookieTime")
  public void setCookieTimeColumn(String cookieTimeColumn) {
    this.cookieTimeColumn = cookieTimeColumn;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_CREATE_TIME_COLUMN,
      priority = JDBCSessionDataStoreFactoryConstants.P39_CREATE_TIME_COLUMN,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_CREATE_TIME_COLUMN,
      label = "Create time column",
      description = "Name of the database table column where the creation time of the session"
          + " is stored. Default: createTime")
  public void setCreateTimeColumn(String createTimeColumn) {
    this.createTimeColumn = createTimeColumn;
  }

  @ServiceRef(referenceId = JDBCSessionDataStoreFactoryConstants.ATTR_DATASTORE, optional = false,
      attributePriority = JDBCSessionDataStoreFactoryAttributePriority.P01_DATASTORE,
      label = "DataStore (target)",
      description = "The datasource that is used to access the database.")
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_EXPIRY_TIME_COLUMN,
      priority = JDBCSessionDataStoreFactoryConstants.P40_EXPIRY_TIME_COLUMN,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_EXPIRY_TIME_COLUMN,
      label = "Expiry time column",
      description = "Name of the database table column where the expiry time of the session"
          + " is stored. Default: expiryTime")
  public void setExpiryTimeColumn(String expiryTimeColumn) {
    this.expiryTimeColumn = expiryTimeColumn;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_ID_COLUMN,
      priority = JDBCSessionDataStoreFactoryConstants.P35_ID_COLUMN,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_ID_COLUMN,
      label = "Id column",
      description = "Name of the database table column where the session id is stored."
          + " Default: sessionId")
  public void setIdColumn(String idColumn) {
    this.idColumn = idColumn;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_LAST_ACCESS_TIME_COLUMN,
      priority = JDBCSessionDataStoreFactoryConstants.P41_LAST_ACCESS_TIME_COLUMN,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_LAST_ACCESS_TIME_COLUMN,
      label = "Last access time column",
      description = "Name of the database table column where the last access time of the session"
          + " is stored. Default: lastAccessTime")
  public void setLastAccessTimeColumn(String lastAccessTimeColumn) {
    this.lastAccessTimeColumn = lastAccessTimeColumn;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_LAST_NODE_COLUMN,
      priority = JDBCSessionDataStoreFactoryConstants.P42_LAST_NODE_COLUMN,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_LAST_NODE_COLUMN,
      label = "Last node column",
      description = "Name of the database table column where the last node of the session"
          + " is stored. Default: lastNode")
  public void setLastNodeColumn(String lastNodeColumn) {
    this.lastNodeColumn = lastNodeColumn;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_LAST_SAVED_TIME_COLUMN,
      priority = JDBCSessionDataStoreFactoryConstants.P43_LAST_SAVED_TIME_COLUMN,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_LAST_SAVED_TIME_COLUMN,
      label = "Last saved time column",
      description = "Name of the database table column where the last saved time of the session"
          + " is stored. Default: lastSavedTime")
  public void setLastSavedTimeColumn(String lastSavedTimeColumn) {
    this.lastSavedTimeColumn = lastSavedTimeColumn;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_LONG_TYPE,
      priority = JDBCSessionDataStoreFactoryConstants.P31_LONG_TYPE,
      optional = true,
      label = "Long type",
      description = "Type of long column. This is an optional field. If not set, type 'number(20)'"
          + " is used for oracle, type 'bigint' is used otherwise.")
  public void setLongType(String longType) {
    this.longType = longType;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_MAP_COLUMN,
      priority = JDBCSessionDataStoreFactoryConstants.P44_MAP_COLUMN,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_MAP_COLUMN,
      label = "Map column",
      description = "Name of the database table column where the session attributes are stored."
          + " Default: map")
  public void setMapColumn(String mapColumn) {
    this.mapColumn = mapColumn;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_MAX_INTERVAL_COLUMN,
      priority = JDBCSessionDataStoreFactoryConstants.P45_MAX_INTERVAL_COLUMN,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_MAX_INTERVAL_COLUMN,
      label = "Max interval column",
      description = "Name of the database table column where the max interval of the session"
          + " is stored. Default: maxInterval")
  public void setMaxIntervalColumn(String maxIntervalColumn) {
    this.maxIntervalColumn = maxIntervalColumn;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_SCHEMA_NAME,
      priority = JDBCSessionDataStoreFactoryConstants.P33_SCHEMA_NAME,
      optional = true,
      label = "Schema name",
      description = "Name of the database schema where the tables should be.")
  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_STRING_TYPE,
      priority = JDBCSessionDataStoreFactoryConstants.P32_STRING_TYPE,
      defaultValue = "varchar",
      label = "String type",
      description = "Type of string column. Default: varchar.")
  public void setStringType(String stringType) {
    this.stringType = stringType;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_TABLE_NAME,
      priority = JDBCSessionDataStoreFactoryConstants.P34_TABLE_NAME,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_TABLE_NAME,
      label = "Table name",
      description = "Name of the table where the session data is stored. Default: JettySessions")
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  @StringAttribute(attributeId = JDBCSessionDataStoreFactoryConstants.ATTR_VIRTUAL_HOST_COLUMN,
      priority = JDBCSessionDataStoreFactoryConstants.P46_VIRTUAL_HOST_COLUMN,
      defaultValue = JDBCSessionDataStoreFactoryConstants.DEFAULT_VIRTUAL_HOST_COLUMN,
      label = "Virtual host column",
      description = "Name of the database table column where the virtual host of the session"
          + " is stored. Default: virtualHost")
  public void setVirtualHostColumn(String virtualHostColumn) {
    this.virtualHostColumn = virtualHostColumn;
  }
}
