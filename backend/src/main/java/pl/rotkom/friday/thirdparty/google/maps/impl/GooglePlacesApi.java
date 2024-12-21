package pl.rotkom.friday.thirdparty.google.maps.impl;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.maps.FindPlaceFromTextRequest.FieldMask;
import com.google.maps.FindPlaceFromTextRequest.InputType;
import com.google.maps.FindPlaceFromTextRequest.LocationBias;
import com.google.maps.FindPlaceFromTextRequest.LocationBiasCircular;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;

import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;
import pl.rotkom.friday.library.utils.FEnumUtils;
import pl.rotkom.friday.thirdparty.google.GoogleConfig;
import pl.rotkom.friday.thirdparty.google.maps.api.dto.PlaceDetailsDTO;
import pl.rotkom.friday.thirdparty.google.maps.api.dto.OpeningHoursDTO;
import pl.rotkom.friday.thirdparty.google.maps.api.dto.SearchPlaceResponseDTO;
import pl.rotkom.friday.thirdparty.google.maps.api.dto.SearchPlaceResponseDTO.PlaceDTO;
import pl.rotkom.friday.thirdparty.google.maps.api.dto.SearchPlacesDTO;
import pl.rotkom.friday.thirdparty.google.maps.api.service.IGooglePlacesApi;


@Log4j2
@Component
public class GooglePlacesApi implements IGooglePlacesApi {

    private final GeoApiContext context;

    public GooglePlacesApi(GoogleConfig config) {
        this.context = new GeoApiContext.Builder()
                .apiKey(config.getApiKey())
                .build();
    }

    @Override
    public Try<SearchPlaceResponseDTO> searchPlaceId(SearchPlacesDTO dto) {
        return Try.of(() -> {
            LocationBias locationBias = null;
            if (dto.getLocationBias() != null && dto.getLocationBias().getCircle() != null) {
                var center = dto.getLocationBias().getCircle().getCenter();
                locationBias = new LocationBiasCircular(
                        new LatLng(center.getLatitude(), center.getLongitude()),
                        dto.getLocationBias().getCircle().getRadius()
                );
            }

            var rs = PlacesApi.findPlaceFromText(context, dto.getTextQuery(), InputType.TEXT_QUERY)
                    .fields(FieldMask.PLACE_ID)
                    .locationBias(locationBias)
                    .await();

            var places = Arrays.stream(rs.candidates)
                    .map(p -> {
                        var place = new PlaceDTO();
                        place.setId(p.placeId);
                        return place;
                    })
                    .collect(Collectors.toList());

            var response = new SearchPlaceResponseDTO();
            response.setPlaces(places);
            return response;
        });
    }

    @Override
    public Try<PlaceDetailsDTO> getPlaceDetails(String placeId) {
        return Try.of(() -> {
            var rs = PlacesApi.placeDetails(context, placeId)
                    .fields(PlaceDetailsDTO.FIELDS)
                    .language("pl")
                    .await();

            var details = new PlaceDetailsDTO();

            details.setId(rs.placeId);
            details.setDisplayName(rs.name);
            details.setFormattedAddress(rs.formattedAddress);
            details.setNationalPhoneNumber(rs.formattedPhoneNumber);
            details.setRating((double) rs.rating);
            details.setGoogleMapsUrl(rs.url);
            details.setWebsiteUrl(rs.website);
            details.setUserRatingCount(rs.userRatingsTotal);
            details.setTypes(FEnumUtils.toStringList(rs.types));

            if(rs.priceLevel != null) {
                details.setPriceLevel(rs.priceLevel.toString());
            }
            if (rs.openingHours != null) {
                var dto = new OpeningHoursDTO();
                dto.setWeekdayText(Arrays.asList(rs.openingHours.weekdayText));
                details.setOpeningHoursDTO(dto);
            }

            return details;
        });
    }
}
