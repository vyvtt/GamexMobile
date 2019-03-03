package com.gamex.network;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class CheckInternetTask extends AsyncTask<Void, Void, Boolean> {

    private Consumer mConsumer;
    public  interface Consumer { void accept(Boolean internet); }

    public  CheckInternetTask(Consumer consumer) { mConsumer = consumer; execute(); }

    @Override protected Boolean doInBackground(Void... voids) { try {
        Socket sock = new Socket();
        sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
        sock.close();
        return true;
    } catch (IOException e) { return false; } }

    @Override protected void onPostExecute(Boolean internet) { mConsumer.accept(internet); }

//    // you may separate this or combined to caller class.
//    public interface AsyncResponse {
//        void processFinish(boolean output);
//    }
//
//    public AsyncResponse delegate = null;
//
//    public CheckInternetTask(AsyncResponse delegate){
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected Boolean doInBackground(Void... voids) {
//        // Check internet
//        try {
//            Socket sock = new Socket();
//            sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
//            sock.close();
//            return true;
//        } catch (IOException e) {
//            return false;
//        }
//    }
//
//    @Override
//    protected void onPostExecute(Boolean result) {
//        delegate.processFinish(result);
//    }
}