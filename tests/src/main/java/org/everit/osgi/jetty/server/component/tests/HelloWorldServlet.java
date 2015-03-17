package org.everit.osgi.jetty.server.component.tests;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;

import aQute.bnd.annotation.headers.ProvideCapability;

@Component
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@StringAttributes({ @StringAttribute(attributeId = "name", defaultValue = "helloworld") })
@Service
public class HelloWorldServlet implements Servlet {

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
    System.out.println("///////////////// INIT CALLED");
  }

  @Override
  public void service(final ServletRequest req, final ServletResponse res) throws ServletException,
      IOException {

    res.getWriter().write("HELLO WORLD!!!");

  }

}
