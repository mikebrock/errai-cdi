package org.jboss.errai.cdi.rebind;

import org.jboss.errai.ioc.rebind.ioc.IOCExtension;
import org.jboss.errai.ioc.rebind.ioc.InjectionPoint;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Mike Brock .
 */
public class ApplicationScopedExtension extends IOCExtension<ApplicationScoped> {
    public ApplicationScopedExtension(Class<ApplicationScoped> decoratesWith) {
        super(decoratesWith);
    }

    @Override
    public String generateDecorator(InjectionPoint<ApplicationScoped> ctx) {

    }
}
