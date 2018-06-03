package mourovo.homeircontroller.persistence.task;

import android.os.AsyncTask;

import java.util.List;

import mourovo.homeircontroller.persistence.ControllerLoader;
import mourovo.homeircontroller.persistence.IRDatabase;
import mourovo.homeircontroller.persistence.entities.Controller;

public class LoadControllersTask extends AsyncTask<Void, Void, List<Controller>> {

    private ControllerLoader loader;
    private IRDatabase database;

    public LoadControllersTask(ControllerLoader loader, IRDatabase database) {
        this.loader = loader;
        this.database = database;
    }

    @Override
    protected List<Controller> doInBackground(Void... voids) {
        return database.controllerDao().getAll();
    }

    @Override
    protected void onPostExecute(List<Controller> controllers) {
        this.loader.controllersLoaded(controllers);
    }
}
