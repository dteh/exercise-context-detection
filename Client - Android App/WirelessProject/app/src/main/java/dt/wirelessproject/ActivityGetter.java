package dt.wirelessproject;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by dt on 12/13/15.
 */
public class ActivityGetter extends AsyncTask<Void,Void,String>{
    String user;
    String url;
    HttpClient http;
    String buffer;
    ActivityGetter(String user, String url){
        this.user = user;
        this.url = url;
        this.http = new DefaultHttpClient();
        this.buffer = null;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Thread.sleep(1000);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url + ":80/api/user/" + user);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            buffer = EntityUtils.toString(httpEntity);
            Log.i("Response", buffer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    @Override
    protected void onPostExecute(String text){
        MainActivity.AT.setText(text);
    }

}

