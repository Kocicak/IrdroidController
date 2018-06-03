package mourovo.homeircontroller.app;

import android.content.Context;
import android.util.Log;

import mourovo.homeircontroller.activity.DebugActivity;

public class Logger {

    // TODO: nejak poresit ten statickej context
    private static Context context;

    public static void setContext(Context context) {
        Logger.context = context;
        d("changed context to " + context.getClass().toString());
    }


    public static void d(String message) {
        if (context instanceof DebugActivity) {
            ((DebugActivity) context).log(message);
        } else {
            Log.d("IRDroidManager", message);
        }
    }

}
