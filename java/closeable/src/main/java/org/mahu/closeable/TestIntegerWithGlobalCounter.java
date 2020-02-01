package org.mahu.closeable;

public class TestIntegerWithGlobalCounter {
	
	public static int globalCntr = 0;

    private int value = 0;

    public void setTrue() {
        value = ++globalCntr;
    }

    int getValue() {
        return value;
    }

}
