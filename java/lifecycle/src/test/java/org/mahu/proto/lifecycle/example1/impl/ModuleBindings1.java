package org.mahu.proto.lifecycle.example1.impl;

import org.mahu.proto.lifecycle.example1.ExampleInterface1;
import org.mahu.proto.lifecycle.example1.ExampleInterface2;
import org.mahu.proto.lifecycle.example1.ExampleInterface3;
import org.mahu.proto.lifecycle.impl.AbstractServiceModule;

import com.google.inject.Scopes;

public class ModuleBindings1 extends AbstractServiceModule {
    
    public ModuleBindings1() {
        super(ModuleBindings1.class.getPackage().getName());
    }    

    @Override
    protected void configure() {
          
        bindService(ExampleInterface2.class).to(ExampleInterface2Impl.class).in(Scopes.SINGLETON);
        bind(ExampleInterface3.class).to(ExampleInterface3Impl.class).in(Scopes.SINGLETON);         
        bindService(ExampleInterface1.class).to(ExampleInterface1Impl.class).in(Scopes.SINGLETON);

        bindServiceListener();
    }
}
