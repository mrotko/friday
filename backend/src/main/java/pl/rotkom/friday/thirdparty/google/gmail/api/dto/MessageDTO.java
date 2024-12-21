package pl.rotkom.friday.thirdparty.google.gmail.api.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class MessageDTO {

    private String id;

    private Instant receivedAt;

    private String snippet;

    private String sender;

    private String subject;

    private String contentText;

    private String contentHtml;

    private Boolean delivered;

}
