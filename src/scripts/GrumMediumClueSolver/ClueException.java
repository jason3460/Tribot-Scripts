package scripts.GrumMediumClueSolver;

/**
 * Created by Graham on 02/06/2016.
 */
public class ClueException extends Exception {

    private String message;

    public ClueException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
