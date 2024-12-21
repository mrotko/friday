package pl.rotkom.friday.thirdparty.spotify.impl;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;


import pl.rotkom.friday.thirdparty.spotify.SpotifyConfig;
import pl.rotkom.friday.thirdparty.spotify.api.dto.EpisodeDTO;
import pl.rotkom.friday.thirdparty.spotify.api.dto.ShowDTO;
import pl.rotkom.friday.thirdparty.spotify.api.service.ISpotifyApi;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.michaelthelin.spotify.SpotifyApi;

@Slf4j
@Component
class SpotifyApiImpl implements ISpotifyApi {

    private final SpotifyApi spotifyApi;

    private final ScheduledExecutorService executorService;

    public SpotifyApiImpl(SpotifyConfig spotifyConfig) {
        this.spotifyApi = SpotifyApi.builder()
                .setClientId(spotifyConfig.getClientId())
                .setClientSecret(spotifyConfig.getClientSecret())
                .setRedirectUri(URI.create(spotifyConfig.getRedirectUri()))
                .setRefreshToken(spotifyConfig.getRefreshToken())
                .build();
        refreshToken();

        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this::refreshToken, 50, 50, TimeUnit.MINUTES);
    }

    // clean up the executor service
    @PreDestroy
    public void close() {
        executorService.shutdown();
    }

    private void refreshToken() {
        try {
            var accessToken = spotifyApi.authorizationCodeRefresh().build().execute().getAccessToken();
            spotifyApi.setAccessToken(accessToken);
        } catch (Exception e) {
            log.error("Error while refreshing token", e);
        }
    }

    @Override
    public List<ShowDTO> getShows() {
        int page = 0;
        int limit = 50;
        List<ShowDTO> shows = new ArrayList<>();
        try {
            while (true) {
                var paging = spotifyApi.getUsersSavedShows().limit(limit).offset(page * limit).build().execute();
                var items = paging.getItems();
                if (items.length == 0) {
                    break;
                }

                for(var show : items) {
                    ShowDTO dto = new ShowDTO();
                    dto.setId(show.getShow().getId());
                    dto.setName(show.getShow().getName());
                    dto.setUri(show.getShow().getUri());
                    shows.add(dto);
                }

                page++;
            }
        } catch (Exception e) {
            log.error("Error while fetching shows", e);
            return Collections.emptyList();
        }
        return shows;
    }


    @Override
    public List<EpisodeDTO> getEpisodesByShowIds(Collection<String> showIds) {
        return Flux.fromIterable(showIds)
                .flatMap(showId -> Mono.fromFuture(getEpisodesByShowId(showId)), 1)
                .flatMapIterable(Function.identity())
                .collectList()
                .blockOptional()
                .orElse(Collections.emptyList());
    }

    private CompletableFuture<List<EpisodeDTO>> getEpisodesByShowId(String showId) {
        try {
            return spotifyApi.getShowEpisodes(showId)
                    .limit(3)
                    .build().executeAsync()
                    .thenApply(paging -> Arrays.stream(paging.getItems())
                            .filter(Objects::nonNull)
                            .map(episode -> {
                                EpisodeDTO dto = new EpisodeDTO();
                                dto.setId(episode.getId());
                                dto.setName(episode.getName());
                                dto.setReleaseAt(LocalDate.parse(episode.getReleaseDate()));
                                dto.setDuration(Duration.ofMillis(episode.getDurationMs()));
                                dto.setUri(episode.getUri());
                                dto.setShowId(showId);
                                return dto;
                            })
                            .toList()
                    );
        } catch (Exception e) {
            log.error("Error while fetching episodes", e);
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
    }
}
