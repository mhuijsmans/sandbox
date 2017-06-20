Objective: explore usage of Dagger 2, including testing

Create component: 
-----------------
use builder().build() or create()?

create() is a wrapper for new Builder().build();

    public static void main(String[] args) {
        IExampleApp app = DaggerIExampleApp.builder().build();
        app.getPrinter().printMsg("Hello World!");
    }
    
    public static void main(String[] args) {
        IExampleApp app = DaggerIExampleApp.create();
        app.getPrinter().printMsg("Hello World!");
    } 
    
Refactoring:
------------
Renaming Example.java to ExampleNoInject.java
resulted in error, because 
HelloWorldApp app = DaggerExample_HelloWorldApp.create();
was not updated to
HelloWorldApp app = DaggerExampleNoInject_HelloWorldApp.create(); 

Nice examples:
--------------
https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2    