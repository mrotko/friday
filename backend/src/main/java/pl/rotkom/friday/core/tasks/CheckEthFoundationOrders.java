package pl.rotkom.friday.core.tasks;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pl.rotkom.friday.core.repository.model.common.Currency;
import pl.rotkom.friday.core.repository.model.common.Money;
import pl.rotkom.friday.core.repository.model.order.Order;
import pl.rotkom.friday.core.repository.model.order.Source;
import pl.rotkom.friday.core.repository.service.order.IOrderService;
import pl.rotkom.friday.thirdparty.etherscan.api.service.IEtherscanApi;
import pl.rotkom.friday.thirdparty.telegram.api.dto.MessageDTO;
import pl.rotkom.friday.thirdparty.telegram.api.service.ITelegramApi;

@Component
@Slf4j
@RequiredArgsConstructor
public class CheckEthFoundationOrders {

    private static final Long BEGINNING_SEC = Instant.parse("2024-12-01T00:00:00Z").getEpochSecond();

    private static final String ETH_FOUNDATION_ADDRESS = "0xd779332c5A52566Dada11A075a735b18DAa6c1f4";

    private final IEtherscanApi etherscanApi;

    private final IOrderService orderService;

    private final ITelegramApi telegramApi;

    @SneakyThrows
    @Scheduled(fixedRate = 30,initialDelay = 0, timeUnit = TimeUnit.MINUTES)
    public void check() {
        var currentIds = orderService.getExternalIds();

        var items = etherscanApi.getAccountTransactions(ETH_FOUNDATION_ADDRESS).getOrThrow().stream()
                .filter(tx -> tx.getTimeStamp() >= BEGINNING_SEC)
                .filter(tx -> tx.getFunctionName().startsWith("createOrder"))
                .filter(tx -> !currentIds.contains(tx.getHash()))
                .map(tx -> {
                    var order = new Order();

                    order.setOrderDate(Instant.ofEpochSecond(tx.getTimeStamp()));
                    order.setMoney(new Money(Currency.ETH.fromUnit(tx.getValue()), Currency.ETH));
                    order.setSource(Source.ETHERSCAN);
                    order.setExternalId(tx.getHash());

                    return order;
                })
                .toList();

        log.info("New orders: {}", items.size());

        items.forEach(o -> {
            String msg = """
                    New ETH Foundation order:
                    - hash: `%s`
                    - date: `%s`
                    - value: `%s ETH`
                    """.formatted(o.getExternalId(), o.getOrderDate(), o.getMoney().getAmount());
            var dto = new MessageDTO();
            dto.setMessage(msg);
            telegramApi.sendMessage(dto);
            orderService.save(o);
        });
    }
}


