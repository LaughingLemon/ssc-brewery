package guru.sfg.brewery.web.controllers.integration.api;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.web.controllers.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.BEER_1_UPC;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestTest extends BaseIntegrationTest {

    @Test
    public void testRestBeerList() throws Exception {
        mvc.perform(get("/api/v1/beer").with(httpBasic("user", "password")))
           .andExpect(status().isOk());
    }

    @Test
    public void testRestBeerItem() throws Exception {
        final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
        UUID uuid = UUID.fromString(id);

        when(beerRepository.findById(uuid)).thenReturn(Optional.of(Beer.builder()
                                                                       .id(uuid)
                                                                       .build()));

        mvc.perform(get("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                            .with(httpBasic("user", "password")))
           .andExpect(status().isOk());
    }

    @Test
    public void deleteBeerById() throws Exception {
        final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
        UUID uuid = UUID.fromString(id);

        mvc.perform(delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                            .with(csrf())
                            .header("Api-key", "spring")
                            .header("Api-secret", "password"))
           .andExpect(status().isNoContent());

        verify(beerService).deleteById(uuid);
    }

    @Test
    public void deleteBeerByIdWithUserRole() throws Exception {
        mvc.perform(delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                            .with(csrf())
                            .header("Api-key", "user")
                            .header("Api-secret", "password"))
           .andExpect(status().is4xxClientError());

        verifyNoInteractions(beerService);
    }

    @Test
    public void deleteBeerByIdWithCustomerRole() throws Exception {
        mvc.perform(delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                            .with(csrf())
                            .header("Api-key", "scott")
                            .header("Api-secret", "password"))
           .andExpect(status().is4xxClientError());

        verifyNoInteractions(beerService);
    }

    @Test
    public void deleteBeerByIdInvalid() throws Exception {
        mvc.perform(delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                            .with(csrf()))
           .andExpect(status().isUnauthorized());

        verifyNoInteractions(beerService);
    }

    @Test
    public void testRestBeerByUPC() throws Exception {
        mvc.perform(get("/api/v1/beerUpc/" + BEER_1_UPC).with(httpBasic("user", "password")))
           .andExpect(status().isOk());
    }

}
