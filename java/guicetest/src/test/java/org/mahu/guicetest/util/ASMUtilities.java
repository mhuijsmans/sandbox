package org.mahu.guicetest.util;

import org.objectweb.asm.ClassWriter;

/**
 * @author Artem Eroshenko eroshenkoam 7/8/13, 8:03 PM
 */

public class ASMUtilities {

    public static Class<?> defineClass(String name, byte[] bytes) {
        return new MyClassLoader(MyClassLoader.class.getClassLoader()).defineClassForName(name, bytes);
    }

    public static Class<?> defineClass(String name, ClassWriter writer) {
        return defineClass(name, writer.toByteArray());
    }

}
