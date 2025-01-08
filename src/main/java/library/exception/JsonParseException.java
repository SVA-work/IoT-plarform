package library.exception;

public class JsonParseException extends NumException {

    public JsonParseException(String UserDto, Throwable cause) {
        super(message, cause);
    }
}
