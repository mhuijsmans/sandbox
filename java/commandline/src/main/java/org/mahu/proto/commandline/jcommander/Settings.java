package org.mahu.proto.commandline.jcommander;

import com.beust.jcommander.Parameter;

public class Settings {

	@Parameter(names = "-url", description = "Server address", required = true)
	private String url;

	@Parameter(names = "-token", description = "Authentication token", required = false)
	private String authenticationToken;

	@Parameter(names = "-month", description = "Number of month (1-12) for timesheet", required = true, validateWith = MonthValidator.class)
	private Integer month;

	@Parameter(names = "-year", description = "Year", required = false)
	private Integer year;

	public String getUrl() {
		return url;
	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	public  Integer getMonth() {
		return month;
	}

	public  Integer getYear() {
		return year;
	}
	
	

}