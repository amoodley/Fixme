package main.java.za.co.wethinkcode.amoodley.fixme.broker;

import main.java.za.co.wethinkcode.amoodley.fixme.broker.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Broker {

    public static void main(String args[]) {

        new Config();
        new BrokerClient();

    }
}
