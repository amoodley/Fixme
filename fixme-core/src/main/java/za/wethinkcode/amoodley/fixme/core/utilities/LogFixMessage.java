package main.java.za.wethinkcode.amoodley.fixme.core.utilities;

public class LogFixMessage {

    private static String _message;

    public static String LogMessage(String encryptedStr){
        String[] msg_parts = encryptedStr.split("\\|");

        _message = "MARKET=" + msg_parts[0] + " | REQUEST_TYPE=" + msg_parts[1] + " | CHECKSUM=" + msg_parts[2];
        return (_message);
    }
}
