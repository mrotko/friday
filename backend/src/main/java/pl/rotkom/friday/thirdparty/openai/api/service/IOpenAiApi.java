package pl.rotkom.friday.thirdparty.openai.api.service;


import pl.rotkom.friday.thirdparty.openai.api.dto.ParcelMessageRequestDTO;
import pl.rotkom.friday.thirdparty.openai.api.dto.ParcelMessageResponseDTO;

public interface IOpenAiApi {

    ParcelMessageResponseDTO analyzeParcelMessage(ParcelMessageRequestDTO dto);
}
