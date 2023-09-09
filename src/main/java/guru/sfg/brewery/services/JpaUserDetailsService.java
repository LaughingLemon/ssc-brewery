package guru.sfg.brewery.services;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("find username " + username);
        User user = userRepository.findByUsername(username)
                                  .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                                                                      user.getPassword(),
                                                                      user.isEnabled(),
                                                                      user.isAccountNonExpired(),
                                                                      user.isCredentialsNonExpired(),
                                                                      user.isAccountNonLocked(),
                                                                      convertAuthorities(user.getAuthorities()));
    }

    private Set<? extends GrantedAuthority> convertAuthorities(Set<Authority> authorities) {
        if (Objects.nonNull(authorities) && !authorities.isEmpty()) {
            return authorities.stream()
                              .map(Authority::getRole)
                              .map(SimpleGrantedAuthority::new)
                              .collect(Collectors.toSet());
        }
        return Set.of();
    }
}
