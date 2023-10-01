package com.example.demo.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

@Configuration
@Log4j2
public class WebClientConfiguration {

    @Value("${service.hub.endpoint.base-url}")
    private String baseUrl;

    @Value("${service.oauth2.client-id}")
    private String clientId;

    public static final String CLIENT_ID_HEADER_NAME = "clientId";

    @Bean
    public WebClient webClient(@Autowired WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(CLIENT_ID_HEADER_NAME, clientId)
                .filter(logRequest())
                .filter(logResponseFunction)
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("({}) {}", request.method().name(), request.url());
            log.debug("headers={}", request.headers().toString());
            return Mono.just(request);
        });
    }

    private final ExchangeFilterFunction logResponseFunction = (request, next) ->
        next.exchange(request).map(response -> {
            log.info("response {}", response.statusCode());
            log.debug("headers={}", response.headers().asHttpHeaders());

            Flux<DataBuffer> body = response.body(BodyExtractors.toDataBuffers())
                .doOnNext(dataBuffer -> {
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());

                        log.info("{}", baos.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), " "));
                    } catch (IOException e) {
                        log.error("ERROR Response", e);
                    }
                });
            return ClientResponse.from(response).body(body).build();
        });
}
