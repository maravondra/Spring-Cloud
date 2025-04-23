package com.maravondra.apigateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory {

  @Autowired
  Environment environment;

  public AuthorizationHeaderFilter() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Object config) {

    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
        return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
      }
      String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
      String jwt = authorizationHeader.replace("Bearer ", "");

      if (!isJwtValid(jwt)) {
        return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
      }

      return chain.filter(exchange);
    };
  }

  private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(status);

    return response.setComplete();
  }

  private boolean isJwtValid(String jwt) {
    boolean valid = true;
    String subject = null;

    String tokenSecret = environment.getProperty("token.secret");
    byte[] encode = Base64.getEncoder().encode(tokenSecret.getBytes());
    SecretKey signingKey = new SecretKeySpec(encode, SignatureAlgorithm.HS512.getJcaName());
    JwtParser parser = null;
    try {
      parser
          = Jwts.parser()
          .verifyWith(signingKey)
          .build();
    } catch (Exception e) {
      return false;
    }

    try {
      Jwt<Header, Claims> parsedToken = (Jwt<Header, Claims>) parser.parse(jwt);
      subject = parsedToken.getPayload().getSubject();
    } catch (Exception e) {
      valid = false;
    }

    if (subject == null || subject.isEmpty()) {
      valid = false;
    }

    return valid;
  }

  public static class Config {
    // Put the configuration properties
  }


}
