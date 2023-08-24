package com.afs.restapi.service;

import com.afs.restapi.exception.EmployeeNotFoundException;
import com.afs.restapi.repository.EmployeeJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

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
}
