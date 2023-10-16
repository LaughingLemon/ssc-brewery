package guru.sfg.brewery.configuration;

import guru.sfg.brewery.domain.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class BeerOrderAuthenticationManager {

    public boolean doesCustomerIdMatch(Authentication authentication, UUID customerId) {
        User authenticatedUser = (User) authentication.getPrincipal();
        log.info("Auth user cust id {}, vs cust id {}", authenticatedUser.getCustomer().getId(), customerId);
        return authenticatedUser.getCustomer().getId().equals(customerId);
    }

}
