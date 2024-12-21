package pl.rotkom.friday.thirdparty.openai.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ParcelMessageRequestDTO {

    private List<String> items = new ArrayList<>();


}
