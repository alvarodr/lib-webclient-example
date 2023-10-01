package com.example.demo.webclient;

import com.example.demo.dto.League;
import com.example.demo.exception.BattleNetException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class StarCarftIIWebClient {

    private final WebClient webClient;

    private static final String LEAGUE_PATH_URL = "/data/sc2/league/{sesionId}/{teamType}/{leagueId}";

    public Flux<League> getLeagues(String sessionId, String teamType, String leagueId) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(LEAGUE_PATH_URL).build(sessionId, teamType, leagueId))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .onStatus(HttpStatus::isError, BattleNetException.exceptionFunction)
            .bodyToFlux(League.class);
    }

}
