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
package org.everit.jetty.server.ecm.internal.servletcontext;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Generated;

import org.everit.osgi.ecm.component.ServiceHolder;
import org.osgi.framework.ServiceReference;

/**
 * Keys for Filters and Servlets.
 *
 * @param <T>
 *          The type of the holder.
 */
public class HolderKey<T> {

  private static final String INIT_PARAM_PREFIX = "init-";

  public final boolean asyncSupported;

  public final T heldValue;

  public final Map<String, String> initParameters;

  public final String name;

  public final ServiceReference<T> serviceReference;

  /**
   * Constructor that sets all properties based on the content of serviceHolder.
   *
   * @param serviceHolder
   *          The serviceHolder that contains all relevant attributes for this key.
   */
  public HolderKey(final ServiceHolder<T> serviceHolder) {
    this.serviceReference = serviceHolder.getReference();
    this.name = serviceHolder.getReferenceId();
    this.heldValue = serviceHolder.getService();
    this.asyncSupported = resolveAsyncSupported();
    this.initParameters = resolveInitParameters(serviceHolder.getAttributes());
  }

  // CHECKSTYLE.OFF: NPathComplexity
  // CHECKSTYLE.OFF: CyclomaticComplexity
  @Override
  @Generated("eclipse")
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    @SuppressWarnings("unchecked")
    HolderKey<T> other = (HolderKey<T>) obj;
    if (this.asyncSupported != other.asyncSupported) {
      return false;
    }
    if (this.heldValue == null) {
      if (other.heldValue != null) {
        return false;
      }
    } else if (!this.heldValue.equals(other.heldValue)) {
      return false;
    }
    if (this.initParameters == null) {
      if (other.initParameters != null) {
        return false;
      }
    } else if (!this.initParameters.equals(other.initParameters)) {
      return false;
    }
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    if (this.serviceReference == null) {
      if (other.serviceReference != null) {
        return false;
      }
    } else if (!this.serviceReference.equals(other.serviceReference)) {
      return false;
    }
    return true;
  }
  // CHECKSTYLE.ON: NPathComplexity
  // CHECKSTYLE.ON: CyclomaticComplexity

  @Override
  @Generated("eclipse")
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.asyncSupported ? 1231 : 1237);
    result = prime * result + (this.heldValue == null ? 0 : this.heldValue.hashCode());
    result =
        prime * result + (this.initParameters == null ? 0 : this.initParameters.hashCode());
    result = prime * result + (this.name == null ? 0 : this.name.hashCode());
    result =
        prime * result + (this.serviceReference == null ? 0 : this.serviceReference.hashCode());
    return result;
  }

  private boolean resolveAsyncSupported() {
    Object asyncSupportedProperty = this.serviceReference.getProperty("async-supported");
    if (asyncSupportedProperty == null) {
      return false;
    }

    return Boolean.valueOf(String.valueOf(asyncSupportedProperty));
  }

  private Map<String, String> resolveInitParameters(final Map<String, Object> attributes) {
    Map<String, String> result = new HashMap<>();
    Set<Entry<String, Object>> attributeEntries = attributes.entrySet();
    for (Entry<String, Object> attributeEntry : attributeEntries) {
      String attributeKey = attributeEntry.getKey();
      if (attributeKey.startsWith(HolderKey.INIT_PARAM_PREFIX)) {
        String initParamName = attributeKey.substring(HolderKey.INIT_PARAM_PREFIX.length());

        Object attributeValue = attributeEntry.getValue();
        String initParamValue = null;
        if (attributeValue != null) {
          initParamValue = String.valueOf(attributeValue);
        }

        result.put(initParamName, initParamValue);
      }
    }
    return result;
  }
}
