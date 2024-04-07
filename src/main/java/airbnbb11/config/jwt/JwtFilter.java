package airbnbb11.config.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.ServletException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;


@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, java.io.IOException {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            if (StringUtils.hasText(token)) {
                try {
                    String email = jwtService.extractUserEmail(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                   if(jwtService.isTokenValid(token, userDetails)) {
                       UsernamePasswordAuthenticationToken authenticationToken =
                               new UsernamePasswordAuthenticationToken(
                                       userDetails,
                                       null,
                                       userDetails.getAuthorities());

                       SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                   }

                } catch (JWTVerificationException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
