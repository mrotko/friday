package pl.rotkom.friday.thirdparty.telegram.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "friday.thirdparty.telegram")
@Configuration
public class TelegramConfig {

    private String baseUrl;

    private String token;

    private String chatId;

}
