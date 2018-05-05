package mourovo.homeircontroller.irdroid;

import mourovo.homeircontroller.app.Logger;
import mourovo.homeircontroller.irdroid.exception.InvalidResponseException;
import mourovo.homeircontroller.irdroid.exception.TransmitException;

public class Commander {

    public static final byte[] RESET = {0,0,0,0,0};
    public static final byte[] SAMPLING_MODE = {'s'};
    public static final byte[] LED_ON = {0x12};
    public static final byte[] LED_OFF = {0x13};

    public static final byte[] TRANSMIT_BYTE_COUNT = {0x24};
    public static final byte[] TRANSMIT_NOTIFY_COMPLETE = {0x25};
    public static final byte[] TRANSMIT_HANDSHAKE_MODE = {0x26};
    public static final byte[] TRANSMIT = {0x03};

    public static final byte[] COMMAND_END = new byte[]{-1, -1}; // 2x 0xFF is end of command.
    public static final byte[] COMMAND_ERROR = new byte[]{-1, -1, -1, -1, -1, -1}; // 6x 0xFF is ERROR


    private Connection connection;

    public Commander(Connection connection) {
        this.connection = connection;
    }

    public void reset(){
        if(this.connection != null) {
            this.connection.write(RESET);
            Logger.d("sent reset");
        }
    }

    public void enterSamplingMode() throws InvalidResponseException {
        if(this.connection != null) {
            int retries = 0;
            while(true) {
                this.reset();
                this.connection.write(SAMPLING_MODE);
                byte[] response = new byte[3];
                int read = this.connection.read(response,response.length);

                if(read == 0 || !(new String(response).equals("S01"))) {
                    Logger.d("IRToy did not return 'S01' response, cannot enter sampling mode");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(retries++ > 5) {
                        throw new InvalidResponseException("Something wrong, cannot reset device");
                    }
                    continue;
                }
                break;
            }
        }
    }

    public void sendLedOn() {
        connection.write(LED_ON);
    }


    public void sendLedOff() {
        connection.write(LED_OFF);
    }

    public void initTransmission() throws InvalidResponseException {
        if(this.connection != null) {
            this.connection.write(TRANSMIT_BYTE_COUNT);
            this.connection.write(TRANSMIT_NOTIFY_COMPLETE);
            this.connection.write(TRANSMIT_HANDSHAKE_MODE);
        }
    }

    public int readHandshake() throws TransmitException {
        byte[] bytesToSend = new byte[1];
        int res = this.connection.read(bytesToSend, bytesToSend.length);
        Logger.d("handshake response: "+res + ": " + Manager.getHexString(bytesToSend));
        if(res == 0) {
            throw new TransmitException("Wrong or no handshake response");
        }
        return bytesToSend[0];
    }

    public int readTransmitByteCount() throws TransmitException {
        byte[] bytes = new byte[3];
        int res = this.connection.read(bytes, bytes.length);

        if(res == 0 || bytes[0] != 't') {
            throw new TransmitException("ReadTransmitByteCount response is wrong");
        }
        return ((bytes[1] & 0xff) << 8) | (bytes[2] & 0xff);
    }

    public boolean readTransmitSuccess() throws TransmitException {
        byte[] bytes = new byte[1];
        int res = this.connection.read(bytes, bytes.length);
        if(res == 0) {
            throw new TransmitException("Wrong transmit success response");
        }

        if(bytes[0] == 'F') {
            throw new TransmitException("Buffer underrun during transmit");
        }
        return bytes[0] == 'C';
    }

}
