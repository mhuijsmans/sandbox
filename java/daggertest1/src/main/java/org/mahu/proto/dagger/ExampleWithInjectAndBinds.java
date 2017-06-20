package org.mahu.proto.dagger;

import javax.inject.Inject;

import dagger.Binds;
import dagger.Component;
import dagger.Module;

public class ExampleWithInjectAndBinds {

    interface Console {
        void println(String msg);
    }

    static class TextConsole implements Console {

        // Inject constructor needed so that dagger it can create an object
        // What is rationale for @Inject annotation here?
        @Inject
        public TextConsole() {
        }

        @Override
        public void println(String msg) {
            System.out.println(msg);
        }
    }

    // Because ConsolePrinter has a @Inject annotated ctor, no further plumbing
    // code is needed for this class.
    public static class ConsolePrinter {
        private final Console console;

        @Inject
        public ConsolePrinter(final Console console) {
            this.console = console;
        }

        public void printMsg(String msg) {
            console.println(msg);
        }
    }

    // Console is an interface. This modules defines for dagger a mapping to
    // implementation. But because @Binds is used the implementation class must
    // have a default ctor with @Inject so that dagger knows that it is allowed
    // to create an instance.
    @Module
    static abstract class ConsoleModule {
        @Binds
        public abstract Console provideConsole(TextConsole console);
    }

    @Component(modules = { ConsoleModule.class })
    interface HelloWorldApp {
        ConsolePrinter getPrinter();
    }

    public static void main(String[] args) {
        HelloWorldApp app = DaggerExampleWithInjectAndBinds_HelloWorldApp.create();
        app.getPrinter().printMsg("Hello World!");
    }
}
