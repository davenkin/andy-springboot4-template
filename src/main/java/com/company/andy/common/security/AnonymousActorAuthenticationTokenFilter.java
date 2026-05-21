package com.company.andy.common.security;

import static com.company.andy.common.model.actor.AnonymousActor.createAnonymousActor;
import static com.company.andy.common.security.SecurityUtils.createActorInitiatorFrom;
import static com.company.andy.common.util.Constants.ANONYMOUS_ROLE;
import static com.company.andy.common.util.Constants.ROLE_PREFIX;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

import java.util.UUID;

import com.company.andy.common.model.actor.AnonymousActor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

public class AnonymousActorAuthenticationTokenFilter extends AnonymousAuthenticationFilter {

  public AnonymousActorAuthenticationTokenFilter() {
    super(UUID.randomUUID().toString());
  }

  @Override
  protected Authentication createAuthentication(HttpServletRequest request) {
    AnonymousActor anonymousActor = createAnonymousActor(createActorInitiatorFrom(request));
    return new ActorAuthenticationToken(anonymousActor, createAuthorityList(ROLE_PREFIX + ANONYMOUS_ROLE), null);
  }
}
