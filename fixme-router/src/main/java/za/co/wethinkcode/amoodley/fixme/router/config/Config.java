package main.java.za.co.wethinkcode.amoodley.fixme.router.config;

public class Config {

    public static String SERVER_ADDRESS;
    public static int BROKER_PORT;
    public static int MARKET_PORT;

    public Config(){
        this.init();
    }

    private void init(){
        SERVER_ADDRESS = "127.0.0.1";
        BROKER_PORT = 5000;
        MARKET_PORT = 5001;
    }
}
