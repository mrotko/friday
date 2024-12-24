package pl.rotkom.friday.thirdparty.common.rq;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class HttpRequestException extends RequestException {

    private final String url;

    private final HttpStatus status;

    private final ErrorDetails errorDetails;

    public HttpRequestException(String url, HttpStatus status, String description) {
        this(url, status, description, null);
    }

    public HttpRequestException(String url, HttpStatus status, String description, ErrorDetails errorDetails) {
        super("Request to " + url + " failed with status " + status + ": " + description);
        this.url = url;
        this.status = status;
        this.errorDetails = errorDetails;
    }
}
