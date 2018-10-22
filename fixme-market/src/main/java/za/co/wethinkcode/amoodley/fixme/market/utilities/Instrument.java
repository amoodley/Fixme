package main.java.za.co.wethinkcode.amoodley.fixme.market.utilities;

import main.java.za.co.wethinkcode.amoodley.fixme.market.model.InstrumentModel;
import org.omg.IOP.TAG_INTERNET_IOP;

import java.util.ArrayList;
import java.util.List;

public class Instrument {

    public static List<InstrumentModel> _instrumentList = new ArrayList<InstrumentModel>();
    private static InstrumentModel instrument;

    public static List<InstrumentModel> create() {
        String[] instrumentName = {"AMZN", "AAPL", "NFLX", "PYPL", "MSFT", "NVDA", "TSLA"};
        int[] price = {1790, 219, 348, 84, 109, 233, 257};
        int[] quantity = {2424, 14299, 5964, 17994, 12350, 4569, 4462};

        for (int cout = 0; cout  <= 5; cout++) {
            instrument = new InstrumentModel(instrumentName[cout], quantity[cout], price[cout]);
            _instrumentList.add(instrument);
        }
        return (_instrumentList);
    }

    public static void print(List<InstrumentModel> _instrumentList) {
        System.out.println("\nAVAILABLE INSTRUMENTS\n");
        for (InstrumentModel im: _instrumentList) {
            System.out.println("Id." + im.get_id() + " " + im.get_Name() + " Quantity:" + im.get_quantity() + " Price:" + im.get_price());
        }
    }
}

























