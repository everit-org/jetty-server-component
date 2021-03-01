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
package org.everit.jetty.server.ecm.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.everit.osgi.dev.testrunner.EOSGiTestClass;
import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.component.ConfigurationException;
import org.everit.osgi.ecm.extender.ExtendComponent;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test component that tests functionality.
 */
@ExtendComponent
@Component(configurationPolicy = ConfigurationPolicy.IGNORE)
@StringAttributes({
    @StringAttribute(attributeId = TestRunnerConstants.SERVICE_PROPERTY_TEST_ID,
        defaultValue = "JettyComponentTest"),
    @StringAttribute(attributeId = TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE,
        defaultValue = "junit4") })
@EOSGiTestClass
@Service
public class JettyComponentTest {

  private static HttpURLConnection openConnection(final URL url) throws IOException {
    URLConnection urlConnection = url.openConnection();
    if (!(urlConnection instanceof HttpURLConnection)) {
      throw new RuntimeException("urlConnection should be instasnce of HttpUrlConnection");
    }
    return (HttpURLConnection) urlConnection;
  }

  private static String readResponseFromUrlConnection(final HttpURLConnection urlConnection)
      throws IOException {
    urlConnection.connect();
    InputStream inputStream = urlConnection.getInputStream();
    StringBuilder sb = new StringBuilder();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

      boolean first = true;
      String line = reader.readLine();
      while (line != null) {
        if (!first) {
          sb.append("\n");
        }
        sb.append(line);
        line = reader.readLine();
      }
    }
    return sb.toString();
  }

  private int port = 0;

  private Server server;

  /**
   * Sets the port where the server listens on.
   */
  @Activate
  public void activate() {
    Connector[] connectors = this.server.getConnectors();
    if (connectors.length == 0) {
      throw new ConfigurationException("At least on network connector should be available");
    }
    Integer foundPort = null;
    for (int i = 0; (i < connectors.length) && (foundPort == null); i++) {
      Connector connector = connectors[i];
      if (!(connector instanceof NetworkConnector)) {
        throw new ConfigurationException("Connector must be an instance of network connector");
      }
      @SuppressWarnings("resource")
      NetworkConnector networkConnector = (NetworkConnector) connector;
      List<String> protocols = networkConnector.getProtocols();
      if (!protocols.contains("ssl")) {
        foundPort = networkConnector.getLocalPort();
      }
    }

    if (foundPort == null) {
      throw new ConfigurationException("No simple http network connector found");
    }
    this.port = foundPort;

  }

  private JSONObject readJSONResponse(final HttpURLConnection urlConnection) throws IOException {
    String response = JettyComponentTest.readResponseFromUrlConnection(urlConnection);
    JSONObject jsonObject = new JSONObject(response);
    return jsonObject;
  }

  @ServiceRef(
      defaultValue = "(org.everit.osgi.ecm.component.id=org.everit.jetty.server.ecm.JettyServer)")
  public void setServer(final Server server) {
    this.server = server;
  }

  @Test
  public void testForwardRequestCustomizer() {
    try {
      InetAddress localHost = InetAddress.getLocalHost();
      URL url =
          new URL("http://" + localHost.getHostName() + ":" + this.port + "/sample/echoremote");
      HttpURLConnection urlConnection = JettyComponentTest.openConnection(url);
      JSONObject jsonObject = readJSONResponse(urlConnection);
      Assert.assertEquals(localHost.getHostName(), jsonObject.getString("serverName"));
      Assert.assertEquals(String.valueOf(this.port), jsonObject.get("serverPort").toString());

      final String testClientName = "11.11" + ".11.11";
      final String testServerName = "mytest.com";
      final int testServerPort = 888;

      urlConnection = JettyComponentTest.openConnection(url);
      urlConnection.setRequestProperty(HttpHeader.X_FORWARDED_FOR.asString(), testClientName);

      urlConnection.setRequestProperty(HttpHeader.X_FORWARDED_HOST.asString(),
          testServerName + ":" + testServerPort);
      urlConnection.setRequestProperty(HttpHeader.X_FORWARDED_PROTO.asString(), "https");

      jsonObject = readJSONResponse(urlConnection);
      Assert.assertEquals(testClientName, jsonObject.getString("remoteAddr"));
      Assert.assertEquals(testServerName, jsonObject.getString("serverName"));
      Assert.assertEquals(String.valueOf(testServerPort), jsonObject.get("serverPort").toString());
      Assert.assertEquals(true, Boolean.valueOf(jsonObject.get("secure").toString()));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

  }

  @Test
  public void testPlainTextHttp2Support() throws Exception {
    InetAddress localHost = InetAddress.getLocalHost();
    HttpClient httpClient = new HttpClient(new HttpClientTransportOverHTTP2(new HTTP2Client()));
    httpClient.start();

    ContentResponse contentResponse =
        httpClient
            .GET("http://" + localHost.getHostName() + ":" + this.port + "/sample/echoremote");

    JSONObject jsonObject = new JSONObject(contentResponse.getContentAsString());

    httpClient.stop();

    Assert.assertEquals(localHost.getHostName(), jsonObject.getString("serverName"));
    Assert.assertEquals(String.valueOf(this.port), jsonObject.get("serverPort").toString());
  }
}
