package pl.rotkom.friday.thirdparty.spotify.api.dto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EpisodeDTO {

    private String id;

    private String name;

    private String uri;

    private Duration duration;

    private LocalDate releaseAt;

    private String showId;
}
