package org.mahu.proto.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class StateController {
	
	@GetMapping(value="/state", produces = "text/plain")
	public String getValueXml() {
		return "my-state";
	}	

}
