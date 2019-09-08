package org.everit.jetty.server.ecm.internal;

import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.SessionCache;
import org.eclipse.jetty.server.session.SessionHandler;
import org.everit.jetty.server.SessionCacheFactory;
import org.everit.jetty.server.SessionDataStoreFactory;
import org.everit.jetty.server.ecm.DefaultSessionCacheFactoryConstants;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.ReferenceConfigurationType;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.IntegerAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.extender.ExtendComponent;
import org.osgi.framework.Constants;

/**
 * ECM based configurable component that registers one or more instantiated
 * {@link SessionCacheFactory} OSGi services.
 */
@ExtendComponent
@Component(componentId = DefaultSessionCacheFactoryConstants.SERVICE_FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    label = "Everit Jetty Default SessionCache Factory",
    description = "ECM based component that can register one or more SessionCacheFactory "
        + "instances.")
@StringAttributes({
    @StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION, optional = true,
        priority = DefaultSessionCacheFactoryAttributePriority.P01_SERVICE_DESCRIPTION,
        label = "Service description",
        description = "Optional description for SessionCacheFactory service.") })
@Service(SessionCacheFactory.class)
public class DefaultSessionCacheFactoryComponent implements SessionCacheFactory {

  private int evictionTimeout = -1;

  private boolean removeUnloadableSessions = false;

  private boolean saveOnCreate = false;

  private boolean saveOnInactiveEviction = false;

  private SessionDataStoreFactory sessionDataStoreFactory = null;

  @Override
  public SessionCache createSessionCache(SessionHandler sessionHandler) {
    DefaultSessionCache sessionCache = new DefaultSessionCache(sessionHandler);
    sessionCache.setEvictionPolicy(this.evictionTimeout);
    sessionCache.setRemoveUnloadableSessions(this.removeUnloadableSessions);
    sessionCache.setSaveOnCreate(this.saveOnCreate);
    sessionCache.setSaveOnInactiveEviction(this.saveOnInactiveEviction);
    if (this.sessionDataStoreFactory != null) {
      sessionCache.setSessionDataStore(this.sessionDataStoreFactory.createSessionDataStore());
    }
    return sessionCache;
  }

  @IntegerAttribute(attributeId = DefaultSessionCacheFactoryConstants.ATTR_EVICTION_TIMEOUT,
      defaultValue = SessionCache.NEVER_EVICT,
      priority = DefaultSessionCacheFactoryAttributePriority.P2_EVICTION_TIMEOUT,
      label = "Eviction timeout",
      description = "-1 means we never evict inactive sessions. 0 means we evict a session after"
          + " the last request for it exits. >0 is the number of seconds after which we"
          + " evict inactive sessions from the cache (default: -1)")
  public void setEvictionTimeout(int evictionTimeout) {
    this.evictionTimeout = evictionTimeout;
  }

  @BooleanAttribute(
      attributeId = DefaultSessionCacheFactoryConstants.ATTR_REMOVE_UNLOADABLE_SESSIONS,
      defaultValue = false,
      priority = DefaultSessionCacheFactoryAttributePriority.P5_REMOVE_UNLOADABLE_SESSIONS,
      label = "Remove unloadable sessions",
      description = "If a session's data cannot be loaded from the store without error, remove"
          + " it from the persistent store. (default: false)")
  public void setRemoveUnloadableSessions(boolean removeUnloadableSessions) {
    this.removeUnloadableSessions = removeUnloadableSessions;
  }

  @BooleanAttribute(attributeId = DefaultSessionCacheFactoryConstants.ATTR_SAVE_ON_CREATE,
      defaultValue = false,
      priority = DefaultSessionCacheFactoryAttributePriority.P3_SAVE_ON_CREATE,
      label = "Save on create",
      description = "Whether or not a session that is newly created should beimmediately saved."
          + " If false, a session that is created andinvalidated within a single request is"
          + " never persisted. (default: false)")
  public void setSaveOnCreate(boolean saveOnCreate) {
    this.saveOnCreate = saveOnCreate;
  }

  @BooleanAttribute(
      attributeId = DefaultSessionCacheFactoryConstants.ATTR_SAVE_ON_INACTIVE_EVICTION,
      defaultValue = false,
      priority = DefaultSessionCacheFactoryAttributePriority.P4_SAVE_ON_INACTIVE_EVICTION,
      label = "Save on inactive eviction",
      description = "Whether or not a a session that is about to be evicted shouldbe saved before"
          + " being evicted. (default: false)")
  public void setSaveOnInactiveEviction(boolean saveOnInactiveEviction) {
    this.saveOnInactiveEviction = saveOnInactiveEviction;
  }

  @ServiceRef(referenceId = DefaultSessionCacheFactoryConstants.ATTR_SESSION_DATA_STORE_FACTORY,
      configurationType = ReferenceConfigurationType.FILTER, optional = true, dynamic = false,
      attributePriority = DefaultSessionCacheFactoryAttributePriority.P1_SESSION_DATA_STORE_FACTORY,
      label = "Session data store factory (target)",
      description = "A SessionDataStore factory that is the authoritative sourceof session"
          + " information.")
  public void setSessionDataStore(SessionDataStoreFactory sessionDataStoreFactory) {
    this.sessionDataStoreFactory = sessionDataStoreFactory;
  }
}
