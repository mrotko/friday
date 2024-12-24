package pl.rotkom.friday.thirdparty.etherscan.api.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class EtherRsDTO {

    private BigDecimal status;

    private String message;

    private Object result;
}
