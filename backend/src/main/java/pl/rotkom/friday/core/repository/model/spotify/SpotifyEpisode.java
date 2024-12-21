package pl.rotkom.friday.core.repository.model.spotify;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import pl.rotkom.friday.core.repository.model.common.ExternalEntity;

@Entity
@Table(name = "fr_spotify_episode")
@Data
public class SpotifyEpisode extends ExternalEntity {

    private String name;

    private String uri;

    private Long showId;

    @Column(name = "duration_sec")
    private Long durationDec;

    private LocalDate releaseAt;

}
