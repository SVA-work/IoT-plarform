package test.library.exception;

public class NumException extends RuntimeException {

    public NumException(String UserDto) {
        super(UserDto);
    }

    public NumException(Throwable cause) {
        super(cause);
    }

    public NumException(String UserDto, Throwable cause) {
        super(UserDto, cause);
    }
}
