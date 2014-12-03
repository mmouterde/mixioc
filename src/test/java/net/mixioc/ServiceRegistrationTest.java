package net.mixioc;

import net.mixioc.internal.ServiceRegistration;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServiceRegistrationTest {

    @Test
    public void computeMatcher() {

        Method isMatchingMethod;
        try {
            isMatchingMethod = ServiceRegistration.class.getDeclaredMethod("computeMatcher", String.class);
            isMatchingMethod.setAccessible(true);

            Assert.assertTrue((Boolean) isMatchingMethod.invoke(new ServiceRegistration(null, "toto"), "toto"));
            Assert.assertTrue((Boolean) isMatchingMethod.invoke(new ServiceRegistration(null, "arg1=toto"), "arg1=toto"));
            Assert.assertTrue((Boolean) isMatchingMethod.invoke(new ServiceRegistration(null, "arg1=toto,arg2=toto"), "arg1=toto"));
            Assert.assertFalse((Boolean) isMatchingMethod.invoke(new ServiceRegistration(null, "0arg1=totos"), "arg1=toto"));
            Assert.assertFalse((Boolean) isMatchingMethod.invoke(new ServiceRegistration(null, "arg1=toto"), "rg1=toto"));
            Assert.assertTrue((Boolean) isMatchingMethod.invoke(new ServiceRegistration(null, "arg1=toto,arg2=coucou,arg3=mouette"), "arg2=coucou"));

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
