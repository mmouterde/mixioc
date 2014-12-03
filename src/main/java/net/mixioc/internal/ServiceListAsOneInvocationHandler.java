package net.mixioc.internal;


import net.mixioc.ServiceManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * This class is a proxy to broadcast method call to a list of service sharing the same interface
 * (usefull for listener which return void)
 * without having to recall the serviceManager
 */
public class ServiceListAsOneInvocationHandler implements InvocationHandler {


    private String parameters;
    private Class serviceInterface;

    public ServiceListAsOneInvocationHandler(Class serviceInterface, String parameters) {
        this.parameters = parameters;
        this.serviceInterface = serviceInterface;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object lastResult = null;
        for (Object service : ServiceManager.getServices(serviceInterface, parameters)) {
            lastResult = method.invoke(service, args);
        }
        return lastResult;
    }

}
