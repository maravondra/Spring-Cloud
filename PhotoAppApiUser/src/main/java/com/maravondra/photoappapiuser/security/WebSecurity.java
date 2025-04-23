package com.maravondra.photoappapiuser.security;

import com.maravondra.photoappapiuser.service.UserService;
import com.maravondra.photoappapiuser.service.UserServiceImpl;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {

  private final Environment environment;

  private final UserService userService;
  private final BCryptPasswordEncoder bcryptPasswordEncoder;

  @Bean
  protected SecurityFilterChain configure(HttpSecurity http) throws Exception {


//Configuration Builder
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bcryptPasswordEncoder);

    AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

    // create auth url
    AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, environment, authenticationManager);
    authenticationFilter.setFilterProcessesUrl(
        environment.getProperty("login.url.path", String.class)
    );

    http.csrf((crsf) -> crsf.disable());

    http
        .authorizeHttpRequests(
        (authorize) -> authorize
//            .requestMatchers(new AntPathRequestMatcher("/users")).access(
//                new WebExpressionAuthorizationManager(
//                  "hasIpAddress('"+environment.getProperty("gateway.ip")+"')"
//                )
//            )
            .requestMatchers(new AntPathRequestMatcher("/users/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
        )
        .addFilter(authenticationFilter)
        .authenticationManager(authenticationManager)

        .sessionManagement(
        (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    http.headers((headers) ->
        headers.frameOptions((frameOptionsConfig ->
          frameOptionsConfig.sameOrigin()
        ))
    );

    return http.build();

  }

}
