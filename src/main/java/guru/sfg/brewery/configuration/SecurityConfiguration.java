package guru.sfg.brewery.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authenticationManager) throws Exception {
        log.info("filterChain start");

        http.addFilterBefore(new RestAuthorizationFilter(authenticationManager),
                             UsernamePasswordAuthenticationFilter.class)
            .csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/", "/webjars/**", "/login", "/resources/**")
                                                         .permitAll()
                                                         .requestMatchers("/beers*")
                                                         .permitAll()
                                                         .requestMatchers("/beers/find")
                                                         .hasAnyRole("ADMIN", "CUSTOMER", "USER")
                                                         .requestMatchers(HttpMethod.GET, "/api/v1/beer/**")
                                                         .permitAll()
                                                         .requestMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}")
                                                         .permitAll()
                                                         .requestMatchers("/brewery/breweries/**")
                                                         .hasAnyRole("ADMIN", "CUSTOMER")
                                                         .anyRequest()
                                                         .authenticated())
            .formLogin(withDefaults())
            .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
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