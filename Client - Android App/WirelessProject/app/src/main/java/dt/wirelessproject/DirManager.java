package dt.wirelessproject;

import android.content.Context;
import android.os.FileObserver;
import android.os.Looper;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import com.loopj.android.http.*;


import cz.msebera.android.httpclient.Header;

/**
 * Created by dt on 10/31/15.
 */
public class DirManager extends FileObserver{
    private static SyncHttpClient client = new SyncHttpClient(80);
    private Socket socket;
    private ObjectOutputStream outputStream;
    private boolean isConnected = false;
    private String destinationPath = "./data/"; //change
    public String remoteHost = "http://ec2-54-68-125-126.us-west-2.compute.amazonaws.com";
    private String usrID;
    private String filedir;
    private Long sessionTime;

    public DirManager(String path) {
        super(path);
        filedir = path;
        sessionTime = System.currentTimeMillis();
    }

    private void postTest(String path){
        RequestParams params = new RequestParams();
        File file = new File(path);
        try{
            params.put("File", file);
            params.put("Session",sessionTime);
            params.put("userID", MainActivity.user);
            params.put("filename", file.getName());

        } catch (Exception e) {e.printStackTrace();}

        client.post(remoteHost, params,

                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        System.out.println("success, code: " + ((Integer)statusCode).toString());

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }

                }

        );
        new ActivityGetter(MainActivity.user,MainActivity.dirManager.remoteHost).execute();
    }

    @Override
    public void onEvent(int event, String path) {
        if(event == FileObserver.CLOSE_WRITE) {
            Log.i("dirManager", "event detected");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
            //connect(remoteHost);
            //sendFile(path);
            postTest(filedir + "/"+ path);
        }
    }
}
