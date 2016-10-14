package com.example.doge.smartgym3;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.neovisionaries.ws.client.WebSocket;
//import com.neovisionaries.ws.client.WebSocketAdapter;
//import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class WorkoutGuideActivity extends AppCompatActivity {

    private Button cancel;
    private Socket mSocket;
    private LinearLayout mLayout;
    private TextView repsLeft;
    private TextView instruction;
    private int reps;
    private boolean isConnected;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_guide);
        WorkoutApplication app = (WorkoutApplication) getApplication();
        mSocket = app.getSocket();
        Bundle b = getIntent().getExtras();
        if (b != null){
            reps = b.getInt("repcount");
        }
        setResources();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
//        mSocket.off("user joined", onUserJoined);
//        mSocket.off("user left", onUserLeft);
//        mSocket.off("typing", onTyping);
//        mSocket.off("stop typing", onStopTyping);
    }

    private void setResources(){
        mUsername = "Blinky Bill";
        mLayout = (LinearLayout) findViewById(R.id.activity_workout_guide);
        repsLeft = (TextView) findViewById(R.id.reps_left);
        repsLeft.setText(String.valueOf(reps) + " left");
        instruction = (TextView) findViewById(R.id.instruction);
        cancel = (Button) findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSocket();
    }

    private void changeBackgoundColor(int condition){
        switch(condition){
            case 0:
                mLayout.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            case 1:
                mLayout.setBackgroundColor(getResources().getColor(R.color.orange));
                break;
            case 2:
                mLayout.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            default:
                mLayout.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }

    private void decrementRepCount(){
        reps = reps - 1;
        repsLeft.setText(String.valueOf(reps) + " left");
    }

    private void changeInstruction(int condition){
        switch(condition){
            case 0:
                instruction.setText(R.string.doing_good);
                break;
            case 1:
                instruction.setText(R.string.tilting);
                break;
            case 2:
                instruction.setText(R.string.abort);
                break;
            default:
                instruction.setText(R.string.doing_good);
        }
    }

    private void setSocket() {
        WorkoutApplication app = (WorkoutApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
//        mSocket.on("user joined", onUserJoined);
//        mSocket.on("user left", onUserLeft);
//        mSocket.on("typing", onTyping);
//        mSocket.on("stop typing", onStopTyping);
        mSocket.connect();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        if(mUsername != null) {
                            mSocket.emit("add user", mUsername);
                        }
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e){
                        return;
                    }
                    if (message.contentEquals("background")){
                        Random r = new Random();
                        int i1 = r.nextInt(3 - 0) ;
                        changeBackgoundColor(i1);
                        changeInstruction(i1);
                    } else if (message.contentEquals("reps")){
                        decrementRepCount();
                    }
                }
            });
        }
    };
}
