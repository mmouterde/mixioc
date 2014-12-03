package net.mixioc.internal;

import net.mixioc.ServiceManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * This class is a proxy to ensure to alway iterate on up to date list
 * without having to recall the serviceManager
 */
public class ServiceListInvocationHandler implements InvocationHandler {

    private Iterable<Object> wrappedServiceList = new ArrayList<>();

    private String parameters;
    private Class serviceInterface;

    public ServiceListInvocationHandler(Class serviceInterface, String parameters) {
        this.parameters = parameters;
        this.serviceInterface = serviceInterface;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.equals(Object.class.getMethod("toString"))) {
            return this.toString() + serviceInterface + "(" + parameters + ") = ";
        }

        wrappedServiceList = ServiceManager.getServices(serviceInterface, parameters);

        return method.invoke(wrappedServiceList, args);
    }

}
