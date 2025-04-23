package com.maravondra.apigateway;

import java.util.Set;
import lombok.extern.java.Log;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Log
public class MyPreFilter implements GlobalFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    log.info("MyPreFilter: Pre-processing request");

    String requestPath = exchange.getRequest().getPath().toString();
    log.info("Request Path: " + requestPath);

    HttpHeaders headers = exchange.getRequest().getHeaders();
    Set<String> headersName = headers.keySet();
    headersName.forEach((headersOneName) -> {
      String headerValue = headers.getFirst(headersOneName);
      log.info("Header: " + headersOneName + " Value: " + headerValue);
    });
    return chain.filter(exchange);
  }
}
