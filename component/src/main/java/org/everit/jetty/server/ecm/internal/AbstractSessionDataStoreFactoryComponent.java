package org.everit.jetty.server.ecm.internal;

import org.eclipse.jetty.server.session.AbstractSessionDataStore;
import org.eclipse.jetty.server.session.SessionDataStore;
import org.everit.jetty.server.SessionDataStoreFactory;
import org.everit.jetty.server.ecm.CommonSessionDataStoreFactoryConstants;
import org.everit.osgi.ecm.annotation.attribute.IntegerAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.osgi.framework.Constants;

/**
 * Abstract class that has the common functionality of the different {@link SessionDataStoreFactory}
 * components.
 */
@StringAttributes({
    @StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION, optional = true,
        priority = CommonSessionDataStoreFactoryAttributePriority.P01_SERVICE_DESCRIPTION,
        label = "Service description",
        description = "Optional description for SessionDataStore Factory service.") })
public abstract class AbstractSessionDataStoreFactoryComponent implements SessionDataStoreFactory {

  private int gracePeriodSec = CommonSessionDataStoreFactoryConstants.DEFAULT_GRACE_PERIOD_SEC;

  private int savePeriodSec;

  @Override
  public SessionDataStore createSessionDataStore() {
    AbstractSessionDataStore sessionDataStore = doCreateSessionDataStore();

    sessionDataStore.setGracePeriodSec(this.gracePeriodSec);
    sessionDataStore.setSavePeriodSec(this.savePeriodSec);

    return sessionDataStore;
  }

  protected abstract AbstractSessionDataStore doCreateSessionDataStore();

  @IntegerAttribute(attributeId = CommonSessionDataStoreFactoryConstants.ATTR_GRACE_PERIOD_SEC,
      defaultValue = CommonSessionDataStoreFactoryConstants.DEFAULT_GRACE_PERIOD_SEC,
      dynamic = true,
      priority = CommonSessionDataStoreFactoryAttributePriority.P20_GRACE_PERIOD_SEC,
      label = "Grace period seconds",
      description = "Interval in secs to prevent too eager session scavenging.")
  public void setGracePeriodSec(int gracePeriodSec) {
    this.gracePeriodSec = gracePeriodSec;
  }

  @IntegerAttribute(attributeId = CommonSessionDataStoreFactoryConstants.ATTR_SAVE_PERIOD_SEC,
      defaultValue = CommonSessionDataStoreFactoryConstants.DEFAULT_SAVE_PERIOD_SEC,
      dynamic = true,
      priority = CommonSessionDataStoreFactoryAttributePriority.P21_SAVE_PERIOD_SEC,
      label = "Save period seconds",
      description = "The minimum time in seconds between save operations. Saves normally occur"
          + " every time the last request exits as session. If nothing changes on the session"
          + " except for the access time and the persistence technology is slow, this can cause"
          + " delays. By default the value is 0, which means we save after the last request"
          + " exists. A non zero value means that we will skip doing the save if the session"
          + " isn't dirty if the elapsed time since the session was last saved does not exceed"
          + " this value.")
  public void setSavePeriodSec(int savePeriodSec) {
    this.savePeriodSec = savePeriodSec;
  }
}
