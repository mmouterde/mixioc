package net.mixioc.entities;


import net.mixioc.annotation.Inject;

public class TestVisibilityClass {

    @Inject
    private TestService service;

    @Inject
    public TestService service2;

    @Inject
    protected TestService service3;

    @Inject
    TestService service4;

    @Inject
    private static TestService service5;

    @Inject
    public static TestService service6;

    @Inject
    protected static TestService service7;

    @Inject
    TestService service8;

    public TestVisibilityClass() {
        service.sayHello();
    }

    public TestVisibilityClass(int a) {
        a = 9;
        service.sayHello();
    }

    // Contructor for test
    public TestVisibilityClass(Object a) {
    }

    public TestService getInjectedService1() {
        return service;
    }

    public TestService getInjectedService2() {
        return service2;
    }

    public TestService getInjectedService3() {
        return service3;
    }

    public TestService getInjectedService4() {
        return service4;
    }

    public TestService getInjectedService5() {
        return service5;
    }

    public TestService getInjectedService6() {
        return service6;
    }

    public TestService getInjectedService7() {
        return service7;
    }
}
