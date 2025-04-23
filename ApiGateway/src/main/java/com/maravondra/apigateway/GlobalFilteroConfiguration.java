package com.maravondra.apigateway;

import lombok.extern.java.Log;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
@Log
public class GlobalFilteroConfiguration {

  @Bean
  public GlobalFilter secondPreFilter() {
    return ((exchange, chain) -> {
      log.info("My second pre filter");
      return chain.filter(exchange).then(Mono.fromRunnable(() -> {);
        log.info("My second post filter");
      }));
    });
  }

  @Bean
  public GlobalFilter thirdPreFilter() {
    return ((exchange, chain) -> {
      log.info("My third pre filter");
      return chain.filter(exchange).then(Mono.fromRunnable(() -> {);
        log.info("My third post filter");
      }));
    });
  }
}
