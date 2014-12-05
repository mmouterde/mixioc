package net.mixioc.entities.expected;


import net.mixioc.ServiceManager;
import net.mixioc.annotation.Inject;
import net.mixioc.entities.TestService;

public class TestClass {

    @Inject
    private TestService service;

    public TestClass() {
        ServiceManager.inject(this);
        service.sayHello();
    }

    public TestClass(int a) {
        ServiceManager.inject(this);
        a = 9;
        service.sayHello();
    }

    // Contructor for test
    public TestClass(Object a) {
        ServiceManager.inject(this);
    }

    public TestService getInjectedService(){
        return service;
    }
}
