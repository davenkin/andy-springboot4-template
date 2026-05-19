package com.company.andy.common.security;

import static com.company.andy.common.model.actor.ActorSource.HUMAN_USER;
import static com.company.andy.common.model.actor.SystemActor.createUserSystemActor;
import static com.company.andy.common.util.Constants.JWT_PREFERRED_USERNAME;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;

import com.company.andy.common.model.actor.ActorSource;
import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.common.tracing.ActorMdcSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

// Convert the default Jwt principal into SystemActor
// Controllers can use "@AuthenticationPrincipal SystemActor actor" to obtain the current actor

// todo: use authentication entry point for failure

@Slf4j
public class JwtToSystemActorAuthenticationTokenFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    boolean mdcPopulated = false;
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null) {
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
          Jwt jwt = jwtAuthenticationToken.getToken();
          if (jwt != null) {
            SystemActorAuthenticationToken authenticationToken = createActorAuthenticationToken(request, jwt);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            ActorMdcSupport.addMdc(authenticationToken.getActor());
            mdcPopulated = true;
          }
        }
      }
      filterChain.doFilter(request, response);
    } finally {
      if (mdcPopulated) {
        ActorMdcSupport.clearMdc();
      }
    }
  }

  private SystemActorAuthenticationToken createActorAuthenticationToken(HttpServletRequest request, Jwt jwt) {
    SystemActor actor = createUserSystemActor(
        jwt.getSubject(),
        getActorName(jwt),
        getActorSource(jwt),
        "%s[%s]".formatted(request.getMethod(), request.getRequestURI())
    );
    return new SystemActorAuthenticationToken(actor, jwt);
  }

  private String getActorName(Jwt jwt) {
    String name = jwt.getClaimAsString(JWT_PREFERRED_USERNAME);
    return isNotBlank(name) ? name : jwt.getSubject();
  }

  private ActorSource getActorSource(Jwt jwt) {
    // todo: decide the actual ActorSource(HUMAN_USER or SERVICE_ACCOUNT) based on Jwt content
    return HUMAN_USER;
  }
}
