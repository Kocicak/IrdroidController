package mourovo.homeircontroller.app;

import android.app.Application;

import mourovo.homeircontroller.irdroid.Manager;

public class HomeIRApplication extends Application {
    private Manager irManager;

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
}
