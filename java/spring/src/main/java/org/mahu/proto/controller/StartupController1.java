package org.mahu.proto.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class StartupController1 {
	
	private static final long THIRTY_SECONDS = 30 * 1000;
	
	private final Object lock = new Object();
	private final List<String> samples = new LinkedList<String>();
	
	@PostMapping(value="/startup", produces = "text/plain")
	public String getValueXml() throws InterruptedException {
		return removeFirstValueFromList();
	}
	
	@PostMapping(value="/startup-value", consumes = "text/plain")
	public void setValue(@RequestBody String value) throws InterruptedException {
		addValueFromToList(value);
	}	

	private void addValueFromToList(String value) {
		synchronized(lock) {
			samples.add(value);
			lock.notify();
		}
	}

	private String removeFirstValueFromList() throws InterruptedException {
		synchronized(lock) {
			if (samples.isEmpty()) {
				lock.wait(THIRTY_SECONDS);
				if (samples.isEmpty()) {
					throw new RuntimeException("No value found");
				}
			}
			return samples.remove(0);
		}
	}

}
