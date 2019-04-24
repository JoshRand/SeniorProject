package com.example.surfacejosh.myapplication;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;

import android.widget.Switch;
import android.widget.TextView;

//import com.jjoe64.graphview.GraphView;

import static java.lang.Thread.sleep;


public class Maf_Final extends AppCompatActivity {
    private Button SpeedDown;
    private Button SpeedStop;
    private TextView MAF_HR;
    private TextView Actual_hr;
    private TextView StepCountView;
    private TextView Calories;
    private Button Speedup;
    private Button Startup;
    //boolean displayhr = false;
    private ImageView CIRCLE;
    private Switch MAF_Switch;
    private TextView WORKOUT_START;
    private TextView WORKOUT_MODE;
    //GraphView GRAPH;
    int TreadSpeedReading;
    int Stepcount;
    String speedvalue;
    int MAFHR;
    int age = 0;
    int mafhr2;
    int stateofwarmup =0;
    private Handler mainHandler = new Handler();
    int weight = 0;
    int A_Hr = 60;
    int calint = 0;
    float caloriesburned = 0.0f;
    //  TREADMILL  speedup/slowdown/ and stop or ( no speed change )
    String startup ="1";
    String spdup = "2";
    String spdown = "5";
    String spdstop = "6";
    ////////////////////////
    Bundle extras;
    String MAFLOG = "";
    String MAF_HR_AND_LOG;
    String MAF_HR_STRING;
    double DUR_FROM_HR;
    int minutesdisplay =0;
    int hoursdisplay =0;
    int HourDis= 0;
    int MinuteDis= 0;
    int seconds = 0;
    int secondtwentieth = 0;
    int SecondDis = 0;
    int Mafworkoutstate = 0;
    int MafEndSecs1 = 50; // 300 seconds   Change to 10,10,10 to test each phase of workout
    int MafEndSecs2 = 140; //600
    int MafEndSecs3 = 50; //300
    //int hrdur;
    BluetoothTestService bts;


    private final ServiceConnection mServiceConnection = new ServiceConnection() {


        /**
         * This is called when the BluetoothTestService is connected
         *
         * @param componentName the component name of the service that has been connected
         * @param service service being bound
         */

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            bts = ((BluetoothTestService.LocalBinder) service).getService();

            bts.initialize();

        }


        /**
         * This is called when the BluetoothTestService is disconnected.
         *
         * @param componentName the component name of the service that has been connected
         */

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            bts = null;

        }
    };


    public void CHANGE_HEART_SIZE() {//double DUR){
        DUR_FROM_HR = (1 / (((double) A_Hr / 60))) * 1000;
        //double dur = DUR;
        CIRCLE = (ImageView) findViewById(R.id.HR_CIRCLE);

        CIRCLE.getLayoutParams().height = 400;

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                CIRCLE,
                PropertyValuesHolder.ofFloat("scaleX", 1.3f),
                PropertyValuesHolder.ofFloat("scaleY", 1.3f));
        scaleDown.setDuration((long) DUR_FROM_HR);//dur);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        scaleDown.start();

        CIRCLE.requestLayout();

    }

    public void doBindService() {
        Intent gattServiceIntent = new Intent(this, BluetoothTestService.class);

        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


    }



    public void DISPLAY_MAF_HR(int hr) {

        MAF_HR_STRING = Integer.toString(hr);
        MAF_HR_AND_LOG = MAFLOG + MAF_HR_STRING;
        MAF_HR = (TextView) findViewById(R.id.MAF_HR_TV);
        MAF_HR.setText(MAF_HR_AND_LOG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maf__final);
        doBindService();
        extras = getIntent().getExtras();
        Actual_hr = (TextView) findViewById(R.id.HR);
        StepCountView = (TextView) findViewById(R.id.StepCount);
        Speedup = (Button) findViewById(R.id.speedup);
        SpeedDown = (Button) findViewById(R.id.speeddown);
        SpeedStop = (Button) findViewById(R.id.speedstop);
        Calories = (TextView) findViewById(R.id.CALORIESBURNED);
        Startup = (Button) findViewById(R.id.Startup);
       // GRAPH = (GraphView) findViewById(R.id.graph);
        /*LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });*/
        //GRAPH.addSeries(series);
        if (extras != null) {
            MAFHR = extras.getInt("MafHeartRate");
            mafhr2 = MAFHR;
            weight = extras.getInt("Weight");
            //weight = Integer.parseInt(WeightI);
            age = extras.getInt("Age");

        }
        //weight = Integer.parseInt(WeightI);
        final IntentFilter filter = new IntentFilter();
        filter.addAction(bts.ACTION_DATA_RECEIVED_FIT_TRACKER);
        Intent gattServiceIntent = new Intent(this, BluetoothTestService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mBleUpdateReceiver, filter);
        DISPLAY_MAF_HR(MAFHR);
        CHANGE_HEART_SIZE();//DUR_FROM_HR);
        WORKOUT_START = (TextView) findViewById(R.id.WORKOUT_START);
        WORKOUT_MODE = (TextView) findViewById(R.id.WORKOUT_MODE);
        MAF_Switch = (Switch) findViewById(R.id.MAF_Switch);
        Speedup.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(final View v) {
                                           bts.writeSpeedCharacteristic(spdup);
                                          WORKOUT_START.setText(spdup);
                                          bts.readStepCharacteristic();
                                       }
                                   });
        SpeedDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                bts.writeSpeedCharacteristic(spdown);
                WORKOUT_START.setText(spdown);
            }
        });
        Startup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                bts.writeSpeedCharacteristic("1");
                //WORKOUT_START.setText(spdown);
            }
        });
        SpeedStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                bts.writeSpeedCharacteristic(spdstop);
                WORKOUT_START.setText(spdstop);
            }
        });

        MAF_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {

                if (on) {
                    final HandlerThread handlerThread = new HandlerThread("handlerThread");
                    //WORKOUT_START.setText("Starting Workout");
                    //TODO FIX HANDLER NOT QUITTING OUT OF LOOPS  ::BUG::

                    handlerThread.start();
                    Looper looper = handlerThread.getLooper();
                    final Handler handler = new Handler(looper);
                   // Handler MAF_HANDLE = new Handler(Looper.getMainLooper());
                    //WORKOUT_START.setText("EurickaA");

                    handler.post(new Runnable(){

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    WORKOUT_START.setText("Starting Maf Workout");
                                }
                            });

                            while(MAF_Switch.isChecked()){
                                try {
                                    if (seconds <= 1){
                                        mainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                WORKOUT_MODE.setText("WARM-UP");
                                            }
                                        });
                                     }
                                     ////// Increment seconds after every 50milliseconds for each mode
                                    sleep(50);
                                    secondtwentieth++;
                                    if(secondtwentieth%20 ==0){
                                        seconds++;
                                        SecondDis++;
                                        if(MinuteDis==60){
                                            HourDis++;
                                            MinuteDis=0;
                                        }
                                        if(SecondDis==60){
                                            MinuteDis++;
                                            SecondDis=0;
                                        }
                                        secondtwentieth = 0;
                                    }

                                }catch(Exception e){ }
                                if(seconds>= 0 && seconds<MafEndSecs1){
                                    try{

                                        if(seconds <=3) {
                                            bts.writeSpeedCharacteristic(startup);
                                        }

                                        /// if switch is off Stop everything set everything to 0 and send stop signal to treadmill
                                        if (!MAF_Switch.isChecked()) {

                                            minutesdisplay =0;
                                            hoursdisplay =0;
                                            HourDis= 0;
                                            MinuteDis= 0;
                                            seconds = 0;
                                            secondtwentieth = 0;
                                            SecondDis = 0;


                                                mainHandler.post(new Runnable() {

                                                     @Override
                                                     public void run() {
                                                         WORKOUT_START.setText("Stopping Treadmill");
                                                     }
                                                 });

                                                while(!MAF_Switch.isChecked()){
                                                    // Stop Treadmill signal send
                                                    bts.writeSpeedCharacteristic(spdstop);
                                                    Thread.currentThread().interrupt();
                                                    sleep(1000);
                                                    break;
                                                }
                                                handlerThread.quitSafely();
                                            handlerThread.quit();
                                                continue;
                                        }
                                        ///Set HrIgnore flag
                                        boolean hrignore = false;
                                        String hrstatus = Actual_hr.getText().toString();
                                        if(hrstatus.compareToIgnoreCase("...") == 0){
                                            hrignore = true;
                                        }
                                        // Get Tread Speed reading
                                        if(secondtwentieth%20==0){
                                            bts.readSpeedCharacteristic();
                                            TreadSpeedReading =0;//= Integer.parseInt(bts.getSpeedReading());
                                        }
                                        //// Decide if Speed down speed up or keep the pace
                                        if(seconds%3 == 0 && !hrignore && TreadSpeedReading == 0){
                                            if(A_Hr <= 205 && A_Hr >= MAFHR) {
                                                bts.writeSpeedCharacteristic(spdown);
                                                mainHandler.post(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        WORKOUT_START.setText("P1: Slowing down at "+ HourDis +":"+MinuteDis+":"+SecondDis);
                                                    }
                                                });

                                            }
                                        } else if (seconds%1 == 0 && !hrignore && TreadSpeedReading == 0) {
                                            if(A_Hr >= 40 && A_Hr <= MAFHR){
                                                bts.writeSpeedCharacteristic(spdup);
                                                mainHandler.post(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        WORKOUT_START.setText("P1: Speeding up at "+ HourDis +":"+MinuteDis+":"+SecondDis);
                                                    }
                                                });

                                            }


                                        }  else {
                                            bts.writeSpeedCharacteristic("0");
                                            mainHandler.post(new Runnable() {

                                                @Override
                                                public void run() {
                                                    WORKOUT_START.setText("P1: Keep the Pace at "+ HourDis +":"+MinuteDis+":"+SecondDis);
                                                }
                                            });

                                        }
                                        ///Print calories TODO:: fix accuracy of calories displayed
                                        mainHandler.post(new Runnable() {

                                            @Override
                                            public void run() {
                                                Calories.setText(CalcCalories(seconds)+" Calories Burned");
                                            }
                                        });

                                    }catch(Exception e){ }
                                }
                                if(seconds>= MafEndSecs1&& seconds <=(MafEndSecs1+MafEndSecs2)){
                                    try{

                                        mainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                WORKOUT_MODE.setText("MID MAF");
                                            }
                                        });
                                        /// if switch is off Stop everything set everything to 0 and send stop signal to treadmill
                                        if (!MAF_Switch.isChecked()) {

                                            minutesdisplay =0;
                                            hoursdisplay =0;
                                            HourDis= 0;
                                            MinuteDis= 0;
                                            seconds = 0;
                                            secondtwentieth = 0;
                                            SecondDis = 0;
                                            mainHandler.post(new Runnable() {

                                                @Override
                                                public void run() {
                                                    WORKOUT_START.setText("Stopping Treadmill");
                                                }
                                            });
                                            handlerThread.quitSafely();
                                            handlerThread.quit();
                                            while(!MAF_Switch.isChecked()){
                                                // Stop Treadmill signal send
                                                bts.writeSpeedCharacteristic(spdstop);
                                                Thread.currentThread().interrupt();
                                                sleep(1000);
                                                break;
                                            }
                                            continue;
                                        }
                                        ///Set HrIgnore flag
                                        boolean hrignore = false;
                                        String hrstatus = Actual_hr.getText().toString();
                                        if(hrstatus.compareToIgnoreCase("...") == 0){
                                            hrignore = true;
                                        }
                                        // Get Tread Speed reading
                                        if(secondtwentieth%20==0){
                                            bts.readSpeedCharacteristic();
                                            TreadSpeedReading =0;//= Integer.parseInt(bts.getSpeedReading());
                                        }
                                        //// Decide if Speed down speed up or keep the pace
                                        if(seconds%3 == 0 && !hrignore && TreadSpeedReading == 0){
                                            if(A_Hr <= 205 && A_Hr >= MAFHR) {
                                                bts.writeSpeedCharacteristic(spdown);
                                                mainHandler.post(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        WORKOUT_START.setText("P2: Slowing down at "+ HourDis +":"+MinuteDis+":"+SecondDis);
                                                    }
                                                });

                                            }
                                        } else if (seconds%1 == 0 && !hrignore && TreadSpeedReading == 0) {
                                            if(A_Hr >= 40 && A_Hr <= MAFHR){
                                                bts.writeSpeedCharacteristic(spdup);
                                                mainHandler.post(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        WORKOUT_START.setText("P2: Speeding up at "+ HourDis +":"+MinuteDis+":"+SecondDis);
                                                    }
                                                });

                                            }


                                        }  else {
                                            bts.writeSpeedCharacteristic("0");
                                            mainHandler.post(new Runnable() {

                                                @Override
                                                public void run() {
                                                    WORKOUT_START.setText("P2: Keep the Pace at " + HourDis +":"+MinuteDis+":"+SecondDis);
                                                }
                                            });

                                        }
                                        ///Print calories TODO:: fix accuracy of calories displayed
                                        mainHandler.post(new Runnable() {

                                            @Override
                                            public void run() {
                                                Calories.setText(CalcCalories(seconds)+" Calories Burned");
                                            }
                                        });


                                    }catch(Exception e){ }
                                }
                                if(seconds>(MafEndSecs1+MafEndSecs2)&& seconds <=(MafEndSecs1+MafEndSecs2+MafEndSecs3)){
                                    try{
                                        mainHandler.post(new Runnable() {

                                            @Override
                                            public void run() {
                                                WORKOUT_MODE.setText("WARM DOWN");
                                            }
                                        });

                                        /// if switch is off Stop everything set everything to 0 and send stop signal to treadmill
                                        if (!MAF_Switch.isChecked()) {

                                            mainHandler.post(new Runnable() {

                                                @Override
                                                public void run() {
                                                    WORKOUT_START.setText("Stopping Treadmill");
                                                }
                                            });

                                            minutesdisplay =0;
                                            hoursdisplay =0;
                                            HourDis= 0;
                                            MinuteDis= 0;
                                            seconds = 0;
                                            secondtwentieth = 0;
                                            SecondDis = 0;

                                            handlerThread.quitSafely();
                                            handlerThread.quit();
                                            while(!MAF_Switch.isChecked()){
                                                // Stop Treadmill signal send
                                                bts.writeSpeedCharacteristic(spdstop);
                                                Thread.currentThread().interrupt();
                                                sleep(1000);
                                                break;
                                            }
                                            continue;
                                        }
                                        // Get Tread Speed reading
                                        if(secondtwentieth%20==0){
                                        bts.readSpeedCharacteristic();
                                        TreadSpeedReading =0;//= Integer.parseInt(bts.getSpeedReading());
                                        }
                                        //// Decide if Speed down speed up or keep the pace
                                        if (seconds%3 == 0 && TreadSpeedReading == 0) {

                                            bts.writeSpeedCharacteristic(spdown);
                                            mainHandler.post(new Runnable() {

                                                @Override
                                                public void run() {
                                                    WORKOUT_START.setText("P3: Slowing down "+ HourDis +":"+MinuteDis+":"+SecondDis);
                                                }
                                            });


                                        }
                                        ///Print calories TODO:: fix accuracy of calories displayed
                                        if(seconds != MafEndSecs3+ MafEndSecs2 +MafEndSecs1) {
                                            mainHandler.post(new Runnable() {

                                                @Override
                                                public void run() {
                                                    Calories.setText(CalcCalories(seconds) + " Calories Burned");
                                                }
                                            });
                                        }
                                        //Stop workout End workout
                                        if (seconds == MafEndSecs3+ MafEndSecs2 +MafEndSecs1){
                                            //TODO:: End workout.
                                            minutesdisplay =0;
                                            hoursdisplay =0;
                                            HourDis= 0;
                                            MinuteDis= 0;
                                            seconds = 0;
                                            secondtwentieth = 0;
                                            SecondDis = 0;

                                            bts.writeSpeedCharacteristic(spdstop);

                                                sleep(250);
                                            mainHandler.post(new Runnable() {

                                                @Override
                                                public void run() {

                                                    WORKOUT_START.setText("stop");
                                                    WORKOUT_START.setText("END WORKOUT");
                                                    MAF_Switch.setChecked(false);
                                                }
                                            });

                                                    sleep(1000);
                                                }

                                    }catch(Exception e){ }

                                    }
                                if(!MAF_Switch.isChecked()){
                                    bts.writeSpeedCharacteristic(spdstop);
                                    continue;
                                }
                            }

                            }

                    });
                   // WORKOUT_START.setText("We out? ... Maybe");
                }
            }

        });

    }

    private int CalcCalories(int time){

        caloriesburned =  (0.0175f * 8.0f *(float)weight/2.2f)*(float)time/60.0f;
        calint = (int)caloriesburned;

      return calint;
    }


    private final BroadcastReceiver mBleUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {

                //case BluetoothTestService.ACTION_DISCONNECTED:
                    // Connecttracker.setEnabled(true);

                case BluetoothTestService.ACTION_DATA_RECEIVED_FIT_TRACKER:
                    // This is called after a notify or a read completes
                    //TODO: Change these actions states to Fit_Tracker and Treadmill Action broadcasts

                    //get heartrate
                    String hrvalue = bts.getCapSenseValue();
                    String stpvalue = bts.getStepValue();

                    Actual_hr.setText(hrvalue);
                    StepCountView.setText(stpvalue);
                    if(hrvalue.compareToIgnoreCase("...") == 0){

                    }else{
                        A_Hr = Integer.parseInt(hrvalue);
                    }


                default:
                    break;


            }
        }
    };
}
