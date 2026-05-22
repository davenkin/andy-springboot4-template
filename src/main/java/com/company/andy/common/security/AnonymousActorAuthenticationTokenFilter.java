package com.company.andy.common.security;

import static com.company.andy.common.model.actor.AnonymousActor.createAnonymousActor;
import static com.company.andy.common.security.SecurityUtils.createActorInitiatorFrom;
import static com.company.andy.common.utils.Constants.ANONYMOUS_ROLE;
import static com.company.andy.common.utils.Constants.ROLE_PREFIX;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

import java.util.UUID;

import com.company.andy.common.model.actor.AnonymousActor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

public class AnonymousActorAuthenticationTokenFilter extends AnonymousAuthenticationFilter {
  private final String theKey;

  public AnonymousActorAuthenticationTokenFilter() {
    String key = UUID.randomUUID().toString();
    super(key);
    this.theKey = key;
  }

  @Override
  protected Authentication createAuthentication(HttpServletRequest request) {
    AnonymousActor anonymousActor = createAnonymousActor(createActorInitiatorFrom(request));
    return new AnonymousAuthenticationToken(this.theKey, anonymousActor, createAuthorityList(ROLE_PREFIX + ANONYMOUS_ROLE));
  }
}
