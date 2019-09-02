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

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Generated;

import org.everit.jetty.server.ecm.ServletContextHandlerFactoryConstants;
import org.everit.osgi.ecm.component.ServiceHolder;
import org.osgi.framework.ServiceReference;

/**
 * Superclass for FilterMappingKeys and ServletMappingKeys.
 *
 * @param <T>
 *          The type that is mapped (servlet or filter).
 */
public class MappingKey<T> {

  public final T heldValue;

  public final ServiceReference<T> serviceReference;

  public final String[] urlPatterns;

  /**
   * Constructor.
   *
   * @param serviceHolder
   *          The serviceHolder that contains the relevant data.
   */
  public MappingKey(final ServiceHolder<T> serviceHolder) {
    this.serviceReference = serviceHolder.getReference();
    this.heldValue = serviceHolder.getService();

    Map<String, Object> attributes = serviceHolder.getAttributes();
    this.urlPatterns = resolveClausePotentialListAttribute(
        ServletContextHandlerFactoryConstants.CommonConstants.CLAUSE_ATTR_URL_PATTERN, attributes);

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
    MappingKey<T> other = (MappingKey<T>) obj;
    if (this.heldValue == null) {
      if (other.heldValue != null) {
        return false;
      }
    } else if (!this.heldValue.equals(other.heldValue)) {
      return false;
    }
    if (this.serviceReference == null) {
      if (other.serviceReference != null) {
        return false;
      }
    } else if (!this.serviceReference.equals(other.serviceReference)) {
      return false;
    }
    if (!Arrays.equals(this.urlPatterns, other.urlPatterns)) {
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
    result = prime * result + (this.heldValue == null ? 0 : this.heldValue.hashCode());
    result =
        prime * result + (this.serviceReference == null ? 0 : this.serviceReference.hashCode());
    result = prime * result + Arrays.hashCode(this.urlPatterns);
    return result;
  }

  /**
   * Resolves the specified attribute even if it is specified with XXX:List&lt;String&gt; format.
   *
   * @param attributeName
   *          The name of the attribute that is resolved from the clause.
   * @param clauseAttributes
   *          All attributes of the clause.
   * @return The attribute value or if the value does not exist, an empty array.
   */
  protected String[] resolveClausePotentialListAttribute(final String attributeName,
      final Map<String, Object> clauseAttributes) {

    Object attributeValue = clauseAttributes.get(attributeName);
    if (attributeValue == null) {
      attributeValue = clauseAttributes.get(attributeName + ":List<String>");
    }
    if (attributeValue == null) {
      return new String[0];
    }
    String concatenatedValue = String.valueOf(attributeValue);
    String[] split = concatenatedValue.split(",");
    return split;
  }

}
