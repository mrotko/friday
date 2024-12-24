package pl.rotkom.friday.thirdparty.etherscan.api.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccountTxDTO {

    private BigDecimal blockNumber;

    private Long timeStamp;

    private String hash;

    private BigDecimal nonce;

    private String blockHash;

    private BigDecimal transactionIndex;

    private String from;

    private String to;

    private BigDecimal value;

    private BigDecimal gas;

    private BigDecimal gasPrice;

    private BigDecimal isError;

    @JsonProperty("txreceipt_status")
    private BigDecimal txreceiptStatus;

    private String input;

    private String contractAddress;

    private BigDecimal cumulativeGasUsed;

    private BigDecimal gasUsed;

    private BigDecimal confirmations;

    private String methodId;

    private String functionName;
}
