package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.configuration.SecurityConfiguration;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.web.model.BeerDto;
import guru.sfg.brewery.web.model.BeerPagedList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({SecurityConfiguration.class})
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
    public void testRestBeerByUPC() throws Exception {
        String upc = "567746";
        when(beerService.findBeerByUpc(upc)).thenReturn(BeerDto.builder()
                                                               .upc(upc)
                                                               .build());

        mvc.perform(get("/api/v1/beerUpc/" + upc))
           .andExpect(status().isOk());
    }

}
