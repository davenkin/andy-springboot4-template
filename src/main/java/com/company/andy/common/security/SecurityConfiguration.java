package com.company.andy.common.security;

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

import static com.company.andy.common.utils.Constants.SYSTEM_ADMIN_ROLE;
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

    @Bean
    public SecurityFilterChain featureFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) {
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
                .addFilterAfter(new JwtToOrgActorAuthenticationTokenFilter(jsonAuthenticationEntryPoint), BearerTokenAuthenticationFilter.class)
                .anonymous((it) -> {
                    it.authenticationFilter(new AnonymousActorAuthenticationTokenFilter());
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
        ;
        return http.build();
    }

    @Bean
    @Order(-1)
    public SecurityFilterChain systemFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) {
        http.securityMatcher("/system/**", "/actuator/**")
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(POST, "/system/demo-reservations").permitAll()
                        .requestMatchers(GET, "/actuator/**").permitAll()
                        .anyRequest().hasRole(SYSTEM_ADMIN_ROLE)
                )
                .sessionManagement(it -> it.sessionCreationPolicy(STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(withDefaults())
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler))
                .addFilterAfter(new JwtToSystemActorAuthenticationTokenFilter(jsonAuthenticationEntryPoint), BearerTokenAuthenticationFilter.class)
                .anonymous((it) -> {
                    it.authenticationFilter(new AnonymousActorAuthenticationTokenFilter());
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
        ;
        return http.build();
    }
}
