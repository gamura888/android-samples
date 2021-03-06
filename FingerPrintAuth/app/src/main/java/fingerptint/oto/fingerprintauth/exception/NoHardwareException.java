package fingerptint.oto.fingerprintauth.exception;

/**
 * Created by Otar Iantbelidze on 11/26/16.
 */

public class NoHardwareException extends Exception {
    private String message;

    public NoHardwareException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
