package pl.rotkom.friday.thirdparty.openai.api.dto;

import lombok.Data;

@Data
public class ParcelMessageDetailsDTO {

    private String messageId;

    private String courierCompany;

    private String sender;

    private String parcelNumber;

    private String deliveryPin;

    private String deliveryAddress;

    private String pickupDateLimit;
}
