package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DefaultSecurityLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private Authority adminRole;
    private Authority userRole;
    private Authority customerRole;

    @Override
    public void run(String... args) throws Exception {
        loadAuthorityData();
        loadUserData();
    }

    private void loadUserData() {
        User springUser = User.builder()
                              .username("spring")
                              .password("{bcrypt15}$2a$15$PQNH5woatbMfrWrgvKvW6ekkSvkA08IB2CBKUjMqx.e/Hv4lTO/ZS")
                              .authority(userRole)
                              .build();
        if (userRepository.findBy(Example.of(springUser), FluentQuery.FetchableFluentQuery::first)
                          .isEmpty()) {
            userRepository.save(springUser);
        }
        User userUser = User.builder()
                            .username("user")
                            .password("{bcrypt}$2a$10$7aLHzJ.ywdrlihAwgopoa.4utl4iFHGSAJ13AMspyASltxlZbAQu.")
                            .authority(userRole)
                            .build();
        if (userRepository.findBy(Example.of(userUser), FluentQuery.FetchableFluentQuery::first)
                          .isEmpty()) {
            userRepository.save(userUser);
        }
        User scottUser = User.builder()
                             .username("scott")
                             .password("{noop}password")
                             .authority(customerRole)
                             .build();
        if (userRepository.findBy(Example.of(scottUser), FluentQuery.FetchableFluentQuery::first)
                          .isEmpty()) {
            userRepository.save(scottUser);
        }
    }

    private void loadAuthorityData() {
        adminRole = Authority.builder()
                             .role("ADMIN")
                             .build();
        if (authorityRepository.findBy(Example.of(adminRole),
                                       FluentQuery.FetchableFluentQuery::first)
                               .isEmpty()) {
            adminRole = authorityRepository.save(adminRole);
        }

        userRole = Authority.builder()
                            .role("USER")
                            .build();
        if (authorityRepository.findBy(Example.of(userRole),
                                       FluentQuery.FetchableFluentQuery::first)
                               .isEmpty()) {
            userRole = authorityRepository.save(userRole);
        }
        customerRole = Authority.builder()
                                .role("CUSTOMER")
                                .build();
        if (authorityRepository.findBy(Example.of(customerRole),
                                       FluentQuery.FetchableFluentQuery::first)
                               .isEmpty()) {
            customerRole = authorityRepository.save(customerRole);
        }
    }
}
