package mourovo.homeircontroller.persistence.task;

import android.os.AsyncTask;

import mourovo.homeircontroller.app.Logger;
import mourovo.homeircontroller.persistence.IRDatabase;
import mourovo.homeircontroller.persistence.entities.Controller;

public class SaveControllerTask extends AsyncTask<Controller, Void, Void> {

    private  IRDatabase database;

    public SaveControllerTask(IRDatabase database) {
        this.database = database;
    }

    @Override
    protected Void doInBackground(Controller... controllers) {
        this.database.controllerDao().insertController(controllers[0]);

        Logger.d("Controller " + controllers[0].getName() + " saved. ID: " + controllers[0].getId());
        return null;
    }
}
