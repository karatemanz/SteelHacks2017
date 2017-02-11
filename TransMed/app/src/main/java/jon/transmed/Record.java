package jon.transmed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

import android.util.Log;
import android.content.pm.PackageManager;
import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import org.json.JSONObject;


public class Record extends Activity
{

    private TextView mText, BPText, HRText, REText;
    private SpeechRecognizer sr;
    private static final String TAG = "Recognition";
    private String bloodPressure;
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            if(Math.abs(delta) > 20)
            {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
                sr.startListening(intent);
                Log.i("111111", "11111111");
            }
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);


        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");
            makeRequest();
        }

        mText = (TextView) findViewById(R.id.TextView1);
        BPText = (TextView) findViewById(R.id.TextBP);
        HRText = (TextView) findViewById(R.id.TextHR);
        REText = (TextView) findViewById(R.id.TextRE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());
    }

    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.d(TAG,  "error " +  error);
            //mText.setText("error " + error);
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                Log.d(TAG, "result " + data.get(i));
                parseSpeech((String)data.get(i));
                str += data.get(i);
            }
           // mText.setText("results: "+String.valueOf(data.size()));
        }

        private void parseSpeech(String data) {

            String[] fragment = data.split(" ");
            Log.i(TAG, "Parsing Speech");

            for(int i = 0; i < fragment.length; i++){

                if(fragment.length >= 5) {
                    if (fragment[i].contains("blood") && fragment[i + 1].contains("pressure") && fragment[i + 2].matches("\\d+") && (fragment[i + 3].contains("over") || fragment[i + 3].contains("/")) && fragment[i + 4].matches("\\d+")) {

                        bloodPressure = fragment[i + 2] + " / " + fragment[i + 4];
                        BPText.setText("Blood Pressure: " + bloodPressure);
                    }
                }
                if(fragment.length >= 4) {
                    if (fragment[i].contains("BP") && fragment[i + 1].matches("\\d+") && (fragment[i + 2].contains("over") || fragment[i + 2].contains("/")) && fragment[i + 3].matches("\\d+")) {

                        bloodPressure = fragment[i + 1] + " / " + fragment[i + 3];
                        BPText.setText("Blood Pressure: " + bloodPressure);
                    }
                }

                if(fragment.length >= 3) {
                    if(fragment[i].contains("heart") && fragment[i+1].contains("rate") && fragment[i+2].matches("\\d+")){
                        String heartRate = fragment[i+2];
                        HRText.setText("Heart Rate: " + heartRate);
                    }
                }

                if(fragment.length >= 2) {
                    if(fragment[i].contains("HR") && fragment[i+1].matches("\\d+")) {
                        String heartRate = fragment[i] + fragment[i+1];
                        HRText.setText("Heart Rate: " + heartRate);
                    }
                    else if(fragment[i].contains("respiration") && fragment[i+1].matches("\\d+")){
                        String respir = fragment[i+1];
                        REText.setText("Respiration: " + respir);
                    }
                }

            }


        }

        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                101);
    }
}
