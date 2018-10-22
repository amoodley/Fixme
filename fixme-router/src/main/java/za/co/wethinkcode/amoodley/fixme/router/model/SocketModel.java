package main.java.za.co.wethinkcode.amoodley.fixme.router.model;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class SocketModel {

    private SocketChannel _socketChannel;
    private int _id;

    public SocketModel(SocketChannel sc, int id){
        this._socketChannel = sc;
        this._id = id;
    }

    public int getId(){
        return (this._id);
    }

    public String getIdString(){
        return (Integer.toString(this._id));
    }

    public SocketChannel getSocketChannel(){
        return (this._socketChannel);
    }
}
