package pl.rotkom.friday.thirdparty.etherscan.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pl.rotkom.friday.thirdparty.etherscan.EtherscanConfig;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = EtherscanConfig.class)
@TestPropertySource("classpath:application-test.properties")
class EtherscanApiImplTest {

    private EtherscanApiImpl etherscanApi;

    @Autowired
    private EtherscanConfig config;

    @BeforeEach
    void setUp() {
        etherscanApi = new EtherscanApiImpl(config);
    }

    @Test
    void shouldReturnTransactions() {
        // given
        String address = "0xd779332c5A52566Dada11A075a735b18DAa6c1f4";

        // when
        var rs = etherscanApi.getAccountTransactions(address).getOrThrow();

        Assertions.assertThat(rs).isNotEmpty();
    }
}
