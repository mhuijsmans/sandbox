package org.mahu.proto.junit;

public class Class1 {

    private final Class2 class2;

    public Class1(final Class2 class2) {
        this.class2 = class2;
    }

    public void print() {
        class2.print();
    }
}
