package mourovo.homeircontroller.app;

import android.app.Application;
import android.arch.persistence.room.Room;

import mourovo.homeircontroller.irdroid.Manager;
import mourovo.homeircontroller.persistence.IRDatabase;

public class HomeIRApplication extends Application {
    private Manager irManager;

    private IRDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        this.irManager = new Manager(this);

        this.getIrManager().detectAndAttachDevice();

        this.irManager.registerReceivers();
    }

    public Manager getIrManager() {
        return irManager;
    }

    public IRDatabase getDb() {
        if(db == null) {
            db = Room.databaseBuilder(this,IRDatabase.class,"irapp").build();
        }
        return db;
    }
}
