package pl.rotkom.friday.thirdparty.etherscan;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "friday.thirdparty.etherscan")
@Configuration
public class EtherscanConfig {

    private String baseUrl;

    private String apiKey;

}
