package ru.snowadv.comaprbackend.security

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import ru.snowadv.comaprbackend.security.jwt.AuthEntryPointJwt
import ru.snowadv.comaprbackend.security.jwt.AuthTokenFilter
import ru.snowadv.comaprbackend.security.service.UserService


@Configuration
@EnableMethodSecurity
class WebSecurityConfig(
    val userDetailsService: UserService,
    val unauthorizedHandler: AuthEntryPointJwt,
    val context: ApplicationContext
) {
    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()

        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())

        return authProvider
    }
    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }
            .exceptionHandling { exception: ExceptionHandlingConfigurer<HttpSecurity?> ->
                exception.authenticationEntryPoint(
                    unauthorizedHandler
                )
            }
            .sessionManagement { session: SessionManagementConfigurer<HttpSecurity?> ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/error").permitAll()
                auth.requestMatchers("/api/v1/auth/**").permitAll()
                auth.requestMatchers("/api/v1/roadmap/**").permitAll()
                auth.requestMatchers("/api/v1/category/**").permitAll()
                    .anyRequest().authenticated()
            }

        http.authenticationProvider(authenticationProvider())

        http.addFilterBefore(context.getBean(AuthTokenFilter::class.java), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
