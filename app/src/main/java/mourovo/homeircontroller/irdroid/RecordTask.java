package mourovo.homeircontroller.irdroid;

import android.os.AsyncTask;

import java.util.Arrays;

import mourovo.homeircontroller.app.Logger;

public class RecordTask extends AsyncTask<Void, Integer, byte[]> {

    private final byte[] FINISHED = new byte[] {-1, -1}; // 2x 0xFF is end of command.
    private final byte[] ERROR = new byte[] {-1, -1, -1, -1, -1, -1}; // 6x 0xFF is ERROR

    Manager manager;

    public RecordTask(Manager manager) {
        this.manager = manager;
    }

    @Override
    protected void onPreExecute() {
        if(manager.getCommander() == null) {
            this.cancel(true);
        }
    }

    @Override
    protected byte[] doInBackground(Void... voids) {

        byte[] buffer = new byte[4096];
        int len = 0;

        while(true) {
            if(!manager.isConnectionActive()) {
                Logger.d("Connection disappeared.");
                this.cancel(true);
                return null;
            }

            byte[] received = manager.getConnection().read();

            if(received == null || received.length == 0) {
                continue;
            }

            if(Arrays.equals(received, ERROR)) {
                return null;
            }

            System.arraycopy(received,0,buffer,len,received.length);
            len += received.length;

            if(Arrays.equals(received, FINISHED)) {
                break;
            }

            publishProgress(len);
        }

        byte[] command = new byte[len];
        System.arraycopy(buffer,0,command,0,len);
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
