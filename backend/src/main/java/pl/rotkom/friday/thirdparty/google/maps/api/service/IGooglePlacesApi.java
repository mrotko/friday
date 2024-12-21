package pl.rotkom.friday.thirdparty.google.maps.api.service;

import java.util.concurrent.CompletableFuture;

import io.vavr.control.Try;
import pl.rotkom.friday.thirdparty.google.maps.api.dto.PlaceDetailsDTO;
import pl.rotkom.friday.thirdparty.google.maps.api.dto.SearchPlaceResponseDTO;
import pl.rotkom.friday.thirdparty.google.maps.api.dto.SearchPlacesDTO;


public interface IGooglePlacesApi {

    Try<SearchPlaceResponseDTO> searchPlaceId(SearchPlacesDTO dto);

    Try<PlaceDetailsDTO> getPlaceDetails(String placeId);
}
