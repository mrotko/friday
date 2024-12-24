package pl.rotkom.friday.thirdparty.telegram.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SendMessageRsDTO {

    private boolean ok;

    private Result result;

    @Data
    public static class Result {

        @JsonProperty("message_id")
        private int messageId;

        @JsonProperty("sender_chat")
        private Chat senderChat;

        private Chat chat;

        private int date;

        private String text;

        @Data
        public static class Chat {

            private long id;

            private String title;

            private String username;

            private String type;
        }
    }
}
