package mourovo.homeircontroller.irdroid;

import android.os.AsyncTask;

import java.util.Arrays;

import mourovo.homeircontroller.app.Logger;
import mourovo.homeircontroller.irdroid.exception.IrdroidException;

public class RecordTask extends AsyncTask<Void, Integer, byte[]> {



    Manager manager;

    RecordTask(Manager manager) {
        this.manager = manager;
    }

    @Override
    protected byte[] doInBackground(Void... voids) {

        Connection connection;

        try {
            connection = manager.getConnection(true);
            connection.getCommander(true);
        } catch (IrdroidException e) {
            Logger.d("Can't record, can't init connection: " + e.getMessage());
            this.cancel(true);
            return null;
        }

        Logger.d("Started recording.");

        byte[] buffer = new byte[4096];
        int len = 0;

        while (true) {
            if (!manager.isConnectionActive()) {
                Logger.d("Connection disappeared.");
                this.cancel(true);
            }
            if(this.isCancelled()) {
                Logger.d("Recording cancelled.");
                return null;
            }

            byte[] received = new byte[255];
            int res = connection.read(received, received.length);

            if (res == 0) {
                continue;
            }

            System.arraycopy(received, 0, buffer, len, res);
            len += res;
            publishProgress(len);

            if(received[res-2] == -1 && received[res-1] == -1) {
                Logger.d("finished");
                break;
            }

            if (Arrays.equals(received, Commander.COMMAND_ERROR)) {
                return null;
            }
        }

        byte[] command = new byte[len];
        System.arraycopy(buffer, 0, command, 0, len);
        return command;

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Logger.d("Command updated, length is now " + values[0]);
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        manager.setRecordedCommand(bytes);
    }

    @Override
    protected void onCancelled(byte[] bytes) {
        manager.setRecordedCommand(null);
    }
}
