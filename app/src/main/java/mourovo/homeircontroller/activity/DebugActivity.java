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
import mourovo.homeircontroller.irdroid.Commander;
import mourovo.homeircontroller.irdroid.exception.IrdroidException;


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
        ((HomeIRApplication) getApplication()).getIrManager().detectAndAttachDevice();
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

    public void onBlinkButtonClick(View view) {
        Commander commander;

        try {
            commander = getIrManager().getConnection(true).getCommander(true);
            commander.enterSamplingMode();
        } catch (IrdroidException e) {
            Logger.d("Cannot blink, not connected: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        commander.sendLedOn();

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                getIrManager().getConnection().getCommander().sendLedOff();
                getIrManager().closeConnection();
            }
        };
        handler.postDelayed(r, 3000);
    }

    public void onRecordButtonClick(View view) {
        getIrManager().startRecording();

    }

    public void onPlayButtonClick(View view) {
        getIrManager().transmitRecordedCommand();
    }

    public void onStopButtonClick(View view) {
        getIrManager().stopAllActivity();
    }

    public void onClearButtonClick(View view) {
        tvLogger.setText("");
    }

    public void onReadButtonClick(View view) {
        byte[] buf = new byte[8];
        getIrManager().getConnection(true).read(buf,buf.length);
        getIrManager().closeConnection();

    }

    public void onWriteButtonClick(View view) {
        getIrManager().getConnection(true).write(Commander.COMMAND_END);
        getIrManager().closeConnection();
    }

    public void onResetButtonClick(View view) {
        getIrManager().getConnection(true).getCommander(true).enterSamplingMode();
        getIrManager().closeConnection();
    }
}
