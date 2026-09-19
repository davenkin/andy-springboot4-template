package com.company.andy.common.security;

import com.company.andy.common.configuration.property.CommonProperties;
import com.company.andy.common.security.org.AnonymousOrgActorAuthenticationTokenFilter;
import com.company.andy.common.security.org.JwtToOrgActorAuthenticationTokenFilter;
import com.company.andy.common.security.platform.AnonymousPlatformActorAuthenticationTokenFilter;
import com.company.andy.common.security.platform.JwtToPlatformActorAuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;
    private final JsonAccessDeniedHandler jsonAccessDeniedHandler;
    private final CommonProperties commonProperties;

    @Bean
    public SecurityFilterChain orgApiFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) {
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/about",
                                "/favicon.ico",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(it -> it.sessionCreationPolicy(STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(withDefaults())
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler))
                .addFilterAfter(new JwtToOrgActorAuthenticationTokenFilter(jsonAuthenticationEntryPoint, commonProperties), BearerTokenAuthenticationFilter.class)
                .anonymous((it) -> it.authenticationFilter(new AnonymousOrgActorAuthenticationTokenFilter()))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
        ;
        return http.build();
    }

    @Bean
    @Order(-1)
    public SecurityFilterChain platformApiFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) {
        http.securityMatcher("/platform/**", "/actuator/**")
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(POST, "/platform/demo-reservations").permitAll()
                        .requestMatchers(GET, "/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(it -> it.sessionCreationPolicy(STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(withDefaults())
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler))
                .addFilterAfter(new JwtToPlatformActorAuthenticationTokenFilter(jsonAuthenticationEntryPoint, commonProperties), BearerTokenAuthenticationFilter.class)
                .anonymous((it) -> it.authenticationFilter(new AnonymousPlatformActorAuthenticationTokenFilter()))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
        ;
        return http.build();
    }
}
