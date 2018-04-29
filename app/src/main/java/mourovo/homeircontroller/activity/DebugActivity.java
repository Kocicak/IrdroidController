package mourovo.homeircontroller.activity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Date;

import mourovo.homeircontroller.R;
import mourovo.homeircontroller.app.HomeIRApplication;
import mourovo.homeircontroller.irdroid.Manager;


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

    public void log(String text) {
        tvLogger.append("[" + DateFormat.format("HH:mm:ss", new Date()) + "] " + text + "\n");
        svScroller.post(new Runnable() {
            @Override
            public void run() {
                svScroller.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public Manager getIrManager() {
        return ((HomeIRApplication) getApplication()).getIrManager();
    }

    public void onTransmitButtonClick(View view) {
        getIrManager().blink();
    }
}
