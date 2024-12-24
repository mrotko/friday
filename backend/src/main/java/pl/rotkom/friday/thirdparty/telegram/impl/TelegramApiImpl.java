package pl.rotkom.friday.thirdparty.telegram.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.netty.handler.codec.http.HttpStatusClass;
import pl.rotkom.friday.library.json.JsonMapper;
import pl.rotkom.friday.thirdparty.common.rq.RequestResult;
import pl.rotkom.friday.thirdparty.telegram.api.TelegramConfig;
import pl.rotkom.friday.thirdparty.telegram.api.dto.MessageDTO;
import pl.rotkom.friday.thirdparty.telegram.api.dto.SendMessageRsDTO;
import pl.rotkom.friday.thirdparty.telegram.api.service.ITelegramApi;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;

@Component
class TelegramApiImpl implements ITelegramApi {

    private final HttpClient httpClient;

    private final TelegramConfig config;

    public TelegramApiImpl(TelegramConfig config) {
        this.config = config;
        this.httpClient = reactor.netty.http.client.HttpClient.create()
                .baseUrl(config.getBaseUrl())
                .headers(h -> {
                    h.add("Content-Type", "application/json");
                })
                .headersWhen(h -> Mono.fromSupplier(() -> h.add("X-Request-Id", UUID.randomUUID().toString())));
//                .wiretap(TodoistApi.class.getName(), LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL);
    }

    @Override
    public RequestResult<SendMessageRsDTO> sendMessage(MessageDTO dto) {
        Map<String, Object> rq = new HashMap<>();
        rq.put("chat_id", config.getChatId());
        rq.put("text", dto.getMessage());
        rq.put("parse_mode", "Markdown");

        try {
            return httpClient.post()
                    .uri("/bot" + config.getToken() + "/sendMessage")
                    .send(ByteBufFlux.fromString(Mono.just(JsonMapper.serialize(rq))))
                    .responseSingle((resp, bb) -> {
                        if (resp.status().codeClass() == HttpStatusClass.SUCCESS) {
                            return JsonMapper.deserialize(bb, SendMessageRsDTO.class);
                        } else {
                            // todo(mr): better error handling
                            return bb.asString().flatMap(body -> Mono.error(new RuntimeException("Error sending message: " + body)));
                        }
                    })
                    .map(RequestResult::success)
                    .block();
        } catch (Exception e) {
            return RequestResult.error("Error sending message", e);
        }
    }
}
