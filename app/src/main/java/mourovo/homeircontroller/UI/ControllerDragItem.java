package mourovo.homeircontroller.UI;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItem;

import mourovo.homeircontroller.R;

public class ControllerDragItem extends DragItem {

    private Context context;

    public ControllerDragItem(Context context, int layoutId) {
        super(context, layoutId);
        this.context = context;
    }

    @Override
    public void onBindDragView(View clickedView, View dragView) {
        CharSequence text = ((TextView)clickedView.findViewById(R.id.item_controller_tv_name)).getText();
        ((TextView)dragView.findViewById(R.id.item_controller_tv_name)).setText(text);
        dragView.findViewById(R.id.item_controller_layout).setBackgroundColor(ContextCompat.getColor( context, R.color.list_item_background));
    }
}
