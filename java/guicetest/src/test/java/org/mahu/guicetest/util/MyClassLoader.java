package org.mahu.guicetest.util;

/**
 * @author Artem Eroshenko eroshenkoam 7/8/13, 7:58 PM
 */
public class MyClassLoader extends ClassLoader {

    public MyClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    public Class<?> defineClassForName(String name, byte[] data) {
        return this.defineClass(name, data, 0, data.length);
    }

}
