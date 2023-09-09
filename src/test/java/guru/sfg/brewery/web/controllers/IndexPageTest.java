package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
public class IndexPageTest extends BaseIntegrationTest {

    @Test
    public void accessRoot() throws Exception {
        mvc.perform(get("/"))
           .andExpect(status().isOk());
    }

    @Test
    public void accessAnythingElse() throws Exception {
        mvc.perform(get("/beers/new"))
           .andExpect(status().isUnauthorized());
    }

    @Test
    public void accessAnythingElseSecure() throws Exception {
        mvc.perform(get("/beers/new").with(httpBasic("spring", "password")))
           .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"));
    }

}
