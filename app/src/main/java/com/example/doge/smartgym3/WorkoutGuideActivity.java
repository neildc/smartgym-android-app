package com.example.doge.smartgym3;


import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.speech.tts.TextToSpeech;
//import com.neovisionaries.ws.client.WebSocket;
//import com.neovisionaries.ws.client.WebSocketAdapter;
//import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class WorkoutGuideActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private Button cancel;
    private Socket mSocket;
    private LinearLayout mLayout;
    private TextView repsLeft;
    private TextView smallText;
    private TextView instruction;
    private int reps;
    private int originalReps;
    private boolean isConnected;
   TextToSpeech textToSpeech;
    private String mUsername;
    private int currRep;
    int resttime;
    int currSet;
    int sets;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_guide);
        WorkoutApplication app = (WorkoutApplication) getApplication();
        mSocket = app.getSocket();

        Bundle b = getIntent().getExtras();
        if (b != null){
            reps = b.getInt("repcount");
            resttime = b.getInt("resttime");
            sets = b.getInt("sets");
            originalReps = reps;
            currRep = 0;
        }
        setResources();
        startWorkout();

        textToSpeech = new TextToSpeech(WorkoutGuideActivity.this, WorkoutGuideActivity.this);
        textToSpeech.setPitch((float)0.5);
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
        mSocket.off("rep", onRepCall);
//        mSocket.off("user left", onUserLeft);
//        mSocket.off("typing", onTyping);
//        mSocket.off("stop typing", onStopTyping);
    }

    private void setResources(){
        mUsername = "Blinky Bill";
        mLayout = (LinearLayout) findViewById(R.id.activity_workout_guide);
        repsLeft = (TextView) findViewById(R.id.reps_left);
        smallText = (TextView) findViewById(R.id.reps_count);
        repsLeft.setText(String.valueOf(reps) + " left");
        instruction = (TextView) findViewById(R.id.instruction);
        cancel = (Button) findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelWorkout();
                finish();
            }
        });
//        changeBackgroundColor(Constants.TILT);
//        changeInstruction(Constants.TILT, '');
        setSocket();
    }

    private void setSocket() {
        WorkoutApplication app = (WorkoutApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.on("rep", onRepCall);
//        mSocket.on("user left", onUserLeft);
//        mSocket.on("typing", onTyping);
//        mSocket.on("stop typing", onStopTyping);
        mSocket.connect();
    }

//    PRIVATE FUNCTIONS

    private void startWorkout(){
        JSONObject workout = new JSONObject();
        try {
            workout.put("exercise", "Bench Press");
            workout.put("weight", "60");
            workout.put("rest_time", "30");
            workout.put("sets", "3");
            workout.put("reps_per_set", "10");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("new workout", workout);
    }

    private void setStart(){
        mSocket.emit("set start");
    }

    private void setFinished(){
        mSocket.emit("set finished");
        changeBackgroundColor(Constants.REST);
        changeInstruction(Constants.REST, null);
        startCountdownTimer(resttime);
    }

    private void exerciseComplete() {

    }

    private void newSet() {
        if (currSet > sets) {
            exerciseComplete();
        }
        changeBackgroundColor(Constants.OKAY);
        currSet++;
        TextToSpeechFunction("Time to start set " + Integer.valueOf(currSet) +  " of " + Integer.valueOf(sets));
        reps = originalReps;
        setStart();
    }

    private void cancelWorkout(){
        mSocket.emit("cancel workout");
    }

    private void changeBackgroundColor(int condition){
        switch(condition){
            case Constants.OKAY:
                mLayout.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            case Constants.TILT:
                mLayout.setBackgroundColor(getResources().getColor(R.color.orange));
                break;
            case Constants.ABORT:
                mLayout.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            case Constants.REST:
                mLayout.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
                break;
            default:
                mLayout.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }

    private void decrementRepCount(){

        currRep++;
        reps = reps - 1;

        if (reps >= 3) {
            TextToSpeechFunction(Integer.toString(currRep));
        } else {
            if (reps == 2) {
                TextToSpeechFunction(Integer.toString(reps+1) + " left");
            } else {
                TextToSpeechFunction(Integer.toString(currRep));
            }
        }

        repsLeft.setText(String.valueOf(reps) + " left");
        if (reps == 0) {
            TextToSpeechFunction(
                    Integer.toString(currRep) + " reps completed. You done maaaate");
            setFinished();
        }
    }

    private void changeInstruction(int condition, String direction){
        switch(condition){
            case Constants.OKAY:
                instruction.setText(R.string.doing_good);
                smallText.setText("Rep count:");
                break;
            case Constants.TILT:
                if (direction.contentEquals("")){
                    instruction.setText(R.string.tilting);
                } else {
                    if (direction.contentEquals("left")){
                        instruction.setText(R.string.tilting + "LEFT!");
                    } else if (direction.contentEquals("right")){
                        instruction.setText(R.string.tilting + "RIGHT");
                    } else {
                        Log.wtf("WTF", "SOMETHING WENT WRONG IN CHANGE INSTRUCTION");
                    }
                }
                break;
            case Constants.ABORT:
                instruction.setText(R.string.abort);
                break;
            case Constants.REST:
                instruction.setText("Rest Time");
                smallText.setText("Time remaining:");
                break;
            default:
                instruction.setText(R.string.doing_good);
        }
    }


//    SOCKET EVENT LISTENERS

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        if(mUsername != null) {
                            mSocket.emit("add user", mUsername);
                            Log.wtf("WTF","IT WORKED");
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
        public void call(final Object... args) {runOnUiThread(new Runnable() {
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
                        changeBackgroundColor(i1);
                        changeInstruction(i1, "");
                    } else if (message.contentEquals("reps")){
                        decrementRepCount();
                    }
                }
            });
        }
    };

    @Override
    public void onInit(int Text2SpeechCurrentStatus) {

        if (Text2SpeechCurrentStatus == TextToSpeech.SUCCESS) {

            textToSpeech.setLanguage(Locale.US);

        }

    }

    public void TextToSpeechFunction(String s)
    {

        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);

    }

    public void startCountdownTimer(int seconds) {
        int milliseconds = seconds * 1000;
        new CountDownTimer(milliseconds, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                repsLeft.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                // 10 seconds remaining
                if (millisUntilFinished/1000 == 10) {
                    TextToSpeechFunction("10 seconds until the next set");
                }
            }

            public void onFinish() {
                newSet();
                repsLeft.setText("done!");
            }
        }.start();
    }

    private Emitter.Listener onRepCall = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    String condition;
                    String direction;
                    try {
                        message = data.getString("message");
                        condition = data.getString("condition");
                        direction = data.getString("direction");
                    } catch (JSONException e){
                        return;
                    }

                    if (message.contentEquals("rep")){
                        switch (condition) {
                            case "good":
                                changeInstruction(Constants.OKAY, "");
                                changeBackgroundColor(Constants.OKAY);
                                decrementRepCount();
                                break;
                            case "tilt":
                                changeInstruction(Constants.TILT, direction);
                                changeBackgroundColor(Constants.TILT);
                                break;
                            case "abort":
                                changeInstruction(Constants.ABORT, "");
                                changeBackgroundColor(Constants.ABORT);
                                break;
                            default:
                                return;
                        }
                    } else {
                        return;
                    }
                }
            });
        }
    };
}
