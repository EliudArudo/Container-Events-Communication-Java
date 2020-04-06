package interfaces;


public class JSONResponseWithResult {
    String message;
    Object result;

    public JSONResponseWithResult(String message, Object result) {
        this.message = message;
        this.result = result;
    };
}
