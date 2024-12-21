package pl.rotkom.friday.thirdparty.google.maps.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SearchPlacesDTO {

    private String textQuery;

    private Integer maxResultCount;

    private LocationBiasDTO locationBias;

    @Data
    public static class LocationBiasDTO {

        private CircleDTO circle;
    }

    @Data
    public static class CircleDTO {

        private CenterDTO center;

        private int radius;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CenterDTO {

        private Double latitude;

        private Double longitude;
    }
}
