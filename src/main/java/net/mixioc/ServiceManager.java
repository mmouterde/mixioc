package net.mixioc;

import net.mixioc.annotation.Inject;
import net.mixioc.annotation.Service;
import net.mixioc.internal.ServiceInvocationHandler;
import net.mixioc.internal.ServiceListAsOneInvocationHandler;
import net.mixioc.internal.ServiceListInvocationHandler;
import net.mixioc.internal.ServiceRegistration;
import net.mixioc.utils.HashMapArray;
import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ServiceManager {

    private static Logger LOG = LoggerFactory.getLogger(ServiceManager.class);

    protected static HashMapArray<Class, ServiceRegistration> repository = new HashMapArray<>();

    private static Reflections reflections;

    protected ServiceManager() {
    }

    public static void init(String rootPackage) {
        reflections = new Reflections(rootPackage);
        handleServiceAnnotedClasses(Service.class, null);
    }

    public static void handleServiceAnnotedClasses(Class annotationClass, Class defaultInterface) {

        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(annotationClass);

        for (Class aClass : annotated) {
            Object service;
            try {
                service = aClass.newInstance();

                Annotation annotation = aClass.getAnnotation(annotationClass);
                Class serviceInterface = null;

                if (defaultInterface != null && isMethodDeclared(annotationClass, "interfaceClazz")) {
                    serviceInterface = ServiceManager.class.getClassLoader().loadClass(defaultInterface.getMethod("interfaceClazz").invoke(annotation).toString());
                }
                if (serviceInterface == null) {
                    serviceInterface = getFirstInterface(aClass);
                }
                if (serviceInterface == null) {
                    serviceInterface = defaultInterface;
                }


                if (serviceInterface != null) {
                    if (annotationClass.equals(Service.class)) {
                        registerService(serviceInterface, service, (String) annotationClass.getMethod("parameters").invoke(annotation));
                    } else {
                        registerService(serviceInterface, service, (String) annotationClass.getMethod("value").invoke(annotation));
                    }
                } else {
                    LOG.error("Error while Service Discovery : No Interface for " + aClass);
                }
            } catch (InstantiationException er) {
                LOG.error("This class does not have constructor with no param.", er);
            } catch (Exception e) {
                LOG.error("Error while Service Discovery", e);
            }
        }
    }

    private static boolean isMethodDeclared(Class aClass, String methodName, Class... parameterTypes) {
        for (Method method : aClass.getMethods()) {
            if (method.getName().equalsIgnoreCase(methodName) && Arrays.equals(method.getParameterTypes(), parameterTypes)) {
                return true;
            }
        }
        return false;
    }

    private static Class getFirstInterface(Class aClass) throws ClassNotFoundException {
        if (aClass.getInterfaces().length == 1) {
            return aClass.getInterfaces()[0];
        } else if (aClass.getSuperclass() != null) {
            return getFirstInterface(aClass.getSuperclass());
        } else {
            return null;
        }
    }

    public static <T> void registerService(Class<T> serviceClass, T service) {
        registerService(serviceClass, service, null);
    }

    public static <T> void registerService(Class<T> serviceClass, T service, String parameters) {
        LOG.debug("Service Registration for service " + serviceClass.getCanonicalName() + " implemented by " + service + " with parameters " + parameters);
        repository.append(serviceClass, new ServiceRegistration(service, parameters));
    }

    public static <T> T getService(Class<T> serviceClass, String parameterFilter) {
        List<T> result = getServices(serviceClass, parameterFilter);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        return null;
    }

    private static <T> T getServiceProxy(Class<T> serviceClass, String parameterFilter) {
        Object proxy = Proxy.newProxyInstance(ServiceManager.class.getClassLoader(), new Class[]{serviceClass}, new ServiceInvocationHandler(serviceClass, parameterFilter));
        return (T) proxy;
    }

    public static <T> Iterable<T> getServicesProxy(Class<T> serviceClass, String parameterFilter) {
        Object proxy = Proxy.newProxyInstance(ServiceManager.class.getClassLoader(), new Class[]{Iterable.class}, new ServiceListInvocationHandler(serviceClass, parameterFilter));
        return (Iterable<T>) proxy;
    }

    public static <T> T getService(Class<T> serviceClass) {
        List<T> result = getServices(serviceClass, null);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        return null;
    }

    public static <T> List<T> getServices(Class<T> serviceClass) {
        List<T> result = new ArrayList<>();
        for (ServiceRegistration registeredService : repository.get(serviceClass)) {
            result.add((T) registeredService.getService());
        }
        return result;
    }

    public static <T> List<T> getServices(Class<T> serviceClass, String contraints) {
        List<T> result = new ArrayList<T>();
        for (ServiceRegistration registeredService : repository.get(serviceClass)) {
            if (contraints == null || registeredService.matchs(contraints)) {
                Object service = registeredService.getService();
                result.add((T) service);
            }
        }
        return result;
    }

    public static <T> T getServicesAsOne(Class<T> serviceClass) {
        return getServicesAsOne(serviceClass, null);
    }

    public static <T> T getServicesAsOne(Class<T> serviceClass, String contraints) {
        Object proxy = Proxy.newProxyInstance(ServiceManager.class.getClassLoader(), new Class[]{serviceClass}, new ServiceListAsOneInvocationHandler(serviceClass, contraints));
        return (T) proxy;
    }

    public static void inject(Object object) {
        for (Field field : getFieldsToInject(object.getClass(), object)) {
            try {
                Object value = getFieldValue(object, field);
                field.set(object, value);
                LOG.debug("Inject \'" + field.getName() + "\' for " + object.getClass() + " with " + value);
            } catch (IllegalAccessException e) {
                LOG.error("Fail to inject field \'" + field.getName() + "\' for " + object.getClass());
            }
        }
    }

    private static Object getFieldValue(Object object, Field field) throws IllegalAccessException {
        Class type = field.getType();
        if (type.equals(Logger.class)) {
            return org.slf4j.LoggerFactory.getLogger(object.getClass().getSimpleName());
        } else if (type.equals(Iterable.class)) {
            return getServicesProxy(getServiceInterfaceFromGenerics(field), getAnnotationParam(field));
        } else if (type.isInterface() && !(field.getGenericType() instanceof ParameterizedType)) {
            return getServiceProxy(type, getAnnotationParam(field));
        } else {
            LOG.error("Fail to inject field \'" + field.getName() + "\' for " + object.getClass());
            return null;
        }
    }

    private static Class getServiceInterfaceFromGenerics(Field field) {
        return (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    private static String getAnnotationParam(Field field) {
        String value = field.getAnnotation(Inject.class).value();
        if (StringUtils.isNotEmpty(value)) {
            return value;
        } else {
            return null;
        }
    }

    private static List<Field> getFieldsToInject(Class aClass, Object object) {
        ArrayList<Field> result = new ArrayList<>();
        //Get declared fields
        for (Field field : aClass.getDeclaredFields()) {
            //Keep @Inject annoted
            if (field.isAnnotationPresent(Inject.class)) {
                //make it accessible by reflection
                field.setAccessible(true);
                //Keep only if null
                try {
                    if (field.get(object) == null) {
                        result.add(field);
                    }
                } catch (IllegalAccessException e) {
                    LOG.error("fail to read field" + field, e);
                }
            }
        }

        if (aClass.getSuperclass() != null) {
            result.addAll(getFieldsToInject(aClass.getSuperclass(), object));
        }
        return result;
    }
}
