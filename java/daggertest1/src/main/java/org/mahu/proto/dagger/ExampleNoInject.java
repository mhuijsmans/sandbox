package org.mahu.proto.dagger;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

// Example taken from: https://groups.google.com/forum/#!topic/dagger-discuss/STkfRUs_Wg0
// But it does not use an Inject
public class ExampleNoInject {

    interface Printer {
        void printMsg(String msg);
    }

    static class ConsolePrinter implements Printer {
        @Override
        public void printMsg(String msg) {
            System.out.println(msg);
        }
    }

    // This module makes an implementation available for an interface.
    @Module
    static class ConsoleModule {
        @Provides
        Printer providePrinter() {
            return new ConsolePrinter();
        }
    }

    @Component(modules = ConsoleModule.class)
    interface HelloWorldApp {
        Printer getPrinter();
    }

    public static void main(String[] args) {
        HelloWorldApp app = DaggerExampleNoInject_HelloWorldApp.create();
        app.getPrinter().printMsg("Hello World!");
    }
}
