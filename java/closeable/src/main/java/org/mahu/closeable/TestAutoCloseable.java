package org.mahu.closeable;

import java.util.Optional;

public class TestAutoCloseable implements AutoCloseable {

    private TestBoolean isCloseCalled;
    private Optional<Runnable> r;

    public TestAutoCloseable(final TestBoolean value) {
        this(value, null);
    }
    
    public TestAutoCloseable(final TestBoolean value, Runnable r) {
        this.isCloseCalled = value;
        this.r = Optional.ofNullable(r);
        callRunnable();        
    }

    public void bla() {
    }

	private void callRunnable() {
		if (r.isPresent()) {
			   r.get().run();
		   }
	}

    @Override
    public void close() {
        isCloseCalled.setTrue();
    }

}
