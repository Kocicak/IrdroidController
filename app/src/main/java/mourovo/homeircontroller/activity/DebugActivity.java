package mourovo.homeircontroller.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Date;

import mourovo.homeircontroller.R;
import mourovo.homeircontroller.app.HomeIRApplication;
import mourovo.homeircontroller.app.Logger;


public class DebugActivity extends IRActivity {

    TextView tvLogger;
    ScrollView svScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        tvLogger = findViewById(R.id.textViewDebugLog);
        svScroller = findViewById(R.id.scrollViewDebugLog);

        log("Debug activity started");

        getIrManager().checkAttachedDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "destroooy");
    }

    public void onAttachButtonClick(View view) {
        ((HomeIRApplication)getApplication()).getIrManager().detectAndAttachDevice();
    }

    public void log(final String text) {

        tvLogger.post(new Runnable() {
            @Override
            public void run() {
                tvLogger.append("[" + DateFormat.format("HH:mm:ss", new Date()) + "] " + text + "\n");

                svScroller.post(new Runnable() {
                    @Override
                    public void run() {
                        svScroller.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });


    }

    public void onTransmitButtonClick(View view) {
        getIrManager().getCommander().sendLedOn();

        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                getIrManager().getCommander().sendLedOff();


                getIrManager().closeConnection();
            }
        };
        handler.postDelayed(r, 3000);
    }

    public void onRecordButtonClick(View view) {
        if(getIrManager().isConnectionActive()) {
            Logger.d("Connection is active, cannot record.");
            return;
        }
        getIrManager().startRecording();

    }

    public void onPlayButtonClick(View view) {
    }

    public void onStopButtonClick(View view) {
        getIrManager().stopAllActivity();
    }
}
