package org.mahu.proto.webresttest;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/webservice")
@Named
public class EmployeeController {
	
	public final static String HELLO_MSG = "Hello World!!!";
	
	@Inject
	private EmployeeDB employeeDB;
 
     @GET
     @Path("/hello")
     @Produces("text/plain")
     public String hello(){
         return HELLO_MSG;    
     }
     
     @GET
     @Path("/message/{message}")
     @Produces("text/plain")
     public String showMsg(@PathParam("message") String message){
         return message;    
     }
     
     @GET
     @Path("/employees")
     @Produces("application/xml")
     public List<Employee> listEmployees(){
         return new ArrayList<Employee>(employeeDB.values());
     }
     
     @GET
     @Path("/employee/{employeeid}")
     @Produces("application/xml")
     public Employee getEmployee(@PathParam("employeeid")String employeeId){
         return employeeDB.getEmployee(employeeId);        
     }
     
     @GET
     @Path("/json/employees/")
     @Produces("application/json")
     public List<Employee> listEmployeesJSON(){
         return new ArrayList<Employee>(employeeDB.values());
     }

     @GET
     @Path("/json/employee/{employeeid}")
     @Produces("application/json")
     public Employee getEmployeeJSON(@PathParam("employeeid")String employeeId){
         return employeeDB.getEmployee(employeeId);       
     }
  
}