package net.mixioc.internal;

import net.mixioc.ServiceManager;
import net.mixioc.annotation.Inject;
import org.slf4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * This class is a proxy for a service.
 * By getting a such proxy, you can be sure to call the latest service matching serviceInterface/Parameters
 * without having to recall the serviceManager
 */
public class ServiceInvocationHandler implements InvocationHandler {

    @Inject
    private Logger LOG;

    private Object wrappedService;

    private String parameters;
    private Class serviceInterface;

    public ServiceInvocationHandler(Class serviceInterface, String parameters) {
        this.parameters = parameters;
        this.serviceInterface = serviceInterface;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.equals(Object.class.getMethod("toString"))) {
            return this.toString() + serviceInterface + "(" + parameters + ") = " + wrappedService;
        }

        wrappedService = ServiceManager.getService(serviceInterface, parameters);
        if (wrappedService != null) {
            return method.invoke(wrappedService, args);
        } else {
            LOG.warn("No Service found for : " + serviceInterface + " method " + method.getName() + " had no effect");
            return null;
        }
    }


}
