package main.java.za.co.wethinkcode.amoodley.fixme.market.operations;

import main.java.za.co.wethinkcode.amoodley.fixme.market.model.InstrumentModel;
import main.java.za.co.wethinkcode.amoodley.fixme.market.utilities.Instrument;
import main.java.za.wethinkcode.amoodley.fixme.core.utilities.Checksum;

import java.util.List;

public class Operation {

    private String[] _response = {"EXECUTED", "REJECTED"};
    private String _msg_type;
    private String _instrumentName;
    private int _quantity;
    private int _price;
    private List<InstrumentModel> _instrumentList;

    public Operation(String msg_type, String instrumentName, int quantity, int price, List<InstrumentModel> instrumentList) {
        this._instrumentList = instrumentList;
        this._msg_type = msg_type;
        this._instrumentName = instrumentName;
        this._quantity = quantity;
        this._price = price;
    }

    public String setType(String message) {
        message = Checksum.generate(message);
        String[] arr = message.split("\\|");
        String response = null;

        if (this._msg_type.equalsIgnoreCase("Buy")){
            response = this.Buy();
        } else if (this._msg_type.equalsIgnoreCase("Sell")) {
            response = this.Sell();
        }
        Instrument.print(this._instrumentList);
        response = arr[0] + "|" + response + "|" + arr[2];
        return response;
    }

    private String Buy(){
        boolean transactionSuccess = false;
        for (InstrumentModel im: this._instrumentList) {
            int final_price = im.get_price() * this._quantity;
            if (im.get_Name().equalsIgnoreCase(this._instrumentName) && final_price == this._price){
                im.set_quantity(im.get_quantity() - this._quantity);
                transactionSuccess = true;
                if (im.get_quantity() <= 0) {
                    this._instrumentList.remove(im);
                }
            }
        }
        if (!transactionSuccess) {
            return (_response[1]);
        }
        return (_response[0]);
    }

    private String Sell() {
        boolean transactionSuccess = false;
        for (InstrumentModel im: this._instrumentList) {
            int final_price = im.get_price() * this._quantity;
            if (im.get_Name().equalsIgnoreCase(this._instrumentName) && final_price >= this._price) {
                im.set_quantity(im.get_quantity() + this._quantity);
                transactionSuccess = true;
            }
        }
        if (!transactionSuccess) {
            return (_response[1]);
        }
        return (_response[0]);
    }
}























