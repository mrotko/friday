package pl.rotkom.friday.thirdparty.openai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "friday.thirdparty.openai")
@Data
public class OpenAiConfig {

    private String token;
}
