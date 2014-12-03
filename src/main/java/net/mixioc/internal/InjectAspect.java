package net.mixioc.internal;


import net.mixioc.ServiceManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class InjectAspect {

    @Pointcut("execution(*.new(..)) && !within(InjectAspect)")
    public void allConstructorsInPackage() {
    }

    @Before("allConstructorsInPackage()")
    public void before(final JoinPoint thisJoinPoint) {
        ServiceManager.inject(thisJoinPoint.getThis());
    }

}