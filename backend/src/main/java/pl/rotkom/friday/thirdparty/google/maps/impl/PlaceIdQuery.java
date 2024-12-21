package pl.rotkom.friday.thirdparty.google.maps.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import lombok.Data;
import lombok.extern.log4j.Log4j2;


@Data
@Log4j2
public class PlaceIdQuery {

    private static final String SHORT_DOMAIN = "https://maps.app.goo.gl";

    private static final String REDIRECT_DOMAIN = "https://consent.google.com";

    private static final String CAPTCHA_DOMAIN = "https://www.google.com/sorry/index";

    private static final String CID_PREFIX = "https://maps.google.com/?cid=";


    private String name;

    private String address;

    private Double latitude;

    private Double longitude;

    public static PlaceIdQuery fromUrl(String url) {
        if (url.startsWith(CID_PREFIX)) {
            throw new IllegalArgumentException("CID URL is not supported");
        }

        try {
            url = normalizeUrl(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var query = new PlaceIdQuery();
        String[] coordinates = url.split("@")[1].split(",");
        query.latitude = Double.parseDouble(coordinates[0]);
        query.longitude = Double.parseDouble(coordinates[1].split(",")[0]);
        query.name = url.split("/")[5].split("@")[0].replace("+", " ");
        return query;
    }

    public static PlaceIdQuery fromName(String name, String address) {
        var query = new PlaceIdQuery();
        query.setName(name);
        query.setAddress(address);
        return query;
    }

    private static String normalizeUrl(String url) throws IOException {
        String result = url;
        if (url.startsWith(SHORT_DOMAIN) || url.startsWith(CID_PREFIX)) {
            result = getRedirectUrl(result);
            result = getRedirectUrl(result);
            if (result.startsWith(REDIRECT_DOMAIN)) {
                result = result.replace(REDIRECT_DOMAIN + "/ml?continue=", "");
            }
            if (result.startsWith(CAPTCHA_DOMAIN)) {
                result = result.replace(CAPTCHA_DOMAIN + "?continue=", "");
            }
        } else {
            result = java.net.URLDecoder.decode(url, StandardCharsets.UTF_8);
        }
        return java.net.URLDecoder.decode(result, StandardCharsets.UTF_8);
    }

    private static String getRedirectUrl(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.connect();
        String redirectedUrl = conn.getHeaderField("Location");
        conn.disconnect();
        return redirectedUrl != null ? redirectedUrl : url;
    }
}
