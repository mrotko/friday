package pl.rotkom.friday.thirdparty.openai.impl;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi.ChatModel;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.openai.api.ResponseFormat.Type;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pl.rotkom.friday.library.json.JsonMapper;
import pl.rotkom.friday.thirdparty.openai.api.dto.ParcelMessageDetailsDTO;
import pl.rotkom.friday.thirdparty.openai.api.dto.ParcelMessageRequestDTO;
import pl.rotkom.friday.thirdparty.openai.api.dto.ParcelMessageResponseDTO;
import pl.rotkom.friday.thirdparty.openai.api.service.IOpenAiApi;


@Component
@RequiredArgsConstructor
class OpenAiApi implements IOpenAiApi {

    private final OpenAiChatModel chatModel;

    @Override
    public ParcelMessageResponseDTO analyzeParcelMessage(ParcelMessageRequestDTO dto) {
        var options = OpenAiChatOptions.builder()
                .withModel(ChatModel.GPT_4_O_MINI)
                .withResponseFormat(new ResponseFormat(Type.JSON_OBJECT, null))
                .build();

        var template = """
            analyze email and return json with fields
            messageId,courierCompany,sender,parcelNumber,deliveryPin,deliveryAddress,pickupDateLimit
            date should be in format yyyy-mm-dd
            courier company is enum (for example: IN_POST, DPD, DHL )
            deliveryPin is a number
            email:
            """;

        var items = dto.getItems().parallelStream()
                .map(n -> new UserMessage(template + n))
                .map(userMessage -> {
                    var prompt = new Prompt(userMessage, options);
                    return chatModel.call(prompt).getResult().getOutput().getContent();
                })
                .map(result -> JsonMapper.deserialize(result, ParcelMessageDetailsDTO.class))
                .toList();

        var response = new ParcelMessageResponseDTO();
        response.setItems(items);
        return response;
    }
}
