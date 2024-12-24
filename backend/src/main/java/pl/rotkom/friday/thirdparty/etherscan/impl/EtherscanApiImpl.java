package pl.rotkom.friday.thirdparty.etherscan.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;

import io.netty.handler.codec.http.HttpStatusClass;
import pl.rotkom.friday.library.json.JsonMapper;
import pl.rotkom.friday.thirdparty.common.rq.RequestException;
import pl.rotkom.friday.thirdparty.common.rq.RequestResult;
import pl.rotkom.friday.thirdparty.etherscan.EtherscanConfig;
import pl.rotkom.friday.thirdparty.etherscan.api.dto.AccountTxDTO;
import pl.rotkom.friday.thirdparty.etherscan.api.dto.EtherRsDTO;
import pl.rotkom.friday.thirdparty.etherscan.api.service.IEtherscanApi;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
class EtherscanApiImpl implements IEtherscanApi {

    private final EtherscanConfig config;

    private final HttpClient httpClient;

    public EtherscanApiImpl(EtherscanConfig config) {
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
    public RequestResult<List<AccountTxDTO>> getAccountTransactions(String address) {

        var uri = UriComponentsBuilder.fromPath("/v2/api")
                .queryParam("chainid", "1")
                .queryParam("module", "account")
                .queryParam("action", "txlist")
                .queryParam("address", address)
                .queryParam("sort", "desc")
                .queryParam("apikey", config.getApiKey())
                .build();

        try {
            return httpClient.get().uri(uri.toUriString())
                    .responseSingle((resp, bb) -> {
                        if (resp.status().codeClass() == HttpStatusClass.SUCCESS) {
                            return JsonMapper
                                    .deserialize(bb, EtherRsDTO.class)
                                    .flatMap(dto -> {
                                        if (dto.getStatus().intValue() == 0) {
                                            // todo(mr): better error handling
                                            return Mono.error(new RequestException(String.valueOf(dto.getResult())));
                                        } else {
                                            var items = JsonMapper.convert(
                                                    dto.getResult(),
                                                    new TypeReference<List<AccountTxDTO>>() {}
                                            );
                                            return Mono.just(items);
                                        }
                                    });
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
