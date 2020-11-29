package com.example.cunguyenxuan.mqtt_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.*;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    MqttAndroidClient client;
    String stringJson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PB((CircularProgressBar) findViewById(R.id.progress_circular1),76f);
        PB((CircularProgressBar) findViewById(R.id.progress_circular2),30f);
//              Intent mainService=new Intent(MainActivity.this,MainService.class);
//        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
//        startForegroundService(mainService);
//        }
//        else{
//            startService(mainService);
//        }


        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883", clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("mqtt", "onSuccess");
                    Pub("Hello");
                    Sub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("mqtt", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    void Pub(String content){
        String topic = "IoT_BT1";
        String payload = content;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
    void Sub(){
        String topic = "IoT_BT2";
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("mqtt","subcribe success");
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("mqtt", "onFailure: ");
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("mqtt", message.toString());
                 stringJson=message.toString();
                JSONObject objectJson = new JSONObject(stringJson);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    void PB(CircularProgressBar pb, float value){

        CircularProgressBar circularProgressBar = pb;
// Set Progress Max
        circularProgressBar.setProgressMax(100f);
// Set Progress
        //     circularProgressBar.setProgress(75f);
// or with animation
        circularProgressBar.setProgressWithAnimation(value, (long)1000);// =1s
// Set ProgressBar Color
        circularProgressBar.setProgressBarColor(Color.BLACK);
// or with gradient
        circularProgressBar.setProgressBarColorStart(Color.WHITE);
        if(circularProgressBar==(CircularProgressBar) findViewById(R.id.progress_circular1)){
        circularProgressBar.setProgressBarColorEnd(Color.RED);
        }
        else if(circularProgressBar==(CircularProgressBar) findViewById(R.id.progress_circular2)){
            circularProgressBar.setProgressBarColorEnd(Color.BLUE);
        }
        circularProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);

// Set background ProgressBar Color
        circularProgressBar.setBackgroundProgressBarColor(Color.LTGRAY);
// or with gradient
        // circularProgressBar.setBackgroundProgressBarColorStart(Color.WHITE);
        //  circularProgressBar.setBackgroundProgressBarColorEnd(Color.RED);
        // circularProgressBar.setBackgroundProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);

// Set Width
        circularProgressBar.setProgressBarWidth(15f); // in DP
        circularProgressBar.setBackgroundProgressBarWidth(15f); // in DP
    }
}