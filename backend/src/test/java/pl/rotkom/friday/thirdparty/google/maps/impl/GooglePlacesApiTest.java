package pl.rotkom.friday.thirdparty.google.maps.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pl.rotkom.friday.thirdparty.google.GoogleConfig;
import pl.rotkom.friday.thirdparty.google.maps.api.dto.SearchPlacesDTO;
import pl.rotkom.friday.thirdparty.google.maps.api.dto.SearchPlacesDTO.CircleDTO;
import pl.rotkom.friday.thirdparty.google.maps.api.dto.SearchPlacesDTO.LocationBiasDTO;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = GoogleConfig.class)
@TestPropertySource("classpath:application-test.properties")
class GooglePlacesApiTest {

    private GooglePlacesApi googlePlacesApi;

    @Autowired
    private GoogleConfig googleConfig;

    @BeforeEach
    void setUp() {
        googlePlacesApi = new GooglePlacesApi(googleConfig);
    }

    @Test
    void shouldReturnDetailsForSearchedPlace() {
        var locationBias = new LocationBiasDTO();

        var circle = new CircleDTO();
        circle.setRadius(1000);
        circle.setCenter(new SearchPlacesDTO.CenterDTO(50.05839803087999, 19.92693900355231));
        locationBias.setCircle(circle);

        var dto = new SearchPlacesDTO();

        dto.setTextQuery("święta krowa");
        dto.setLocationBias(locationBias);
        dto.setMaxResultCount(10);

        var result = googlePlacesApi.searchPlaceId(dto);
        var id = result.get().getPlaces().get(0).getId();

        var details = googlePlacesApi.getPlaceDetails(id).get();

        assertThat(details.getId()).isEqualTo("ChIJBysg2hFbFkcRC-6q1YA5c4w");
        assertThat(details.getDisplayName()).isEqualTo("Święta Krowa");
        assertThat(details.getFormattedAddress()).isEqualTo("Smoleńsk 22, 31-112 Kraków, Polska");
        assertThat(details.getNationalPhoneNumber()).isEqualTo("733 701 866");
        assertThat(details.getRating()).isBetween(4., 5.);
        assertThat(details.getOpeningHoursDTO().getWeekdayText())
                .anyMatch(v -> v.startsWith("poniedziałek"));

        assertThat(details.getPriceLevel()).isEqualTo("1");
        assertThat(details.getGoogleMapsUrl().toString()).startsWith("https://maps.google.com");
        assertThat(details.getWebsiteUrl().toString()).startsWith("http://www.facebook.com");
        assertThat(details.getUserRatingCount()).isGreaterThan(100);
        assertThat(details.getTypes()).contains("FOOD");
    }
}
