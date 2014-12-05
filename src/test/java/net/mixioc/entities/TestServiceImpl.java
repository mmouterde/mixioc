package net.mixioc.entities;

import net.mixioc.annotation.Service;

@Service
public class TestServiceImpl  implements TestService{

    public void sayHello(){
        System.out.println("Hello");
    }
}
