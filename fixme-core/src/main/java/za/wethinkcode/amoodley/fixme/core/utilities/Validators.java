package main.java.za.wethinkcode.amoodley.fixme.core.utilities;

public class Validators {

    private static  boolean isValid;
    public static String price;
    public static String msg_type;
    public static String instrument;
    public static String quantity;
    public static String marketID;

    public static boolean ValidateMessage(String message) {
        isValid = false;
        try {
            String[] msg_parts = message.split("\\|");

            if (msg_parts.length == 5) {
                marketID = msg_parts[0];
                msg_type = msg_parts[1];
                instrument = msg_parts[2];
                quantity = msg_parts[3];
                price = msg_parts[4];
            }

            if (validateInstrument(marketID, quantity, price) == true && validateMessage(msg_type) == true) {
                isValid = true;
            } else {
                isValid = false;
            }

        } catch (Exception x) {}
        return (isValid);
    }

    public static boolean validateMessage(String message) {
        if (message.equalsIgnoreCase("Buy"))
            return true;
        else if (message.equalsIgnoreCase("Sell"))
            return true;
        return false;
    }

    public static boolean validateInstrument(String ID, String qty, String price) {
        try {
            Integer.parseInt(ID);
            Integer.parseInt(qty);
            Integer.parseInt(price);
            return (true);
        } catch (NumberFormatException e) {
            System.out.println("\nOne of your inputs [marketID || quantity || price is not an integer]");
        }
        return false;
    }
}
