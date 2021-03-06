/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.errai.container;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.weld.resources.ManagerObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Emulates the behavior of the naming resource binding that is typically done using configuration in Tomcat and Jetty.
 * JBoss AS does not provide a way to register naming resources at the application level, but it does provide a
 * read-write naming director, so a listener will suffice.
 * 
 * @author Dan Allen
 */
public class BeanManagerResourceBindingListener implements ServletContextListener {
  private static final Logger log = LoggerFactory.getLogger(BeanManagerResourceBindingListener.class);

  private static final String RESOURCES_CONTEXT = "java:comp/env";

  private static final String BEAN_MANAGER_JNDI_NAME = "BeanManager";

  private static final String QUALIFIED_BEAN_MANAGER_JNDI_NAME = RESOURCES_CONTEXT + "/" + BEAN_MANAGER_JNDI_NAME;

  public void contextInitialized(ServletContextEvent sce) {
    try {
      InitialContext ctx = new InitialContext();
      boolean present = false;
      try {

        NamingEnumeration<NameClassPair> entries = ctx.list(RESOURCES_CONTEXT);
        while (entries.hasMoreElements()) {
          NameClassPair e = entries.next();
          if (e.getName().equals(BEAN_MANAGER_JNDI_NAME) && e.getClassName().equals(BeanManager.class)) {
            present = true;
            break;
          }
        }
      }
      catch (NamingException e) {
        log.info("Could not perform lookup to detect BeanManager reference in JNDI: " + e.getExplanation());
        try {
          Context compCtx = (Context) ctx.lookup("java:comp");
          compCtx.createSubcontext("env");
        }
        catch (Exception ex) {
          log.info("Could not create env context!");
        }
      }

      if (!present) {
        try {
          // we rebind just in case it really is there and we just couldn't read it
          ctx.rebind(QUALIFIED_BEAN_MANAGER_JNDI_NAME,
                     new Reference(BeanManager.class.getName(), ManagerObjectFactory.class.getName(), null));
          log.info("BeanManager reference bound to " + QUALIFIED_BEAN_MANAGER_JNDI_NAME);
        }
        catch (NamingException e) {
          log.warn("Could not bind BeanManager reference to JNDI: "
              + e.getExplanation()
              + " \n"
              + "If the naming context is read-only, you may need to use configuration to bind the entry, " +
                  "such as Tomcat's context.xml or Jetty's jetty-web.xml.");
        }
      }
    }
    catch (NamingException e) {
      log.warn("Could not create InitialContext to check for BeanManager reference in JNDI: " + e.getExplanation());
    }
  }

  public void contextDestroyed(ServletContextEvent arg0) {}
}
