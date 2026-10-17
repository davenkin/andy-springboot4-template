package com.company.andy.common.security;

import com.company.andy.common.configuration.profile.DisableForIT;
import com.company.andy.common.configuration.property.CommonProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import static com.company.andy.common.model.actor.ActorOriginChannel.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver.fromTrustedIssuers;

@NullMarked
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;
    private final JsonAccessDeniedHandler jsonAccessDeniedHandler;
    private final CommonProperties commonProperties;

    @Bean
    @DisableForIT
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
        return fromTrustedIssuers(
                commonProperties.orgJwtIssuer(),
                commonProperties.platformJwtIssuer(),
                commonProperties.deviceJwtIssuer());
    }

    @Bean
    public SecurityFilterChain orgApiFilterChain(HttpSecurity http,
                                                 CorsConfigurationSource corsConfigurationSource,
                                                 AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver) {
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/about",
                                "/favicon.ico",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()
                        .requestMatchers(POST, "/demo-reservations").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(it -> it.sessionCreationPolicy(STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer((oauth2) -> oauth2.authenticationManagerResolver(authenticationManagerResolver)
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler))
                .addFilterAfter(new JwtToOrgActorAuthenticationTokenFilter(jsonAuthenticationEntryPoint, commonProperties), BearerTokenAuthenticationFilter.class)
                .anonymous((it) -> it.authenticationFilter(new AnonymousActorAuthenticationTokenFilter(ORG_API)))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
        ;
        return http.build();
    }

    @Bean
    @Order(-1)
    public SecurityFilterChain platformApiFilterChain(HttpSecurity http,
                                                      CorsConfigurationSource corsConfigurationSource,
                                                      AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver) {
        http.securityMatcher("/platform/**", "/actuator/**")
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(GET, "/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(it -> it.sessionCreationPolicy(STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer((oauth2) -> oauth2.authenticationManagerResolver(authenticationManagerResolver)
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler))
                .addFilterAfter(new JwtToPlatformActorAuthenticationTokenFilter(jsonAuthenticationEntryPoint, commonProperties), BearerTokenAuthenticationFilter.class)
                .anonymous((it) -> it.authenticationFilter(new AnonymousActorAuthenticationTokenFilter(PLATFORM_API)))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
        ;
        return http.build();
    }

    @Bean
    @Order(-2)
    public SecurityFilterChain deviceApiFilterChain(HttpSecurity http,
                                                    CorsConfigurationSource corsConfigurationSource,
                                                    AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver) {
        http.securityMatcher("/device/**")
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )
                .sessionManagement(it -> it.sessionCreationPolicy(STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer((oauth2) -> oauth2.authenticationManagerResolver(authenticationManagerResolver)
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler))
                .addFilterAfter(new JwtToDeviceActorAuthenticationTokenFilter(jsonAuthenticationEntryPoint, commonProperties), BearerTokenAuthenticationFilter.class)
                .anonymous((it) -> it.authenticationFilter(new AnonymousActorAuthenticationTokenFilter(DEVICE_API)))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
        ;
        return http.build();
    }
}
