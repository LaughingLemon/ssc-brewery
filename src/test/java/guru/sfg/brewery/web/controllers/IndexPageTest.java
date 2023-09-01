package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.configuration.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest
@Import({SecurityConfiguration.class})
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
        mvc.perform(get("/beers/new").with(httpBasic("admin", "Ch4ng3Me!")))
           .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"));
    }

}
