package pl.rotkom.friday.thirdparty.google.maps.api.dto;

import java.util.List;

import lombok.Data;

@Data
public class SearchPlaceResponseDTO {

    private List<PlaceDTO> places;

    @Data
    public static class PlaceDTO {

        private String id;
    }

}
