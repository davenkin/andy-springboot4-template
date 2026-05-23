package com.company.andy.common.configuration;

import com.company.andy.common.configuration.profile.DisableForIT;
import com.company.andy.common.security.JwtRelayInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

import static com.company.andy.common.utils.Constants.KEYCLOAK_SERVICE_ACCOUNT;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// Disable for integration tests,
// as we don't want to make real api calls to external systems in integration tests

@DisableForIT
@Configuration(proxyBeanMethods = false)
public class RestClientConfiguration {

   // RestClient that relays current actor's JWT token to call external APIs
    @Bean("jwtRelayRestClient")
    public RestClient jwtRelayRestClient(RestClient.Builder builder, JwtRelayInterceptor jwtRelayInterceptor) {
        return builder.defaultHeader(ACCEPT, APPLICATION_JSON_VALUE).requestInterceptor(jwtRelayInterceptor).build();
    }

    // RestClient that represent the application itself to call external APIs
    @Bean("serviceAccountRestClient")
    public RestClient serviceAccountRestClient(RestClient.Builder builder, OAuth2AuthorizedClientManager authorizedClientManager) {
        OAuth2ClientHttpRequestInterceptor interceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        interceptor.setClientRegistrationIdResolver(_ -> KEYCLOAK_SERVICE_ACCOUNT);
        return builder.defaultHeader(ACCEPT, APPLICATION_JSON_VALUE).requestInterceptor(interceptor).build();
    }

    // This is required by "serviceAccountRestClient" bean to make Oauth2 client credentials flow work
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();
        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }
}
