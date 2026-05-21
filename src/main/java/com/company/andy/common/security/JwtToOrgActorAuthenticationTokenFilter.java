package com.company.andy.common.security;

import static java.util.stream.Collectors.toSet;

import static com.company.andy.common.model.OrgRole.ORG_ADMIN;
import static com.company.andy.common.model.actor.ActorSource.HUMAN_USER;
import static com.company.andy.common.security.SecurityUtils.createActorInitiatorFrom;
import static com.company.andy.common.util.Constants.JWT_CLAIM_ORG_ID;
import static com.company.andy.common.util.Constants.JWT_CLAIM_PREFERRED_USERNAME;
import static com.company.andy.common.util.Constants.ROLE_PREFIX;
import static com.company.andy.common.util.Constants.SYSTEM_ADMIN_ROLE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.company.andy.common.model.OrgRole;
import com.company.andy.common.model.actor.ActorSource;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.common.tracing.ActorMdcSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

// Convert the default Jwt principal into OrgActor
// Controllers can use "@AuthenticationPrincipal OrgActor actor" to obtain the current actor

@Slf4j
public class JwtToOrgActorAuthenticationTokenFilter extends OncePerRequestFilter {
  private final static Set<String> ALL_ORG_ROLES = Arrays.stream(OrgRole.values())
      .map(OrgRole::name)
      .collect(toSet());

  private final AuthenticationEntryPoint authenticationEntryPoint;

  public JwtToOrgActorAuthenticationTokenFilter(AuthenticationEntryPoint authenticationEntryPoint) {
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

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
            ActorAuthenticationToken authenticationToken = createActorAuthenticationToken(jwt, request);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            ActorMdcSupport.addMdc(authenticationToken.getActor());
            mdcPopulated = true;
          }
        }
      }
      filterChain.doFilter(request, response);
    } catch (AuthenticationException ex) {
      log.error("Authentication failed:", ex);
      SecurityContextHolder.clearContext();
      authenticationEntryPoint.commence(request, response, ex);
    } finally {
      if (mdcPopulated) {
        ActorMdcSupport.clearMdc();
      }
    }
  }

  private ActorAuthenticationToken createActorAuthenticationToken(Jwt jwt, HttpServletRequest request) {
    //todo: for system admin, the orgId should come from header first then jwt
    Set<OrgRole> roles = getRoles(jwt);
    List<SimpleGrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.name())).toList();
    return new ActorAuthenticationToken(
        new OrgActor(
            jwt.getSubject(),
            getActorName(jwt),
            getOrgId(jwt),
            roles,
            getActorSource(jwt),
            createActorInitiatorFrom(request)
        ),
        authorities,
        jwt
    );
  }

  private String getActorName(Jwt jwt) {
    String name = jwt.getClaimAsString(JWT_CLAIM_PREFERRED_USERNAME);
    return isNotBlank(name) ? name : jwt.getSubject();
  }

  private String getOrgId(Jwt jwt) {
    String orgId = jwt.getClaimAsString(JWT_CLAIM_ORG_ID);
    // advice: for super admin, you may use orgId from the http header
    if (isBlank(orgId)) {
      throw new InvalidBearerTokenException("Cannot obtain an orgId from Jwt.");
    }
    return orgId;
  }

  private Set<OrgRole> getRoles(Jwt jwt) {
    // system_admin automatically implies ORG_ADMIN
    Set<String> roles = SecurityUtils.getJwtRoles(jwt).stream()
        .map(String::toUpperCase)
        .collect(toSet());
    if (roles.contains(SYSTEM_ADMIN_ROLE)) {
      return Set.of(ORG_ADMIN);
    }

    return roles.stream()
        .filter(ALL_ORG_ROLES::contains)
        .map(OrgRole::valueOf)
        .collect(toSet());
  }

  private ActorSource getActorSource(Jwt jwt) {
    // advice: decide the actual ActorSource(HUMAN_USER or SERVICE_ACCOUNT) based on Jwt content
    return HUMAN_USER;
  }
}
