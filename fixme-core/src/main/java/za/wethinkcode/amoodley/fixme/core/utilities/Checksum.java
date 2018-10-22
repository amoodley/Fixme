package main.java.za.wethinkcode.amoodley.fixme.core.utilities;

public class Checksum {
    private static String separator = "|";

    public static String generate(String message)
    {
        String[] mssg_parts = message.split("\\|");

        String marketId = mssg_parts[0];
        String msgType = mssg_parts[1];
        String instrument = mssg_parts[2];
        String qty = mssg_parts[3];
        String price = mssg_parts[4];

        String checksum = marketId + separator + msgType + separator + Encrypt.encrypt(instrument + separator + qty + separator + price);
        return (checksum);
    }
}
