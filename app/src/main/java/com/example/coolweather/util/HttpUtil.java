package com.example.coolweather.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        Log.d("排错","进入sendHttpRequest，开始线程");
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    Log.e("排错","正在尝试连接"+address);
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    Log.e("排错","连接1");
                    InputStream in = connection.getInputStream();
                    Log.e("排错","连接2");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();

                    String line;
                    while((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    if (listener != null){
                        listener.onFinish(response.toString());
                    }
                    Log.e("排错","连接接受数据成功");
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("排错","连接出错");
                    if (listener != null){
                        listener.onError(e);
                    }
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
