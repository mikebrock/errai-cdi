package org.jboss.errai.cdi.rebind;

import org.jboss.errai.ioc.rebind.ioc.IOCExtension;
import org.jboss.errai.ioc.rebind.ioc.InjectionPoint;

import javax.enterprise.inject.Produces;

/**
 * @author Mike Brock .
 */
public class ProducerExtension extends IOCExtension<Produces> {
    public ProducerExtension(Class<Produces> decoratesWith) {
        super(decoratesWith);
    }

    @Override
    public String generateDecorator(InjectionPoint<Produces> ctx) {

        String ref = ctx.getInjectionContext().getInjector(ctx.getType()).getType(ctx);



        return null;
    }
}
