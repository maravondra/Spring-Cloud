package com.maravondra.photoappapiuser.security;


import static java.time.Instant.now;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maravondra.photoappapiuser.model.LoginRequestModel;
import com.maravondra.photoappapiuser.service.UserService;
import com.maravondra.photoappapiuser.shared.UserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity.JwtSpec;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private UserService userService;
  private Environment environment;

  public AuthenticationFilter(
      UserService userService, Environment environment, AuthenticationManager authenticationManager) {
    super(authenticationManager);
    this.userService = userService;
    this.environment = environment;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
      throws AuthenticationException {
    try {

      LoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), LoginRequestModel.class);

      return getAuthenticationManager().authenticate(
          new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {

    String username = ((User) authResult.getPrincipal()).getUsername();
    UserDTO userDetailsByEmail = userService.getUserDetailsByEmail(username);

    String tokenSecret = environment.getProperty("token.secret");
    byte[] secretBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
    SecretKey secretKey = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());

    String token = Jwts.builder()
        .subject(userDetailsByEmail.getUserId())
        .expiration(Date.from(now()
            .plusMillis(environment.getProperty("token.expiration_time", Integer.class)))
            )
        .issuedAt(Date.from(now()))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();

    response.addHeader("token", token);
    response.addHeader("userId", userDetailsByEmail.getUserId());


  }
}
