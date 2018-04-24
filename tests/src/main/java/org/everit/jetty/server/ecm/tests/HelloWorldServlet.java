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
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.extender.ExtendComponent;

/**
 * Simple servlet to test functionality.
 */
@ExtendComponent
@Component(configurationPolicy = ConfigurationPolicy.FACTORY)
@Service
public class HelloWorldServlet implements Servlet {

  private String name;

  @Override
  public void destroy() {
  }

  @Override
  public ServletConfig getServletConfig() {
    return null;
  }

  @Override
  public String getServletInfo() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void init(final ServletConfig config) throws ServletException {
  }

  @Override
  public void service(final ServletRequest req, final ServletResponse res) throws ServletException,
      IOException {

    PrintWriter writer = res.getWriter();

    writer.write("<html><body>");
    writer.write("<p>Hello " + this.name + "!</p>");
    writer.write("<p>Is secure: " + req.isSecure() + "</p>");
    writer.write("<p>We are at the meetup!!!</p>");
    writer.write("</body></html>");

  }

  @StringAttribute(defaultValue = "World", dynamic = true)
  public void setName(final String name) {
    this.name = name;
  }

}
