package pl.rotkom.friday.core.repository.model.order;

import java.time.Instant;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import pl.rotkom.friday.core.repository.model.common.ExternalEntity;
import pl.rotkom.friday.core.repository.model.common.Money;


@Entity
@Table(name = "fr_order")
@Data
public class Order extends ExternalEntity {

    private Instant orderDate;

    @Embedded
    private Money money;

    @Enumerated(EnumType.STRING)
    private Source source;

}
