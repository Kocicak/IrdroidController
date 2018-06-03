package mourovo.homeircontroller.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.woxthebox.draglistview.DragListView;

import java.util.List;

import mourovo.homeircontroller.R;
import mourovo.homeircontroller.UI.ControllerDragItem;
import mourovo.homeircontroller.UI.EditControllerListAdapter;
import mourovo.homeircontroller.persistence.ControllerLoader;
import mourovo.homeircontroller.persistence.entities.Controller;
import mourovo.homeircontroller.persistence.task.LoadControllersTask;

public class EditorActivity extends IRActivity implements ControllerLoader {

    DragListView controllerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        LoadControllersTask task = new LoadControllersTask(this, getDatabase());
        task.execute();

        controllerListView = findViewById(R.id.edit_draglist_controllers);

    }

    @Override
    public void controllersLoaded(List<Controller> controllers) {
        controllerListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        controllerListView.setLayoutManager(new LinearLayoutManager(this));

        EditControllerListAdapter adapter = new EditControllerListAdapter(controllers,R.layout.edit_controller_list_item, R.id.item_controller_drag_handle, true);
        controllerListView.setAdapter(adapter,true);
        controllerListView.setCanDragHorizontally(false);
        controllerListView.setCustomDragItem(new ControllerDragItem(this, R.layout.edit_controller_list_item));
        controllerListView.setCanNotDragAboveTopItem(true);

    }
}
