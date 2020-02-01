package org.mahu.proto.controller;

import org.mahu.proto.service.ISimpleService;
import org.mahu.proto.service.ISimpleServiceRequestScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class SimpleController {
	
	private final ISimpleService simpleService;
	private final ISimpleServiceRequestScope simpleServiceRequestScopes;
	
	SimpleController(final ISimpleService simpleService, final ISimpleServiceRequestScope simpleServiceRequestScopes) {
		this.simpleService = simpleService;
		this.simpleServiceRequestScopes = simpleServiceRequestScopes;
	}
	
	@GetMapping(value="/simple-controller/xml", produces = "application/xml")
	public String getValueXml() {
		return simpleService.getValueXml();
	}
	
	@GetMapping(value="/simple-controller/text", produces = "text/plain")
	public String getValueText() {
		return simpleService.getValueText();
	}
	
	@GetMapping(value="/simple-controller-request-scope/xml", produces = "application/xml")
	public String getRequestScopeValueXml() {
		return simpleServiceRequestScopes.getValueXml();
	}		

}
