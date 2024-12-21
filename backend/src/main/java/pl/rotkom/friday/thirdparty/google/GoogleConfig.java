package pl.rotkom.friday.thirdparty.google;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "friday.thirdparty.google")
@Data
public class GoogleConfig {

    private String clientId;

    private String projectId;

    private String authUri;

    private String tokenUri;

    private String authProviderX509CertUrl;

    private String clientSecret;

    private List<String> redirectUris;

    private List<String> scopes;

    private String apiKey;
}
