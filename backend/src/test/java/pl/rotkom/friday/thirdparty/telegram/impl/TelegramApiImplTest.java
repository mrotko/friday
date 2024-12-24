package pl.rotkom.friday.thirdparty.telegram.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pl.rotkom.friday.thirdparty.telegram.api.TelegramConfig;
import pl.rotkom.friday.thirdparty.telegram.api.dto.MessageDTO;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = TelegramConfig.class)
@TestPropertySource("classpath:application-test.properties")
class TelegramApiImplTest {

    private TelegramApiImpl telegramApi;

    @Autowired
    private TelegramConfig config;

    @BeforeEach
    void setUp() {
        telegramApi = new TelegramApiImpl(config);
    }

    @Test
    void shouldSendMessage() {
        // given
        MessageDTO dto = new MessageDTO();
        dto.setMessage("Test message");

        var rs = telegramApi.sendMessage(dto).getOrThrow();

        Assertions.assertThat(rs.isOk()).isTrue();
    }
}
