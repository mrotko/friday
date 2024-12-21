package pl.rotkom.friday.thirdparty.google.gmail.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;

public class MessageUtils {


    static String getContent(Message message, String contentType) {
        return Optional.ofNullable(message.getPayload())
                .flatMap(mp -> MessageUtils.findPartWithContentType(mp, contentType))
                .map(MessagePart::getBody)
                .map(MessagePartBody::decodeData)
                .map(String::new)
                .orElse(null);
    }

    private static Optional<MessagePart> findPartWithContentType(MessagePart messagePart, String contentType) {
        if (messagePart.getMimeType().startsWith("multipart")) {
            return messagePart.getParts().stream()
                    .map(mp -> findPartWithContentType(mp, contentType).orElse(null))
                    .filter(Objects::nonNull)
                    .findFirst();
        }
        if (messagePart.getMimeType().equalsIgnoreCase(contentType)) {
            return Optional.of(messagePart);
        }
        return Optional.empty();
    }


    static String getHeader(Message message, String name) {
        return Optional.ofNullable(message.getPayload())
                .map(MessagePart::getHeaders)
                .map(headers -> headers.stream().filter(p -> p.getName().equalsIgnoreCase(name)))
                .flatMap(Stream::findFirst)
                .map(MessagePartHeader::getValue)
                .orElse(null);
    }
}
