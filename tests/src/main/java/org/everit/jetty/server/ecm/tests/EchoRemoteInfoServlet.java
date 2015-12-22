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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.json.JSONObject;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * Send back the remoteHost, remoteAddr and remotePort values of the request separated by a
 * semicolon.
 */
@Component(configurationPolicy = ConfigurationPolicy.IGNORE)
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@Service({ Servlet.class, EchoRemoteInfoServlet.class })
public class EchoRemoteInfoServlet implements Servlet {

  private ServletConfig servletConfig;

  @Override
  public void destroy() {

  }

  @Override
  public ServletConfig getServletConfig() {
    return this.servletConfig;
  }

  @Override
  public String getServletInfo() {
    return "test proxy servlet";
  }

  @Override
  public void init(final ServletConfig config) throws ServletException {
    this.servletConfig = config;

  }

  @Override
  public void service(final ServletRequest req, final ServletResponse res)
      throws ServletException, IOException {
    PrintWriter writer = res.getWriter();

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("remoteAddr", req.getRemoteAddr());
    jsonObject.put("remoteHost", req.getRemoteHost());
    jsonObject.put("remotePort", req.getRemotePort());
    jsonObject.put("serverName", req.getServerName());
    jsonObject.put("serverPort", req.getServerPort());
    jsonObject.put("protocol", req.getProtocol());
    jsonObject.put("secure", req.isSecure());

    writer.write(jsonObject.toString());

  }

}
