package mourovo.homeircontroller.UI.Button;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;

public class DraggableImageButton extends ImageButton {

    public DraggableImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick() {
        Log.d("DRAGIMGBTN", "click");
        return super.performClick();
    }
}
