package org.mahu.proto.webresttest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

@Named
public class EmployeeDBImpl implements EmployeeDB {

	private final static Map<String, Employee> employees = new HashMap<String, Employee>();

	static {
		Employee employee1 = new Employee();
		employee1.setEmployeeId("100");
		employee1.setEmployeeName("James Bond");
		employees.put(employee1.getEmployeeId(), employee1);

		Employee employee2 = new Employee();
		employee2.setEmployeeId("200");
		employee2.setEmployeeName("Boris Yeltsin");
		employees.put(employee2.getEmployeeId(), employee2);
	}

	public Collection<Employee> values() {
		return employees.values();
	}

	public Employee getEmployee(String id) {
		return employees.get(id);
	}
}
