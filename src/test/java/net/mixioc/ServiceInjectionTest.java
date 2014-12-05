package net.mixioc;

import net.mixioc.entities.TestClass;
import net.mixioc.entities.TestVisibilityClass;
import net.mixioc.internal.ServiceRegistration;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServiceInjectionTest {

    @Test
    public void simpleInject() {
        //Collect @Service impl and register it
        ServiceManager.init("net.mixioc.entities");

        //Create a bean instance
        TestClass bean = new TestClass(new Object());

        //"Manually" inject its @Inject properties
        ServiceManager.inject(bean);

        //Check if injection was done
        Assert.assertNotNull(bean.getInjectedService());

        //Check if service is called
        Exception exception = null;
       try{
            bean.getInjectedService().sayHello();
        }catch (Exception e){
           exception=e;
        }
        Assert.assertNull(exception);
    }


    @Test
    public void injectOnDifferentsVisibility() {
        //Collect @Service impl and register it
        ServiceManager.init("net.mixioc.entities");

        //Create a bean instance
        TestVisibilityClass bean = new TestVisibilityClass(new Object());

        //"Manually" inject its @Inject properties
        ServiceManager.inject(bean);

        //Check if injection was done
        Assert.assertNotNull(bean.getInjectedService1());

        Assert.assertNotNull(bean.getInjectedService2());

        Assert.assertNotNull(bean.getInjectedService3());

        Assert.assertNotNull(bean.getInjectedService4());

        Assert.assertNotNull(bean.getInjectedService5());

        Assert.assertNotNull(bean.getInjectedService6());

        Assert.assertNotNull(bean.getInjectedService7());

    }
}
