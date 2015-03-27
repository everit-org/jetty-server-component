/*
 * Copyright (C) 2015 Everit Kft. (http://www.everit.org)
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
package org.everit.osgi.jetty.server.component.internal;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConfiguration.Customizer;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.everit.osgi.ecm.annotation.AttributeOrder;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.IntegerAttribute;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.osgi.jetty.server.ConnectionFactoryFactory;
import org.everit.osgi.jetty.server.component.HttpConnectionFactoryConstants;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * ECM based configurable component that can start one or more {@link HttpConnectionFactory}s and
 * register them as OSGi services.
 */
@Component(componentId = HttpConnectionFactoryConstants.FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    localizationBase = "OSGI-INF/metatype/httpConnectionFactoryFactory")
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@AttributeOrder({ HttpConnectionFactoryConstants.SERVICE_REF_CUSTOMIZERS + ".target",
    HttpConnectionFactoryConstants.ATTR_INPUT_BUFFER_SIZE,
    HttpConnectionFactoryConstants.ATTR_DELAY_DISPATCH_UNTIL_CONTENT, })
@Service
public class HttpConnectionFactoryFactoryComponent implements ConnectionFactoryFactory {

  private WeakHashMap<HttpConnectionFactory, Boolean> activeConnectionFactories =
      new WeakHashMap<HttpConnectionFactory, Boolean>();

  private Customizer[] customizers;

  private boolean delayDispatchUntilContent = false;

  private int inputBufferSize;

  private synchronized Set<HttpConnectionFactory> cloneActiveConnectionFactories() {
    Set<HttpConnectionFactory> result = null;
    while (result == null) {
      try {
        result = new HashSet<HttpConnectionFactory>(activeConnectionFactories.keySet());
      } catch (ConcurrentModificationException e) {
        // TODO probably some warn logging would be nice
      }
    }
    return result;
  }

  @Override
  public ConnectionFactory createConnectionFactory(final String nextProtocol) {
    HttpConfiguration httpConfiguration = new HttpConfiguration();
    httpConfiguration.setDelayDispatchUntilContent(delayDispatchUntilContent);
    httpConfiguration.setCustomizers(Arrays.asList(customizers));

    HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);
    httpConnectionFactory.setInputBufferSize(inputBufferSize);

    activeConnectionFactories.put(httpConnectionFactory, true);
    return httpConnectionFactory;
  }

  @Override
  public String getProtocol() {
    return HttpVersion.HTTP_1_1.toString();
  }

  @ServiceRef(referenceId = HttpConnectionFactoryConstants.SERVICE_REF_CUSTOMIZERS,
      optional = true, dynamic = true)
  public void setCustomizers(final Customizer[] customizers) {
    updateCustomizers(customizers);
  }

  @BooleanAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_DELAY_DISPATCH_UNTIL_CONTENT,
      defaultValue = false, dynamic = true)
  public void setDelayDispatchUntilContent(final boolean delayDispatchUntilContent) {
    updateDelayDispatchUntilContent(delayDispatchUntilContent);
  }

  @IntegerAttribute(attributeId = HttpConnectionFactoryConstants.ATTR_INPUT_BUFFER_SIZE,
      defaultValue = HttpConnectionFactoryConstants.DEFAULT_INPUT_BUFFER_SIZE, dynamic = true)
  public void setInputBufferSize(final int inputBufferSize) {
    updateInputBufferSize(inputBufferSize);
  }

  private void updateCustomizers(final Customizer[] customizers) {
    this.customizers = customizers;
    Set<HttpConnectionFactory> connectionFactories = cloneActiveConnectionFactories();
    for (HttpConnectionFactory httpConnectionFactory : connectionFactories) {
      httpConnectionFactory.getHttpConfiguration().setCustomizers(Arrays.asList(customizers));
    }
  }

  private void updateDelayDispatchUntilContent(final boolean pDelayDispatchUntilContent) {
    this.delayDispatchUntilContent = pDelayDispatchUntilContent;
    Set<HttpConnectionFactory> connectionFactories = cloneActiveConnectionFactories();
    for (HttpConnectionFactory httpConnectionFactory : connectionFactories) {
      httpConnectionFactory.getHttpConfiguration().setDelayDispatchUntilContent(
          pDelayDispatchUntilContent);
    }
  }

  private void updateInputBufferSize(final int pInputBufferSize) {
    this.inputBufferSize = pInputBufferSize;
    Set<HttpConnectionFactory> connectionFactories = cloneActiveConnectionFactories();
    for (HttpConnectionFactory httpConnectionFactory : connectionFactories) {
      httpConnectionFactory.setInputBufferSize(pInputBufferSize);
    }
  }
}
