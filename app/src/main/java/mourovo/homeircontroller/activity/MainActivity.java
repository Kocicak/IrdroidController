package mourovo.homeircontroller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mourovo.homeircontroller.R;

public class MainActivity extends IRActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: pryc s tim
        startDebugActivity();
    }

    protected void startDebugActivity() {
        Intent intent = new Intent(this, DebugActivity.class);
        startActivity(intent);
    }

    public void onDebugButtonClick(View view) {
        startDebugActivity();
    }
}
