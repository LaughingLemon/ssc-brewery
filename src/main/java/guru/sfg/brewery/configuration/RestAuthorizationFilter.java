package guru.sfg.brewery.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class RestAuthorizationFilter extends GenericFilterBean {

    private final AuthenticationManager authenticationManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            String userName = httpRequest.getHeader("Api-key");
            String password = httpRequest.getHeader("Api-secret");
            log.info("request headers: Api-key, " + userName
                               + ", Api-secret, " + password);

            if (!StringUtils.hasLength(userName) && !StringUtils.hasLength(password)) {
                chain.doFilter(request, response);
                return;
            }

            if (!StringUtils.hasLength(userName) || !StringUtils.hasLength(password)) {
                ((HttpServletResponse) response).setStatus(401);
                return;
            }

            UsernamePasswordAuthenticationToken authReq
                    = new UsernamePasswordAuthenticationToken(userName, password);
            Authentication authenticated = authenticationManager.authenticate(authReq);
            SecurityContextHolder.getContext()
                                 .setAuthentication(authenticated);
        }
        chain.doFilter(request, response);
    }

}
