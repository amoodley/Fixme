package main.java.za.co.wethinkcode.amoodley.fixme.router.server;

import main.java.za.co.wethinkcode.amoodley.fixme.router.config.Config;
import main.java.za.co.wethinkcode.amoodley.fixme.router.model.MessageModel;
import main.java.za.co.wethinkcode.amoodley.fixme.router.model.SocketModel;
import main.java.za.wethinkcode.amoodley.fixme.core.utilities.Checksum;
import main.java.za.wethinkcode.amoodley.fixme.core.utilities.Convertor;
import main.java.za.wethinkcode.amoodley.fixme.core.utilities.LogFixMessage;
import main.java.za.wethinkcode.amoodley.fixme.core.utilities.SocketTools;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Server implements Runnable {

    private int _port;
    private static List<MessageModel> _messageList;
    private static List<SocketModel> _brokerList;
    private static List<SocketModel> _marketList;
    private static String store_msg_type;

    public Server(int port){
        this._port = port;
        _messageList = new ArrayList<MessageModel>();
        _brokerList = new ArrayList<SocketModel>();
        _marketList = new ArrayList<SocketModel>();
    }

    public void run() {
        try {
            this.init();
        } catch (Exception e) {
            System.out.println(this.getClass().getSimpleName() + "[Exception] " + e.getMessage());
        }
    }

    public void init() throws Exception {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(Config.SERVER_ADDRESS, this._port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server running: " + serverSocketChannel.getLocalAddress());

            while(true){
                if (selector.selectNow() <= 0){
                    continue;
                }
                this.processKeys(selector.selectedKeys());
            }

        } catch (Exception e) {
            System.out.println("[Exception]" + e.getMessage());
        }
    }

    private void processKeys(Set<SelectionKey> readySet){
        try {
            Iterator<SelectionKey> iterator = readySet.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()){
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = (SocketChannel) serverSocketChannel.accept();
                    String clientType = null;
                    String socketAddress = socketChannel.getRemoteAddress().toString();

                    socketChannel.configureBlocking(false);
                    socketChannel.register(key.selector(), SelectionKey.OP_READ);

                    SocketModel socketModel = new SocketModel(socketChannel, Convertor.GetPort_Integer(socketAddress));
                    if (Convertor.GetPort_Integer(socketChannel.getLocalAddress().toString()) == Config.BROKER_PORT){
                        clientType = "Broker";
                        _brokerList.add(socketModel);
                    }
                    if (Convertor.GetPort_Integer(socketChannel.getLocalAddress().toString()) == Config.MARKET_PORT){
                        clientType = "Market";
                        _marketList.add(socketModel);
                    }
                    System.out.println("Server Accepted: [" + socketChannel.getRemoteAddress() + " as " + clientType + "] on [" + socketChannel.getLocalAddress() + "]");
                }

                if (key.isReadable()){
                    String clientMessage = SocketTools.ProcessRead(key);
                    if (clientMessage != null && clientMessage.length() > 0) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        socketChannel.register(key.selector(), SelectionKey.OP_WRITE);

                        if (clientMessage.equalsIgnoreCase("exit")) {
                            key.cancel();
                            socketChannel.close();
                            continue;
                        }
                        if (this._port == Config.BROKER_PORT) {
                            for (SocketModel sm: _marketList) {
                                String[] fixed_msg = clientMessage.split("\\|");
                                if (fixed_msg != null && fixed_msg.length > 0  && fixed_msg[0].equals(sm.getIdString())) {
                                    store_msg_type = fixed_msg[1];
                                    String brokerPort = Convertor.GetPort_String(socketChannel.getRemoteAddress().toString());
                                    String marketPort = Convertor.GetPort_String(sm.getSocketChannel().getRemoteAddress().toString());

                                    _messageList.add(new MessageModel(brokerPort, marketPort));
                                    String message = Checksum.generate(clientMessage);
                                    System.out.println("Routing message from Broker Port[" + brokerPort + "] to Market Port[" + marketPort + "] : " + LogFixMessage.LogMessage(message));
                                    SocketTools.ProcessWrite(sm.getSocketChannel(), clientMessage);
                                }
                            }
                        }
                        if (this._port == Config.MARKET_PORT) {
                            for (MessageModel mm: _messageList) {
                                if (mm.getTo().equals(Convertor.GetPort_String(socketChannel.getRemoteAddress().toString()))) {
                                    mm.setMessage(clientMessage);
                                }
                            }
                        }
                    }
                }
                if (key.isWritable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    String response = "Market does not exist in the routing table. Please try a valid ID.";
                    boolean check = false;

                    if (this._port == Config.BROKER_PORT) {
                        List<MessageModel> messagesToRemove = new ArrayList<MessageModel>();

                        for (MessageModel mm: _messageList){
                            if (mm.getFrom().equals(Convertor.GetPort_String(socketChannel.getRemoteAddress().toString())) && mm.getMessage() != null) {
                                messagesToRemove.add(mm);
                                response = mm.getMessage();
                                check = true;
                            } else {
                                response = null;
                            }
                        }
                        for (MessageModel mm: messagesToRemove){
                            if (_messageList.contains(mm)) {
                                _messageList.remove(mm);
                            }
                        }
                    }
                    if (this._port == Config.MARKET_PORT) {}

                    if (check) {
                        String[] data = response.split("\\|");
                        //insert.insertInTransactions(socketChannel.getRemoteAddress().toString().split(":")[1], data[0].trim(), store_msg_type, data[2].trim(), data[1].trim());\
                    }

                    if (response != null) {
                        SocketTools.ProcessWrite(socketChannel, response);
                        socketChannel.register(key.selector(), SelectionKey.OP_READ);
                        socketChannel.finishConnect();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
