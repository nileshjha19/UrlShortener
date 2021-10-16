package com.employee.springbootbackend.repository;

import com.employee.springbootbackend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    //No need to write anything here since JpaRepository has everything including CRUD

}
