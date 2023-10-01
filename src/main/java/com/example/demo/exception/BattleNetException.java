package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BattleNetException extends Exception{

    private static List<ErrorDetailEntity> toDetails(Object errors) {
        if (!(errors instanceof Collection))
            return null;

        List<Map<String, String>> errorsList = (List<Map<String, String>>) errors;

        return errorsList.stream()
                .map(value -> new ErrorDetailEntity(value.get("message"), value.get("location"), value.get("code")))
                .collect(Collectors.toList());
    }

    public static final Function<ClientResponse, Mono<? extends Throwable>> exceptionFunction = clientResponse ->
        clientResponse.bodyToMono(Map.class)
            .onErrorResume(throwable -> {
                Map<String, Object> map = new HashMap<>();
                map.put("status", clientResponse.rawStatusCode());
                map.put("message", "");
                return Mono.just(map);
            })
            .flatMap(exc -> Mono.error(new BattleNetException()));
}
