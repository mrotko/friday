package pl.rotkom.friday.core.repository.model.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    ETH("WEI", BigDecimal.valueOf(1000000000000000000L))
    ;

    private final String unitName;

    private final BigDecimal unitAmount;

    public BigDecimal fromUnit(BigDecimal amount) {
        return amount.divide(unitAmount, RoundingMode.DOWN);
    }

    public BigDecimal toUnit(BigDecimal amount) {
        return amount.multiply(unitAmount);
    }
}
