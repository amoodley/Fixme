package main.java.za.co.wethinkcode.amoodley.fixme.router;


import main.java.za.co.wethinkcode.amoodley.fixme.router.config.Config;
import main.java.za.co.wethinkcode.amoodley.fixme.router.server.Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {

    public static void main(String args[]){

        new Config();

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Server(Config.BROKER_PORT));
        executorService.submit(new Server(Config.MARKET_PORT));
        executorService.shutdown();

    }
}
