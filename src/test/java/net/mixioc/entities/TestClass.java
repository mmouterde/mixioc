package net.mixioc.entities;


import net.mixioc.annotation.Inject;

public class TestClass {

    @Inject
    private TestService service;

    public TestClass() {
        service.sayHello();
    }

    public TestClass(int a) {
        a = 9;
        service.sayHello();
    }

    // Contructor for test
    public TestClass(Object a) {
    }

    public TestService getInjectedService(){
        return service;
    }
}
