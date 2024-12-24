package pl.rotkom.friday.core.repository.model.common;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Money {

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;
}
