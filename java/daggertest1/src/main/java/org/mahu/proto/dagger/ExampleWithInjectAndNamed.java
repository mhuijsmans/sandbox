package org.mahu.proto.dagger;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

public class ExampleWithInjectAndNamed {

    interface Console {
        void println(String msg);
    }

    static class Text1Console implements Console {

        @Override
        public void println(String msg) {
            System.out.println("==1== " + msg);
        }
    }

    static class Text2Console implements Console {

        @Override
        public void println(String msg) {
            System.out.println("==2== " + msg);
        }
    }

    // Because ConsolePrinter has a @Inject annotated ctor, no further plumbing
    // code is needed for this class.
    public static class ConsolePrinter {
        private final Console console1;
        private final Console console2;

        @Inject
        public ConsolePrinter(@Named("1") final Console console1, @Named("2") final Console console2) {
            this.console1 = console1;
            this.console2 = console2;
        }

        public void printMsg(String msg) {
            console1.println(msg);
            console2.println(msg);
        }
    }

    // Console is an interface, so dagger needs information on implementation.
    // This modules defined that
    @Module
    static class Console1Module {
        @Provides
        @Named("1")
        Console provideConsole() {
            return new Text1Console();
        }
    }

    @Module
    static class Console2Module {
        @Provides
        @Named("2")
        Console provideConsole() {
            return new Text2Console();
        }
    }

    @Component(modules = { Console1Module.class, Console2Module.class })
    interface HelloWorldApp {
        ConsolePrinter getPrinter();
    }

    public static void main(String[] args) {
        HelloWorldApp app = DaggerExampleWithInjectAndNamed_HelloWorldApp.create();
        app.getPrinter().printMsg("Hello World!");
    }
}
