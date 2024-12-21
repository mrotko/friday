package pl.rotkom.friday.thirdparty.spotify;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "friday.thirdparty.spotify")
@Data
public class SpotifyConfig {

    private String clientId;

    private String clientSecret;

    private String redirectUri;

    private String scope;

    private String refreshToken;
}
