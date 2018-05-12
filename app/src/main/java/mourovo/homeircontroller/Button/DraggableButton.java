package mourovo.homeircontroller.Button;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;


public class DraggableButton extends Button {

    public DraggableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick() {
        Log.d("DRAGBTN", "click");
        return super.performClick();
    }
}
