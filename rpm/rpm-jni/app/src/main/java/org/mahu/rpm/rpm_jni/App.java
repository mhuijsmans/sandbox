package org.mahu.rpm.rpm_jni;

import org.mahu.rpm.rpm_jni.api.HelloWorldPrinter;

import com.google.common.base.Preconditions;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World from java!" );
        HelloWorldPrinter printer = new NativeHelloWorldPrinter();
        System.out.println( "Hello World, jni through interface!" );
        // I wanted to test in the project use of nar + jar archives
        Preconditions.checkNotNull(printer);
        System.out.println( "Hello World from java-guava!" );
        printer.printHelloWorld();
    }
}
