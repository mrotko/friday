package pl.rotkom.friday.thirdparty.spotify.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pl.rotkom.friday.thirdparty.google.GoogleConfig;
import pl.rotkom.friday.thirdparty.spotify.SpotifyConfig;
import pl.rotkom.friday.thirdparty.spotify.api.service.ISpotifyApi;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = SpotifyConfig.class)
@TestPropertySource("classpath:application-test.properties")
class SpotifyApiImplTest {

    private ISpotifyApi spotifyApi;

    @Autowired
    private SpotifyConfig config;

    @BeforeEach
    void setUp() {
        spotifyApi = new SpotifyApiImpl(config);
    }

    @Test
    void shouldReadUserEpisodes() {
        var shows = spotifyApi.getShows();
        var show = shows.stream()
                .filter(s -> s.getName().equals("Maratończycy Puenty"))
                .findFirst()
                .orElseThrow();

        assertThat(show.getId()).isEqualTo("04x4dUJXXuw36V7oUqmijp");
        assertThat(show.getName()).isEqualTo("Maratończycy Puenty");
        assertThat(show.getUri()).isEqualTo("spotify:show:04x4dUJXXuw36V7oUqmijp");

        var episodes = spotifyApi.getEpisodesByShowIds(List.of(shows.getFirst().getId()));
        assertThat(episodes).isNotEmpty();
        assertThat(episodes.getFirst().getId()).isNotBlank();
        assertThat(episodes.getFirst().getName()).isNotBlank();
        assertThat(episodes.getFirst().getUri()).startsWith("spotify:episode:");
        assertThat(episodes.getFirst().getDuration()).isGreaterThan(Duration.ofMillis(1));
        assertThat(episodes.getFirst().getReleaseAt()).isBeforeOrEqualTo(LocalDate.now());
        assertThat(episodes.getFirst().getShowId()).isEqualTo(show.getId());
    }
}
