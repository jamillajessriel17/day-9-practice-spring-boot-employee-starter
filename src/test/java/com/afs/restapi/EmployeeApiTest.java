package com.afs.restapi;

import com.afs.restapi.entity.Employee;
import com.afs.restapi.repository.EmployeeJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeApiTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private EmployeeJpaRepository employeeJpaRepository;

    @BeforeEach
    void setUp() {
        employeeJpaRepository.deleteAll();
    }

    @Test
    void should_update_employee_age_and_salary() throws Exception {
        Employee previousEmployee = new Employee(1L, "zhangsan", 22, "Male", 1000);
        Employee savedEmployee = employeeJpaRepository.save(previousEmployee);
        Employee employeeUpdateRequest = new Employee(1L, "lisi", 24, "Female", 2000);
        ObjectMapper objectMapper = new ObjectMapper();
        String updatedEmployeeJson = objectMapper.writeValueAsString(employeeUpdateRequest);
        mockMvc.perform(put("/employees/{id}", savedEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedEmployeeJson))
                .andExpect(MockMvcResultMatchers.status().is(204));

        Optional<Employee> optionalEmployee = employeeJpaRepository.findById(savedEmployee.getId());
        assertTrue(optionalEmployee.isPresent());
        Employee updatedEmployee = optionalEmployee.get();
        Assertions.assertEquals(employeeUpdateRequest.getAge(), updatedEmployee.getAge());
        Assertions.assertEquals(employeeUpdateRequest.getSalary(), updatedEmployee.getSalary());
        Assertions.assertEquals(savedEmployee.getId(), updatedEmployee.getId());
        Assertions.assertEquals(savedEmployee.getName(), updatedEmployee.getName());
        Assertions.assertEquals(savedEmployee.getGender(), updatedEmployee.getGender());
    }

    @Test
    void should_create_employee() throws Exception {
        Employee employee = getEmployeeBob();

        ObjectMapper objectMapper = new ObjectMapper();
        String employeeRequest = objectMapper.writeValueAsString(employee);
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeRequest))
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(employee.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(employee.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value(employee.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.salary").value(employee.getSalary()));
    }

    @Test
    void should_find_employees() throws Exception {
        Employee employee = getEmployeeBob();
        Employee savedEmployee = employeeJpaRepository.save(employee);

        mockMvc.perform(get("/employees"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(savedEmployee.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(savedEmployee.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(savedEmployee.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender").value(savedEmployee.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].salary").value(savedEmployee.getSalary()));
    }

    @Test
    void should_find_employee_by_id() throws Exception {
        Employee employee = getEmployeeBob();
        Employee savedEmployee = employeeJpaRepository.save(employee);

        mockMvc.perform(get("/employees/{id}", savedEmployee.getId()))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedEmployee.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(employee.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(employee.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value(employee.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.salary").value(employee.getSalary()));
    }

    @Test
    void should_delete_employee_by_id() throws Exception {
        Employee employee = getEmployeeBob();
        Employee employeeToBeDeleted = employeeJpaRepository.save(employee);
        mockMvc.perform(delete("/employees/{id}", employeeToBeDeleted.getId()))
                .andExpect(MockMvcResultMatchers.status().is(204));

        assertTrue(employeeJpaRepository.findById(employeeToBeDeleted.getId()).isEmpty());
    }

    @Test
    void should_find_employee_by_gender() throws Exception {
        Employee employee = getEmployeeBob();
        Employee savedEmployee = employeeJpaRepository.save(employee);

        mockMvc.perform(get("/employees?gender={0}", "Male"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(savedEmployee.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(employee.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(employee.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender").value(employee.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].salary").value(employee.getSalary()));
    }

    @Test
    void should_find_employees_by_page() throws Exception {
        Employee saveEmployeeBob = employeeJpaRepository.save(getEmployeeBob());
        Employee saveEmployeeSusan = employeeJpaRepository.save(getEmployeeSusan());
        employeeJpaRepository.save(getEmployeeLily());

        mockMvc.perform(get("/employees")
                        .param("pageNumber", "1")
                        .param("pageSize", "2"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(saveEmployeeBob.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(saveEmployeeBob.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(saveEmployeeBob.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender").value(saveEmployeeBob.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].salary").value(saveEmployeeBob.getSalary()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(saveEmployeeSusan.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(saveEmployeeSusan.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].age").value(saveEmployeeSusan.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].gender").value(saveEmployeeSusan.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].salary").value(saveEmployeeSusan.getSalary()));
    }

    private static Employee getEmployeeBob() {
        Employee employee = new Employee();
        employee.setName("Bob");
        employee.setAge(22);
        employee.setGender("Male");
        employee.setSalary(10000);
        return employee;
    }

    private static Employee getEmployeeSusan() {
        Employee employee = new Employee();
        employee.setName("Susan");
        employee.setAge(23);
        employee.setGender("Female");
        employee.setSalary(11000);
        return employee;
    }

    private static Employee getEmployeeLily() {
        Employee employee = new Employee();
        employee.setName("Lily");
        employee.setAge(24);
        employee.setGender("Female");
        employee.setSalary(12000);
        return employee;
    }
}