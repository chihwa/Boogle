package com.chihwakim.boogle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    public static Boogle boogle;
    public static List<String> words;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Random r = new Random();
    public int score;
    public int hi;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 800;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    setGrid();
                    this.score = 0;
                    this.mTextView.setText("");
                    this.mTextView.setHint(String.format("Score:%d, Hi:%d", this.score, this.hi));
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setGrid() {
        View gridView = findViewById(R.id.grid);
        char grid[][] = new char[4][4];

        setCell(grid, (EditText) gridView.findViewById(R.id.editText00), 0, 0);
        setCell(grid, (EditText) gridView.findViewById(R.id.editText01), 0, 1);
        setCell(grid, (EditText) gridView.findViewById(R.id.editText02), 0, 2);
        setCell(grid, (EditText) gridView.findViewById(R.id.editText03), 0, 3);

        setCell(grid, (EditText) gridView.findViewById(R.id.editText10), 1, 0);
        setCell(grid, (EditText) gridView.findViewById(R.id.editText11), 1, 1);
        setCell(grid, (EditText) gridView.findViewById(R.id.editText12), 1, 2);
        setCell(grid, (EditText) gridView.findViewById(R.id.editText13), 1, 3);

        setCell(grid, (EditText) gridView.findViewById(R.id.editText20), 2, 0);
        setCell(grid, (EditText) gridView.findViewById(R.id.editText21), 2, 1);
        setCell(grid, (EditText) gridView.findViewById(R.id.editText22), 2, 2);
        setCell(grid, (EditText) gridView.findViewById(R.id.editText23), 2, 3);

        setCell(grid, (EditText) gridView.findViewById(R.id.editText30), 3, 0);
        setCell(grid, (EditText) gridView.findViewById(R.id.editText31), 3, 1);
        setCell(grid, (EditText) gridView.findViewById(R.id.editText32), 3, 2);
        setCell(grid, (EditText) gridView.findViewById(R.id.editText33), 3, 3);
    }

    private char getRandomChar() {
        return (char) (this.r.nextInt(26) + 'A');
    }

    private void setCell(char[][] grid, EditText et, int x, int y) {
        char rc = getRandomChar();

        et.setGravity(Gravity.CENTER);

        et.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    char curChar = ((EditText) (view)).getText().toString().charAt(0);
                    TextView top = (TextView) findViewById(R.id.text);
                    top.append(String.valueOf(curChar));
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                return false;
            }
        });

        et.setText(String.valueOf(rc));
        grid[x][y] = rc;
        MainActivity.words = MainActivity.boogle.findWords(grid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        try {
            initBoogle();
        } catch (IOException e) {
            e.printStackTrace();
        }


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {


            public List<String> words;


            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);

                setGrid();

                mTextView.setOnTouchListener(new View.OnTouchListener() {

                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                String word = String.valueOf(((TextView) view).getText());
                                if (MainActivity.words.contains(word)) {
                                    MainActivity.words.remove(word);
                                    Toast.makeText(getApplicationContext(), String.format("Correct +%d", word.length()), Toast.LENGTH_SHORT).show();
                                    int curScore = score + word.length();
                                    MainActivity.this.score = curScore;
                                    int hiScore = MainActivity.this.hi;
                                    if (curScore > hiScore) {
                                        MainActivity.this.hi = curScore;
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show();
                                    TextView top = (TextView) findViewById(R.id.text);
                                    top.setText("");
                                    top.setHint(String.format("Score:%d, Hi:%d", MainActivity.this.score, MainActivity.this.hi));
                                }
                                break;
                        }
                        return false;
                    }
                });

            }
        });

    }

    private void initBoogle() throws IOException {
        Dictionary dict = new Dictionary(getApplicationContext(), R.raw.words);
        dict.buildDic();
        this.boogle = new Boogle(dict);
    }
}
