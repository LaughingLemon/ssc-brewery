package guru.sfg.brewery.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

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
        UserDetails admin = User.withUsername("admin")
                                .password("{noop}password")
                                .roles("ADMIN")
                                .build();
        UserDetails user = User.withUsername("user")
                               .password("{bcrypt}$2a$10$7aLHzJ.ywdrlihAwgopoa.4utl4iFHGSAJ13AMspyASltxlZbAQu.")
                               .roles("USER")
                               .build();
        UserDetails springUser = User.withUsername("spring")
                               .password("{bcrypt15}$2a$15$PQNH5woatbMfrWrgvKvW6ekkSvkA08IB2CBKUjMqx.e/Hv4lTO/ZS")
                               .roles("USER")
                               .build();
        return new InMemoryUserDetailsManager(user, admin, springUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        String encodingId = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(encodingId, new BCryptPasswordEncoder());
        encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        encoders.put("bcrypt15", new BCryptPasswordEncoder(15));
        return new DelegatingPasswordEncoder(encodingId, encoders);
    }
}