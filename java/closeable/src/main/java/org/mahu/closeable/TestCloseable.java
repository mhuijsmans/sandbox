package org.mahu.closeable;

import java.io.Closeable;
import java.io.IOException;

public class TestCloseable implements Closeable {

    private TestBoolean isCloseCalled;

    public TestCloseable(final TestBoolean value) {
        this.isCloseCalled = value;
    }

    public void bla() {
        // do nothing
    }

    @Override
    public void close() throws IOException {
        isCloseCalled.setTrue();
    }

}
