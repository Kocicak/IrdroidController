package mourovo.homeircontroller.UI;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.List;

import mourovo.homeircontroller.R;
import mourovo.homeircontroller.persistence.entities.Controller;

public class EditControllerListAdapter extends DragItemAdapter<Controller, EditControllerListAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;

    public EditControllerListAdapter(List<Controller> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        Controller dummyAddCtrl = new Controller();
        dummyAddCtrl.setName("Add new controller");
        dummyAddCtrl.setId(666);
        list.add(0, dummyAddCtrl);
        setItemList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Controller ctrl = mItemList.get(position);
        String text = ctrl.getName();
        holder.mText.setText(text);
        if (ctrl.getId() == 666) {
            holder.dragHandle.setVisibility(View.INVISIBLE);
        } else {
            holder.dragHandle.setVisibility(View.VISIBLE);
        }

        holder.itemView.setTag(ctrl);

    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).getId();
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mText;
        ImageView dragHandle;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mText = itemView.findViewById(R.id.item_controller_tv_name);
            dragHandle = itemView.findViewById(R.id.item_controller_drag_handle);
        }



        @Override
        public void onItemClicked(View view) {
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
