package pl.rotkom.friday.core.repository.model.youtube;

import java.time.Instant;
import java.util.Locale;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import pl.rotkom.friday.core.repository.model.common.ExternalEntity;

@Entity
@Table(name = "fr_youtube_video")
@Data
public class YouTubeVideo extends ExternalEntity {

    private String name;

    private String url;

    private Long channelId;

    private Instant publishedAt;

    private Locale language;

    private Long durationSec;

}
