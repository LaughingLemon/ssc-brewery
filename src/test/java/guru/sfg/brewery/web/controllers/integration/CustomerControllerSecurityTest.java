package guru.sfg.brewery.web.controllers.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
public class CustomerControllerSecurityTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("List customers")
    class ListCustomersTests {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.integration.BaseIntegrationTest#getStreamAdminAndCustomerUsers")
        public void testCustomerSecurity(String user, String password) throws Exception {
            mvc.perform(get("/customers").with(httpBasic(user, password)))
               .andExpect(status().isOk())
               .andExpect(view().name("customers/findCustomers"));
        }

        @Test
        public void testCustomerSecurityForbidden() throws Exception {
            mvc.perform(get("/customers").with(httpBasic("user", "password")))
               .andExpect(status().isForbidden());
        }

        @Test
        public void testCustomerSecurityNoAuth() throws Exception {
            mvc.perform(get("/customers"))
               .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Add customer")
    class AddCustomers {
        @Rollback
        @Test
        public void testCustomerSecurity() throws Exception {
            mvc.perform(post("/customers/new")
                                .param("customerName", "A Customer")
                                .with(httpBasic("spring", "password")))
               .andExpect(status().is3xxRedirection());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.integration.BaseIntegrationTest#getStreamUserAndCustomerUsers")
        public void testCustomerSecurityForbidden(String user, String password) throws Exception {
            mvc.perform(post("/customers/new").with(httpBasic(user, password)))
               .andExpect(status().isForbidden());
        }

        @Test
        public void testCustomerSecurityNoAuth() throws Exception {
            mvc.perform(post("/customers/new"))
               .andExpect(status().isUnauthorized());
        }
    }

}
