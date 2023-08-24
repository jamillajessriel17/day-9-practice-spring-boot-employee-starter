package com.afs.restapi.service;

import com.afs.restapi.entity.Company;
import com.afs.restapi.exception.CompanyNotFoundException;
import com.afs.restapi.repository.CompanyJpaRepository;
import com.afs.restapi.repository.EmployeeJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
public class EmployeeServiceTest {


    @Autowired
    private CompanyService companyService;

    private CompanyJpaRepository mockedCompanyJpaRepository;

    private EmployeeJpaRepository mockedEmployeeJpaRepository;

    @BeforeEach
    void setUp() {
        mockedCompanyJpaRepository = Mockito.mock(CompanyJpaRepository.class);
        mockedEmployeeJpaRepository = Mockito.mock(EmployeeJpaRepository.class);
        companyService = new CompanyService(mockedCompanyJpaRepository, mockedEmployeeJpaRepository);
    }

    @Test
    void should_return_company_when_findById_given_company_service_valid_id() {
        //given
        Company helloCompany = getHelloCompany();
        when(mockedCompanyJpaRepository.findById(helloCompany.getId())).thenReturn(Optional.of(helloCompany));
        //when
        Company companyById = companyService.findById(helloCompany.getId());
        //then
        Assertions.assertEquals(helloCompany.getId(), companyById.getId());
        Assertions.assertEquals(helloCompany.getName(), companyById.getName());
    }


    private Company getHelloCompany() {
        Company helloCompany = new Company();
        helloCompany.setId(1L);
        helloCompany.setName("Hello Company");
        return helloCompany;
    }

}
