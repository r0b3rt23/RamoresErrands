package com.example.ramoreserrands.adapters;

import android.os.AsyncTask;
import android.util.Log;

public class LongOperation extends AsyncTask<String, Void, String> {


    @Override
    protected String doInBackground(String... params) {
        try {

            String email = params[0];
            String display_item = params[1];
            String reciept = params[2];
            String name = params[3];
            String total = params[4];
            GMailSender sender = new GMailSender("errands.orders@gmail.com", "edgepoint123");
            sender.sendMail("Order Being Processed "+reciept,
                    "Dear "+name+",\n\nYour order is being processed. \n\n" +
                            "ITEMS ORDERED :\n\n" +display_item+"\n"+
                            "TOTAL PRICE : ₱"+total+"\n\n"+
                            "This is a system-generated email. \n" +
                            "Please wait for a call from our personnel to confirm your order.\n"+
                            "Thanks for shopping!",
                    "errands.orders@gmail.com","errands.orders@gmail.com,"+email)                   ;

        } catch (Exception e) {
            Log.e("error", e.getMessage(), e);
            return "Email Not Sent";
        }

        return "Email Sent";
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("LongOperation",result+"");
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(Void... values) {

    }
}
