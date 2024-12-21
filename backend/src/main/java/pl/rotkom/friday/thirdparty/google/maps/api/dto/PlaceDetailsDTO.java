package pl.rotkom.friday.thirdparty.google.maps.api.dto;

import java.net.URL;
import java.util.List;

import com.google.maps.PlaceDetailsRequest.FieldMask;

import lombok.Data;

@Data
public class PlaceDetailsDTO {

    public static FieldMask[] FIELDS = new FieldMask[]{
            FieldMask.PLACE_ID,
            FieldMask.NAME,
            FieldMask.FORMATTED_ADDRESS,
            FieldMask.FORMATTED_PHONE_NUMBER,
            FieldMask.RATING,
            FieldMask.OPENING_HOURS,
            FieldMask.PRICE_LEVEL,
            FieldMask.URL,
            FieldMask.WEBSITE,
            FieldMask.USER_RATINGS_TOTAL,
            FieldMask.TYPES
    };

    private String id;

    private String displayName;

    private String formattedAddress;

    private String nationalPhoneNumber;

    private Double rating;

    private OpeningHoursDTO openingHoursDTO;

    private String priceLevel;

    private URL googleMapsUrl;

    private URL websiteUrl;

    private int userRatingCount;

    private List<String> types;


}
