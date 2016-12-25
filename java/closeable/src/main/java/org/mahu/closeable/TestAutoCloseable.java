package org.mahu.closeable;

public class TestAutoCloseable implements AutoCloseable {

    private TestBoolean isCloseCalled;

    public TestAutoCloseable(final TestBoolean value) {
        this.isCloseCalled = value;
    }

    public void bla() {
        // do nothing
    }

    @Override
    public void close() {
        isCloseCalled.setTrue();
    }

}
