package pl.rotkom.friday.thirdparty.google.gmail.api.service;

import java.util.Collection;
import java.util.List;

import pl.rotkom.friday.thirdparty.google.gmail.api.dto.MessageDTO;


public interface IGmailApi {

    List<String> searchMessageIds(String query);

    List<MessageDTO> getUnreadMessages();

    List<MessageDTO> getMessages(Collection<String> ids);

    void deleteMessages(Collection<String> ids);

    void archiveMessages(Collection<String> ids);

    void markAsRead(Collection<String> ids);

    void sendMessage(String to, String subject, String text);
}
