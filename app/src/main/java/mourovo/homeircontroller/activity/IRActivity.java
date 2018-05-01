package mourovo.homeircontroller.activity;

import android.app.Activity;
import android.os.Bundle;

import mourovo.homeircontroller.app.HomeIRApplication;
import mourovo.homeircontroller.app.Logger;
import mourovo.homeircontroller.irdroid.Manager;

public abstract class IRActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Logger.setContext(this);
    }


    public Manager getIrManager() {
        return ((HomeIRApplication) getApplication()).getIrManager();
    }
}
