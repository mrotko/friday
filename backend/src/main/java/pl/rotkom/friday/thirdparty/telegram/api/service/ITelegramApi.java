package pl.rotkom.friday.thirdparty.telegram.api.service;


import pl.rotkom.friday.thirdparty.common.rq.RequestResult;
import pl.rotkom.friday.thirdparty.telegram.api.dto.MessageDTO;
import pl.rotkom.friday.thirdparty.telegram.api.dto.SendMessageRsDTO;

public interface ITelegramApi {

    RequestResult<SendMessageRsDTO> sendMessage(MessageDTO dto);
}
