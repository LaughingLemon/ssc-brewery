package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.web.model.BeerDto;
import guru.sfg.brewery.web.model.BeerPagedList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestTest extends BaseIntegrationTest {

    @Test
    public void testRestBeerList() throws Exception {
        when(beerService.listBeers(anyString(), any(), any(), anyBoolean())).thenReturn(new BeerPagedList(List.of()));

        mvc.perform(get("/api/v1/beer"))
           .andExpect(status().isOk());
    }

    @Test
    public void testRestBeerItem() throws Exception {
        final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
        UUID uuid = UUID.fromString(id);

        when(beerRepository.findById(uuid)).thenReturn(Optional.of(Beer.builder()
                                                                       .id(uuid)
                                                                       .build()));

        mvc.perform(get("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f"))
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
        String upc = "567746";
        when(beerService.findBeerByUpc(upc)).thenReturn(BeerDto.builder()
                                                               .upc(upc)
                                                               .build());

        mvc.perform(get("/api/v1/beerUpc/" + upc))
           .andExpect(status().isOk());
    }

}
