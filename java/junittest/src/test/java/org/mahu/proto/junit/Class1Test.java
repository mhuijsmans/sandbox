package org.mahu.proto.junit;

import org.junit.Test;

public class Class1Test {

    @Test
    public void test() {
        // Class2 exists in same package in src/main/java and src/text/java.
        // Eclipse doesn't like that.
        // Maven is fine and maven will use the class in test.
        Class1 class1 = new Class1(new Class2());
        class1.print();
    }

}
