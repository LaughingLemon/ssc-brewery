package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.services.BeerService;
import guru.sfg.brewery.services.BreweryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public abstract class BaseIntegrationTest {
    @Autowired
    WebApplicationContext context;

    MockMvc mvc;

    @MockBean
    BeerRepository beerRepository;
    @MockBean
    BeerService beerService;
    @MockBean
    BeerInventoryRepository beerInventoryRepository;
    @MockBean
    BreweryService breweryService;
    @MockBean
    CustomerRepository customerRepository;

    @BeforeEach
    public void init() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                             .apply(springSecurity())
                             .build();
    }

    protected static Stream<Arguments> getStreamAdminAndCustomerUsers() {
        return Stream.of(Arguments.of("spring", "password"),
                         Arguments.of("scott", "password"));
    }

    protected static Stream<Arguments> getStreamUserAndCustomerUsers() {
        return Stream.of(Arguments.of("user", "password"),
                         Arguments.of("scott", "password"));
    }


}
