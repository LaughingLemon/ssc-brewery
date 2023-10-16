package guru.sfg.brewery.web.controllers.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SpringBootTest
public class BeerControllerSecurityTest extends BaseIntegrationTest {

    @Test
    public void findBeersWithUserRole() throws Exception {
        mvc.perform(get("/beers/find").with(httpBasic("user", "password")))
           .andExpect(status().isOk())
           .andExpect(view().name("beers/findBeers"))
           .andExpect(model().attributeExists("beer"));
    }

    @Test
    public void findBeersWithAdminRole() throws Exception {
        mvc.perform(get("/beers/find").with(httpBasic("spring", "password")))
           .andExpect(status().isOk())
           .andExpect(view().name("beers/findBeers"))
           .andExpect(model().attributeExists("beer"));
    }

    @Test
    public void findBeersWithCustomerRole() throws Exception {
        mvc.perform(get("/beers/find").with(httpBasic("scott", "password")))
           .andExpect(status().isOk())
           .andExpect(view().name("beers/findBeers"))
           .andExpect(model().attributeExists("beer"));
    }

    @Test
    public void findBeersWithNoAccess() throws Exception {
        mvc.perform(get("/beers/find"))
           .andExpect(status().isUnauthorized());
    }

}
