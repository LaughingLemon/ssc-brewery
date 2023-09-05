package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.configuration.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest
@Import({SecurityConfiguration.class})
public class BeerControllerSecurityTest extends BaseIntegrationTest {

    @WithMockUser()
    @Test
    public void findBeers() throws Exception {
        mvc.perform(get("/beers/find"))
           .andExpect(status().isOk())
           .andExpect(view().name("beers/findBeers"))
           .andExpect(model().attributeExists("beer"));
        verifyNoInteractions(beerRepository);
    }

    @Test
    public void findBeersWithBasicAuthUser() throws Exception {
        mvc.perform(get("/beers/find").with(httpBasic("user", "password")))
           .andExpect(status().isOk())
           .andExpect(view().name("beers/findBeers"))
           .andExpect(model().attributeExists("beer"));
        verifyNoInteractions(beerRepository);
    }

    @Test
    public void findBeersWithBasicAuthSpringUser() throws Exception {
        mvc.perform(get("/beers/find").with(httpBasic("spring", "password")))
           .andExpect(status().isOk())
           .andExpect(view().name("beers/findBeers"))
           .andExpect(model().attributeExists("beer"));
        verifyNoInteractions(beerRepository);
    }

    @Test
    public void findBeersWithBasicAuthAdmin() throws Exception {
        mvc.perform(get("/beers/find").with(httpBasic("admin", "password")))
           .andExpect(status().isOk())
           .andExpect(view().name("beers/findBeers"))
           .andExpect(model().attributeExists("beer"));
        verifyNoInteractions(beerRepository);
    }

}
