package mourovo.homeircontroller.irdroid;

import android.util.Log;

import mourovo.homeircontroller.irdroid.exception.InvalidResponseException;

public class Commander {

    public static final byte[] RESET = {0,0,0,0,0};
    public static final byte[] SAMPLING_MODE = {'s'};
    public static final byte[] LED_ON = {0x12};
    public static final byte[] LED_OFF = {0x13};


    private Connection connection;

    public Commander(Connection connection) {
        this.connection = connection;
    }

    public void enterSamplingMode() throws InvalidResponseException {
        if(this.connection != null) {
            this.connection.write(RESET); // reset
            this.connection.write(SAMPLING_MODE); // enter sampling mode
            byte[] response = this.connection.read();

            if(response.length != 3 || !(new String(response).equals("S01"))) {
                throw new InvalidResponseException("IRToy did not return 'S01' response, cannot enter sampling mode");
            }
        }
    }

    public void sendLedOn() {
        connection.write(LED_ON);
    }




}
