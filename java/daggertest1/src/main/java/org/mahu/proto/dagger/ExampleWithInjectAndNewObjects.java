package org.mahu.proto.dagger;

import javax.inject.Inject;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

public class ExampleWithInjectAndNewObjects {

    interface Console {
        void println(String msg);
    }

    static class TextConsole implements Console {

        @Override
        public void println(String msg) {
            System.out.println(msg);
        }
    }

    // Because ConsolePrinter has a @Inject annotated ctor, no further plumbing
    // code is needed for this class.
    public static class ConsolePrinter {
        private final Console console;
        private final ConsolePrinter2 otherConsolePrinter;

        @Inject
        public ConsolePrinter(final Console console, final ConsolePrinter2 otherConsolePrinter) {
            this.console = console;
            this.otherConsolePrinter = otherConsolePrinter;
        }

        public void printMsg(String msg) {
            console.println(msg);
            otherConsolePrinter.printMsg(msg);
        }
    }

    public static class ConsolePrinter2 {
        private final Console console;

        @Inject
        public ConsolePrinter2(final Console console) {
            this.console = console;
        }

        public void printMsg(String msg) {
            console.println(msg);
        }
    }

    // Console is an interface, so dagger needs information on implementation.
    // This modules defined that
    @Module
    static class ConsoleModule {
        @Provides
        Console provideConsole() {
            return new TextConsole();
        }

        @Provides
        ConsolePrinter2 provideConsolePrinter2(final Console console) {
            return new ConsolePrinter2(console);
        }
    }

    @Component(modules = { ConsoleModule.class })
    interface HelloWorldApp {
        ConsolePrinter getPrinter();
    }

    public static void main(String[] args) {
        HelloWorldApp app = DaggerExampleWithInjectAndNewObjects_HelloWorldApp.create();
        app.getPrinter().printMsg("Hello World!");
    }
}
