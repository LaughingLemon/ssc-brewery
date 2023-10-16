package guru.sfg.brewery.web.controllers.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
public class BreweryControllerTest  extends BaseIntegrationTest {

    @Test
    public void listBreweriesWithCustomer() throws Exception {
        mvc.perform(get("/brewery/breweries").with(httpBasic("scott", "password")))
           .andExpect(status().isOk())
           .andExpect(view().name("breweries/index"))
           .andExpect(model().attributeExists("breweries"));
    }

    @Test
    public void listBreweriesWithAdmin() throws Exception {
        mvc.perform(get("/brewery/breweries").with(httpBasic("spring", "password")))
           .andExpect(status().isOk())
           .andExpect(view().name("breweries/index"))
           .andExpect(model().attributeExists("breweries"));
    }

    @Test
    public void listBreweriesWithUser() throws Exception {
        mvc.perform(get("/brewery/breweries").with(httpBasic("user", "password")))
           .andExpect(status().isForbidden());
    }

    @Test
    public void listBreweriesWithAnon() throws Exception {
        mvc.perform(get("/brewery/breweries"))
           .andExpect(status().isUnauthorized());
    }

}
