mixioc
======

IOC Framework in Java : a mix of IOCs patterns (Service Locator &amp; Dependency Injection)

## A Short Summary about patterns

Inversion Of Control is a common pattern to remove dependencies among your code.

IoC is often assimilated to the Dependency Injection provided by Spring. However the are two implementations of IoC : Dependency Injection and Service Locator. Service Locator is the implemented IoC by OSGI specifications. Both of them are efficient and dev-friendly.

MixIOC is a little POC of framework created for Redige.net product that mixes this two implementations of IoC.

In a world, it provide *Service Locator implementation through a Dependency Injection layer*. So you can implement Whiteboard pattern of OSGI using Spring-like annotation.

## How to use it

### Annotations

### `ServiceManager`

Service Locator or OSGI user habitually call a singleton to register or unregister Pojo identified to an Interface.
ServiceManager is this singleton.
~~~java
//Register a service on runtime
ServiceManager.registerService(interfazz, myObjectInstance)
//Unregister a service on runtime
ServiceManager.unregisterService(myObjectInstance)

//Get all registered service for a given interfazz
ServiceManager.getServices(interfazz)
//Get the last registered service for a given interfazz
ServiceManager.getService(interfazz)

/*
 * Bonus : Get all registered service for a given interfazz as one proxy object.
 * Any method call on this object will call this method on each registered service.
 * 
 * The main use case is for Listeners (Observer/Observable Pattern)
 */
ServiceManager.getServicesAsOne(interfazz)
~~~

#### `@Service`
As Spring way, this annotation shoud be use on a Pojo that implement an interface. No specific interface is required, you just need one.
The annoted Object will be instancied on startup.

For Service Locator user, this annotion is a shortcut for `ServiceManager.registerService(interfazz, myObjectInstance)`.
On startup, All annoted objects will be instancied and registered as service for its interfazz.


#### `@Inject`
As Spring way, this annotation shoud be use on a Pojo properties. The type of property is use to define the service to inject. For each instance of the Object, (on constructor call) the property will be value from the ServiceManager.

For Service Locator user, this annotion is a shortcut for `ServiceManager.getService(interfazz)`

Note : There is a exception. You can directly use @Inject on a `org.slf4j.Logger` property. It is a shortcut for conventionnal `Logger LOG = LoggerFactory.getLogger(thisClass.class.getSimpleName());`

### Installation

- add dependency to MixIoC
- on startup call `ServiceManager.init("net.your.package.root")` This will collect by reflection all @Service annoted from this package root.
- If you use @Inject, add the JVM argument `-javaagent:{path\to\mixioc-*.jar}=net.your.package.root` This will inject on constructor call each @Inject annoted properties.
- (optionnal) you can extend ServiceManager. To make MixIoC use your custom ServiceManager add -DCustomServiceManager=net/your/package/YourClassExtendingServiceManager

### FAQ

#### With OSGI implementation, we can use LDAP parameters to filter services having the same interfazz, does mixioc provide the same feature ?

=> Not exactly. Using the third argument of methods getService* you can add contraints, but the syntax is not as complicated as LDAP. This is simply a comma-separated key=value. The operator use is AND.

~~~java
//Register a service pojo with contraints
ServiceManager.registerService(ParserServiceInterface.class,myHTMLParser,"format=html");
//Register a service pojo with contraints
ServiceManager.registerService(ParserServiceInterface.class,myPDFParser,"format=pdf");
...
//Get a specific service
ParserServiceInterface parserService = ServiceManager.getService(ParserServiceInterface.class,"format="+currentFormat);
~~~

#### My Pojo has several interface, how MixIoC will deal with this ?

=> MixIoc get the first one by default. If the first is not expected, use this syntax to be explicit :
~~~java
@Service(interfaceclazz=ParserServiceInterface.class)
~~~
As you know you can not use the same Annotation more than once, so you can create your own annotation to wrap `@Service`, see the example below extracted to ExporterService.java

~~~java 
/**
 * Alias for @Service(interfaceclazz=ExportService.class,parameters=value)
 *
 * @author martin_mo
 */
@Target({
        ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExporterService {

    String value();
}
~~~

Then add to the service discovery process your own annotation : 
~~~
 ServiceManager.init();
 //Add the annotation `ExporterService` as a Service annotation to the interface ExportService
 ServiceManager.handleServiceAnnotedClasses(ExporterService.class, ExportService.class);
~~~
#### My Pojo Service is abstract ?!
=> If your pojo implement an interface by inheriting an other class, MixIoc will get the first interface of the pojo (if any) then the first of its parent etc...

#### What about testing ?
=> You can Inject dependencies using this the inject method of ServiceManager and you can register mocked service.
~~~java
    @Before
    public void init(){
        ServiceManager.init();
        ServiceManager.inject(handler);
        ServiceManager.registerService(EditorService.class,mock(EditorService.class));
    }
~~~
