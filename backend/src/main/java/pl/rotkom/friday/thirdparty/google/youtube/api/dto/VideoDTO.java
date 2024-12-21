package pl.rotkom.friday.thirdparty.google.youtube.api.dto;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import lombok.Data;

@Data
public class VideoDTO {

    private String id;

    private VideoTypeDTO type;

    private String url;

    private String title;

    private String channelId;

    private String channelTitle;

    private Instant publishedAt;

    private Locale language;

    private Duration duration;
}
