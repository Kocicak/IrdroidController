package mourovo.homeircontroller.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import mourovo.homeircontroller.Button.DraggableButton;
import mourovo.homeircontroller.R;
import mourovo.homeircontroller.fragment.ControllerFragment;
import mourovo.homeircontroller.fragment.EditControllerFragment;

public class MainActivity extends IRActivity {

    DrawerLayout layout;

    int oldXvalue;
    int oldYvalue;

    int xPosition;
    int yPosition;

    int startx;
    int starty;

    int step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.drawer_layout);
        layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // TODO: pryc s tim
//        startDebugActivity();


        DraggableButton btn = findViewById(R.id.dragButton);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        step = (int)Math.ceil(60 * metrics.density);

        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent me) {

                Log.d("BTN", "Action: " + me.getAction());

                switch(me.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout lay = findViewById(R.id.dragGrid);
                        int[] sizes = new int[2];
                        lay.getLocationInWindow(sizes);
                        oldXvalue = v.getWidth();
                        oldYvalue = v.getHeight();
                        xPosition = sizes[0];
                        yPosition = sizes[1];
                        startx = (int)me.getX();
                        starty = (int)me.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(oldXvalue, oldYvalue);
                        int x = step*Math.round((me.getRawX() - startx - xPosition)/step);
                        int y = step*Math.round((me.getRawY() - starty - yPosition)/step);
                        params.setMargins(x, y,0,0);
                        v.setLayoutParams(params);
                        break;
                    case MotionEvent.ACTION_UP:
                        // TODO: kdyz potahnu, tak nechci!
                        v.performClick();
                        break;
                }

                return true;
            }
        });
    }

    protected void startDebugActivity() {
        Intent intent = new Intent(this, DebugActivity.class);
        startActivity(intent);
    }

    public void onDebugButtonClick(View view) {
        startDebugActivity();
    }

    public void onTVButtonClick(View view) {
        layout.openDrawer(Gravity.END);

        FragmentManager man = getFragmentManager();
        man.beginTransaction()
                .replace(R.id.frame_controller, new EditControllerFragment())
                .commit();

    }
}
