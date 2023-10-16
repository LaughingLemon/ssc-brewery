package guru.sfg.brewery.web.controllers.integration.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.web.controllers.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerOrderControllerTest extends BaseIntegrationTest {

    private Customer oddbins;
    private Customer wineWarehouse;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        oddbins = customerRepository.findByCustomerName("Oddbins").orElseThrow();
        wineWarehouse = customerRepository.findByCustomerName("Wine Warehouse").orElseThrow();
    }

    @Nested
    @DisplayName("Create Order Security Tests")
    class CreateOrderTests {
        @WithUserDetails("spring")
        @Test
        public void testPlaceOrderForAdmin() throws Exception {
            String jsoObject = objectMapper.writeValueAsString(BeerOrder.builder()
                                                                        .beerOrderLines(Set.of())
                                                                        .build());
            mvc.perform(post("/api/v1/customers/{customerId}/orders", wineWarehouse.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsoObject))
               .andExpect(status().isCreated());
        }

        @WithUserDetails("wineUser")
        @Test
        public void testPlaceOrderForCustomer() throws Exception {
            String jsoObject = objectMapper.writeValueAsString(BeerOrder.builder()
                                                                        .beerOrderLines(Set.of())
                                                                        .build());
            mvc.perform(post("/api/v1/customers/{customerId}/orders", wineWarehouse.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsoObject))
               .andExpect(status().isCreated());
        }

        @WithUserDetails("oddbinsUser")
        @Test
        public void testPlaceOrderForbidden() throws Exception {
            String jsoObject = objectMapper.writeValueAsString(BeerOrder.builder()
                                                                        .beerOrderLines(Set.of())
                                                                        .build());
            mvc.perform(post("/api/v1/customers/{customerId}/orders", wineWarehouse.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsoObject))
               .andExpect(status().isForbidden());
        }

        @Test
        public void testPlaceOrderUnauthorized() throws Exception {
            String jsoObject = objectMapper.writeValueAsString(BeerOrder.builder()
                                                                        .beerOrderLines(Set.of())
                                                                        .build());
            mvc.perform(post("/api/v1/customers/{customerId}/orders", wineWarehouse.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsoObject))
               .andExpect(status().isUnauthorized());
        }

    }

    @Nested
    @DisplayName("List Order Security Tests")
    class ListOrderTests {
        @WithUserDetails("spring")
        @Test
        public void testListOrdersAdmin() throws Exception {
            mvc.perform(get("/api/v1/customers/{customerId}/orders", oddbins.getId()))
               .andExpect(status().isOk());
        }

        @WithUserDetails("oddbinsUser")
        @Test
        public void testListOrdersCustomer() throws Exception {
            mvc.perform(get("/api/v1/customers/{customerId}/orders", oddbins.getId()))
               .andExpect(status().isOk());
        }

        @WithUserDetails("oddbinsUser")
        @Test
        public void testListOrdersWrongCustomer() throws Exception {
            mvc.perform(get("/api/v1/customers/{customerId}/orders", wineWarehouse.getId()))
               .andExpect(status().isForbidden());
        }

        @Test
        public void testListOrdersUnauthorized() throws Exception {
            mvc.perform(get("/api/v1/customers/{customerId}/orders", wineWarehouse.getId()))
               .andExpect(status().isUnauthorized());
        }
    }

}
