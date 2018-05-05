package mourovo.homeircontroller.irdroid;

import android.os.AsyncTask;

import mourovo.homeircontroller.app.Logger;
import mourovo.homeircontroller.irdroid.exception.IrdroidException;
import mourovo.homeircontroller.irdroid.exception.TransmitException;

public class TransmitTask extends AsyncTask<byte[], Integer, Integer> {

    Manager manager;

    public TransmitTask(Manager manager) {
        this.manager = manager;
    }

    @Override
    protected Integer doInBackground(byte[]... bytes) {
        byte[] command = bytes[0];

        Connection connection;
        Commander commander;
        try {
            connection = manager.getConnection(true);
            commander = connection.getCommander(true);
            commander.initTransmission();
        }catch (IrdroidException e) {
            return null;
        }

        int transmitted = 0;
        connection.write(Commander.TRANSMIT);

        while(transmitted < command.length) {
            if(this.isCancelled()) {
                return null;
            }

            try {
                int buflen = commander.readHandshake();

                Logger.d("ready for " + buflen + " bytes");
                byte[] buf = new byte[buflen];
                int toSend =Math.min(buflen,command.length - transmitted);
                System.arraycopy(command,transmitted,buf,0, toSend);
                connection.write(buf);
                transmitted += toSend;

                publishProgress(transmitted);

                if(transmitted >= command.length) {
                    break;
                }

            } catch (TransmitException e) {
                Logger.d("Cannot send: " + e.getMessage());
                e.printStackTrace();
                int retries = 0;
                while(retries++ < 5) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    int written =connection.write(Commander.COMMAND_END);
                    if(written < Commander.COMMAND_END.length) {
                        continue;
                    }
                    break;
                }

                break;
                //return null;
            }
        }

        Logger.d("Whole data sent.");



        int retries = 0;
        while(retries++ < 5) {
            try {
                commander.readHandshake();
                break;
            } catch (TransmitException e) {
                Logger.d("Wrong or no handshake: " + e.getMessage());
                e.printStackTrace();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            }
        }

        int tx = 0;

        try {
            tx = commander.readTransmitByteCount();
        } catch (TransmitException e) {
            Logger.d("Wrong transmit byte response: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            if(commander.readTransmitSuccess()){
                Logger.d("Transmit was successful!");
            } else {
                Logger.d("transmit was not successful");
            }
        } catch (TransmitException e) {
            Logger.d(e.getMessage());
            e.printStackTrace();
        }

        return tx;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Logger.d("Transmitted " + values[0] + " bytes");
    }

    @Override
    protected void onPostExecute(Integer integer) {
        this.manager.transmitCommandComplete(integer);
    }

    @Override
    protected void onCancelled(Integer integer) {
        this.manager.transmitCommandComplete(integer);
    }
}
