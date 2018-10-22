package main.java.za.co.wethinkcode.amoodley.fixme.broker;

import main.java.za.co.wethinkcode.amoodley.fixme.broker.config.Config;
import main.java.za.wethinkcode.amoodley.fixme.core.utilities.SocketTools;
import main.java.za.wethinkcode.amoodley.fixme.core.utilities.Validators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class BrokerClient {

    private BufferedReader userInputReader = null;
    private SocketChannel socketChannel;
    private Selector selector;

    public BrokerClient() {
        try {
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        InetAddress inetAddress = InetAddress.getByName(Config.SERVER_ADDRESS);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, Config.SERVER_PORT);
        selector = Selector.open();
        socketChannel = SocketChannel.open();

        socketChannel.configureBlocking(false);
        socketChannel.connect(inetSocketAddress);
        socketChannel.register(selector, (SelectionKey.OP_CONNECT | SelectionKey.OP_WRITE));
        this.userInputReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            if (selector.selectNow() > 0) {
                boolean isDone = processKeys(selector.selectedKeys());
                if (isDone == true)
                    break ;
            }
        }
    }

    private boolean processKeys(Set<SelectionKey> readySet) throws Exception {
        Iterator<SelectionKey> iterator  = readySet.iterator();

        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();

            if (key.isConnectable()) {
                boolean connected = SocketTools.ProcessConnection(key);

                if (connected == false)
                    return (true);
            }
            if (key.isReadable()) {
                String message = SocketTools.ProcessRead(key);
                System.out.println("[Market]: " + message);
                socketChannel.register(selector, SelectionKey.OP_WRITE);
            }
            if (key.isWritable()) {
                System.out.print("Enter FIX message [MARKET[ID] | BUY or SELL | INSTRUMENT | QUANTITY | PRICE]\n-> ");
                String userInput = this.userInputReader.readLine();

                if (userInput != null && userInput.length() > 0) {
                    if (Validators.ValidateMessage(userInput) == true) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.wrap(userInput.getBytes());
                        socketChannel.write(byteBuffer);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else {
                        System.out.println("Message formatted incorrectly");
                    }
                }
                if (userInput != null && userInput.equalsIgnoreCase("exit")) {
                    socketChannel.close();
                    return true;
                }
            }
        }
        return false;
    }
}































