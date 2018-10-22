package main.java.za.co.wethinkcode.amoodley.fixme.market;

import main.java.za.co.wethinkcode.amoodley.fixme.market.config.Config;
import main.java.za.co.wethinkcode.amoodley.fixme.market.model.InstrumentModel;
import main.java.za.co.wethinkcode.amoodley.fixme.market.operations.Operation;
import main.java.za.co.wethinkcode.amoodley.fixme.market.utilities.Instrument;
import main.java.za.wethinkcode.amoodley.fixme.core.utilities.SocketTools;

import javax.jws.soap.SOAPBinding;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MarketClient {

    private SocketChannel socketChannel;
    private Selector selector;
    private static String _response;
    private Operation operation;
    private static List<InstrumentModel> _instrumentList;

    public MarketClient() {
        try {
            this.init();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void init() throws Exception {
        _instrumentList = Instrument.create();
        InetAddress inetAddress = InetAddress.getByName(Config.SERVER_ADDRESS);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, Config.SERVER_PORT);
        selector = Selector.open();
        socketChannel = SocketChannel.open();

        socketChannel.configureBlocking(false);
        socketChannel.connect(inetSocketAddress);
        socketChannel.register(selector, (SelectionKey.OP_CONNECT | SelectionKey.OP_READ));

        while (true) {
            if (selector.selectNow() > 0) {
                boolean isDone = processKeys(selector.selectedKeys());
                if (isDone == true)
                    break ;
            }
        }
    }

    private boolean processKeys(Set<SelectionKey> readySet) throws Exception {
        Iterator<SelectionKey> iterator = readySet.iterator();

        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();

            if (key.isConnectable()) {
                boolean connected = SocketTools.ProcessConnection(key);
                Instrument.print(_instrumentList);
                if (connected == false) {
                    return (true);
                }
            }
            if (key.isReadable()) {
                String message = SocketTools.ProcessRead(key);
                String address = socketChannel.getLocalAddress().toString();
                String[] fixedMessage = message.split("\\|");

                if (fixedMessage != null && fixedMessage.length > 0 && fixedMessage[0].equals(address.split(":")[1])) {
                    System.out.println("[Broker] " + message);
                    operation = new Operation(fixedMessage[1], fixedMessage[2], Integer.parseInt(fixedMessage[3]), Integer.parseInt(fixedMessage[4]), _instrumentList);
                    _response = operation.setType(message);
                    socketChannel.register(selector,SelectionKey.OP_WRITE);
                }
            }
            if (key.isWritable()) {
                String response = _response;

                if (response != null && response.length() > 0) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.wrap(response.getBytes());
                    socketChannel.write(byteBuffer);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
            }
        }
        return (false);
    }
}


























