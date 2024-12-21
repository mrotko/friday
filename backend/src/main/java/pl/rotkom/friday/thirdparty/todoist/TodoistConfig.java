package pl.rotkom.friday.thirdparty.todoist;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "friday.thirdparty.todoist")
@Data
public class TodoistConfig {

    private String token;
}
