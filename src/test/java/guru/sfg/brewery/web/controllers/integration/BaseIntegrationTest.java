package guru.sfg.brewery.web.controllers.integration;

import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.services.BeerService;
import guru.sfg.brewery.services.BreweryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public abstract class BaseIntegrationTest {
    @Autowired
    protected WebApplicationContext context;

    protected MockMvc mvc;

    @SpyBean
    protected BeerRepository beerRepository;
    @SpyBean
    protected BeerService beerService;
    @SpyBean
    protected BeerInventoryRepository beerInventoryRepository;
    @SpyBean
    protected BreweryService breweryService;
    @SpyBean
    protected BeerOrderService beerOrderService;
    @SpyBean
    protected CustomerRepository customerRepository;

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
