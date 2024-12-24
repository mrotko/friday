package pl.rotkom.friday.thirdparty.common.rq;

import org.springframework.http.HttpStatus;

import io.vavr.control.Either;
import lombok.Data;
import reactor.netty.http.client.HttpClientResponse;

@Data
public class RequestResult<T> {

    private final Either<RequestException, T> data;

    public boolean isSuccess() {
        return data.isRight();
    }

    public boolean isError() {
        return data.isLeft();
    }

    public T getData() {
        return data.get();
    }

    public T getOrThrow() {
        return data.getOrElseThrow(e -> e);
    }

    public RequestException getError() {
        return data.getLeft();
    }

    public static <T> RequestResult<T> success(T data) {
        return new RequestResult<>(Either.right(data));
    }

    public static <T> RequestResult<T> error(String message) {
        return new RequestResult<>(Either.left(new RequestException(message)));
    }

    public static <T> RequestResult<T> error(String message, Throwable cause) {
        return new RequestResult<>(Either.left(new RequestException(message, cause)));
    }

    public static <T> RequestResult<T> httpError(HttpClientResponse rs, String message) {
        var exception = new HttpRequestException(
                rs.resourceUrl(),
                HttpStatus.resolve(rs.status().code()),
                message
        );

        return new RequestResult<>(Either.left(exception));
    }

    public static <T> RequestResult<T> httpError(HttpClientResponse rs, String message, ErrorDetails details) {
        var exception = new HttpRequestException(
                rs.resourceUrl(),
                HttpStatus.resolve(rs.status().code()),
                message,
                details
        );

        return new RequestResult<>(Either.left(exception));
    }
}
