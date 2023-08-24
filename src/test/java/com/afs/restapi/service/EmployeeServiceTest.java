package com.afs.restapi.service;

import com.afs.restapi.entity.Employee;
import com.afs.restapi.exception.EmployeeNotFoundException;
import com.afs.restapi.repository.EmployeeJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
public class EmployeeServiceTest {

    private EmployeeService employeeService;

    private EmployeeJpaRepository mockedEmployeeRepository;

    @BeforeEach
    void setUp() {
        mockedEmployeeRepository = Mockito.mock(EmployeeJpaRepository.class);
        employeeService = new EmployeeService(mockedEmployeeRepository);
    }

    @Test
    void should_return_EmployeeNotFoundException_when_findById_given_employee_service_and_unknown_employee_id() {
        //given
        long unknownEmployeeId = 3L;
        //when
        EmployeeNotFoundException employeeNotFoundException = Assertions.assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.findById(unknownEmployeeId));
        //then
        Assertions.assertEquals("employee id not found", employeeNotFoundException.getMessage());
    }

    @Test
    void should_not_update_employee_salary_when_update_given_employee_service_employee_details_with_null_salary() {
        //given
        Employee employeeToUpdate = new Employee(1L, "Jessriel", 25, "male", 2000);
        Employee employeeDetails = new Employee(null, "Jessriel", 26, "male", null);
        when(mockedEmployeeRepository.findById(employeeToUpdate.getId())).thenReturn(Optional.of(employeeToUpdate));
        //when
        employeeService.update(employeeToUpdate.getId(), employeeDetails);
        //then
        verify(mockedEmployeeRepository).save(argThat(tempEmployee -> {
            Assertions.assertEquals(employeeToUpdate.getSalary(), tempEmployee.getSalary());
            Assertions.assertEquals(employeeToUpdate.getId(), tempEmployee.getId());
            Assertions.assertEquals(employeeToUpdate.getName(), tempEmployee.getName());
            Assertions.assertEquals(employeeDetails.getAge(), tempEmployee.getAge());
            Assertions.assertEquals(employeeToUpdate.getGender(), tempEmployee.getGender());
            return true;
        }));
    }
}
