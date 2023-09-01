package guru.sfg.brewery.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz.requestMatchers(antMatcher("/"),
                                                                  antMatcher("/login"),
                                                                  antMatcher("/webjars/**"),
                                                                  antMatcher("/resources/**"),
                                                                  antMatcher("/beers/find"),
                                                                  antMatcher("/beers*"),
                                                                  antMatcher(HttpMethod.GET, "/api/v1/beer/**"))
                                                 .permitAll()
                                                 .requestMatchers("/api/v1/beerUpc/{upc}")
                                                 .permitAll()
                                                 .anyRequest()
                                                 .authenticated())
            .formLogin(withDefaults())
            .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetails() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                                .username("admin")
                                .password("Ch4ng3Me!")
                                .roles("ADMIN")
                                .build();
        UserDetails user = User.withDefaultPasswordEncoder()
                               .username("user")
                               .password("password")
                               .roles("USER")
                               .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}
