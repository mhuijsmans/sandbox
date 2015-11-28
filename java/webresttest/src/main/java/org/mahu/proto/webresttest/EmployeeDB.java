package org.mahu.proto.webresttest;

import java.util.Collection;

public interface EmployeeDB {

	public Collection<Employee> values();

	public Employee getEmployee(String id);
}