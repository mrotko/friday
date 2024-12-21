package pl.rotkom.friday.thirdparty.openai.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ParcelMessageResponseDTO {

    private List<ParcelMessageDetailsDTO> items = new ArrayList<>();

}
