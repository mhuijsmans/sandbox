package org.mahu.proto.lib;

import java.util.function.Consumer;

public class Aclass {
	
	public void hello(Consumer<String> consumer) {
		consumer.accept("###### Aclass.hello");
	}
	
}
