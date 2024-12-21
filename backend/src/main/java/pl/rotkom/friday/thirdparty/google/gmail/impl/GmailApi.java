package pl.rotkom.friday.thirdparty.google.gmail.impl;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.BatchDeleteMessagesRequest;
import com.google.api.services.gmail.model.BatchModifyMessagesRequest;
import com.google.api.services.gmail.model.Message;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pl.rotkom.friday.thirdparty.google.GoogleConfig;
import pl.rotkom.friday.thirdparty.google.common.GoogleCommons;
import pl.rotkom.friday.thirdparty.google.gmail.api.dto.MessageDTO;
import pl.rotkom.friday.thirdparty.google.gmail.api.service.IGmailApi;
import pl.rotkom.friday.thirdparty.google.oauth2.GoogleOauth2Manager;


@Component
@Slf4j
//
public class GmailApi implements IGmailApi {

    private final Gmail gmail;

    public GmailApi(GoogleOauth2Manager oauth2Manager, GoogleConfig config) {
        this.gmail = new Gmail.Builder(GoogleCommons.HTTP_TRANSPORT, GoogleCommons.JSON_FACTORY, oauth2Manager.getCredentials())
                .setApplicationName(config.getProjectId())
                .build();
    }

    @Override
    @SneakyThrows
    public List<String> searchMessageIds(String query) {
        List<String> ids = new ArrayList<>();
        String pageToken = null;
        while (true) {
            var response = gmail.users().messages().list("me")
                    .setQ(query)
                    .setPageToken(pageToken)
                    .setMaxResults(500L)
                    .execute();
            if(response.getMessages() == null) {
                break;
            }
            ids.addAll(response.getMessages().stream().map(Message::getId).toList());
            if (response.getNextPageToken() == null) {
                break;
            }
            pageToken = response.getNextPageToken();
        }
        return ids;
    }

    @Override
    public List<MessageDTO> getUnreadMessages() {
        return getMessages(searchMessageIds("is:unread"));
    }

    @Override
    public List<MessageDTO> getMessages(Collection<String> ids) {
        return ids.stream().parallel()
                .map(id -> {
                    try {
                        return gmail.users().messages().get("me", id).execute();
                    } catch (Exception e) {
                        log.error("Error while fetching message with id: {}", id, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(msg -> {
                    MessageDTO dto = new MessageDTO();

                    dto.setId(msg.getId());
                    dto.setSnippet(msg.getSnippet());
                    dto.setReceivedAt(Instant.ofEpochMilli(msg.getInternalDate()));
                    dto.setContentText(MessageUtils.getContent(msg, "text/plain"));
                    dto.setContentHtml(MessageUtils.getContent(msg, "text/html"));
                    dto.setSender(MessageUtils.getHeader(msg, "from"));
                    dto.setSubject(MessageUtils.getHeader(msg, "subject"));

                    return dto;
                })
                .toList();
    }

    @Override
    public void deleteMessages(Collection<String> ids) {
        try {
            var request = new BatchDeleteMessagesRequest();
            request.setIds(new ArrayList<>(ids));
            gmail.users().messages().batchDelete("me", request).execute();
        } catch (Exception e) {
            log.error("Error while batch deleting messages", e);
        }
    }

    @Override
    public void archiveMessages(Collection<String> ids) {
        try {
            var request = new BatchModifyMessagesRequest();
            request.setIds(new ArrayList<>(ids));
            request.setRemoveLabelIds(List.of("INBOX"));
            gmail.users().messages().batchModify("me", request).execute();
        } catch (Exception e) {
            log.error("Error while batch archiving messages", e);
        }
    }

    @Override
    public void markAsRead(Collection<String> ids) {
        try {
            var request = new BatchModifyMessagesRequest();
            request.setIds(new ArrayList<>(ids));
            request.setRemoveLabelIds(List.of("UNREAD"));
            gmail.users().messages().batchModify("me", request).execute();
        } catch (Exception e) {
            log.error("Error while batch markAsRead messages", e);
        }
    }

    @Override
    public void sendMessage(String to, String subject, String text) {

        try {
            var from = gmail.users().getProfile("me").execute().getEmailAddress();

            var content = new Message();

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage email = new MimeMessage(session);

            email.setFrom(new InternetAddress(from));
            email.addRecipient(RecipientType.TO, new InternetAddress(to));
            email.setSubject(subject);
            email.setText(text);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] bytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
            Message message = new Message();
            message.setRaw(encodedEmail);

            message = gmail.users().messages().send("me", message).execute();
        } catch (Exception e) {
            log.error("Error while sending email", e);
        }
    }
}
