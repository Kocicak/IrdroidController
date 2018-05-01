package mourovo.homeircontroller.irdroid;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import mourovo.homeircontroller.app.Logger;
import mourovo.homeircontroller.irdroid.exception.ConnectionException;
import mourovo.homeircontroller.irdroid.exception.InvalidResponseException;

public class Connection {
    private UsbInterface usbInterface;
    private UsbEndpoint inEndpoint;
    private UsbEndpoint outEndpoint;
    private UsbDeviceConnection connection;

    private Commander commander;

    Connection(UsbManager manager, UsbDevice device) throws ConnectionException {
        this.connection =  manager.openDevice(device);

        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface intf = device.getInterface(i);
            if (intf.getInterfaceClass() == UsbConstants.USB_CLASS_CDC_DATA) {
                this.usbInterface = intf;
                Logger.d("found cdc interface: intf " + i);

                for (int j = 0; j < intf.getEndpointCount(); j++) {
                    UsbEndpoint ep = intf.getEndpoint(j);
                    if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                        inEndpoint = ep;
                        Logger.d("found IN endpoint: " + j);
                    } else {
                        Logger.d("found OUT endpoint: " + j);
                        outEndpoint = ep;
                    }
                }
            } else {
                this.connection.releaseInterface(intf);
            }
        }

        if (inEndpoint == null || this.usbInterface == null || outEndpoint == null) {
            throw new ConnectionException("No suitable endpoint found.");
        }
    }

    public void close() {
        if (this.connection != null) {
            if(this.usbInterface != null) {
                this.connection.releaseInterface(usbInterface);
            }
            this.connection.close();
            Logger.d("connection closed");
        }
    }

    public void write(byte[] data) {
        if (!connection.claimInterface(usbInterface, true)) {
            Logger.d("cannot claim interface.");
            return;
        }
        int result = connection.bulkTransfer(outEndpoint, data, data.length, 0);
        Logger.d("transferred (" + result + "): " + Manager.getHexString(data));
    }



    public byte[] read() {
        byte[] buf = new byte[255];

        int len = connection.bulkTransfer(inEndpoint, buf, buf.length, 100);
        if (len <= 0) {
            return null;
        }
        Logger.d("read(" + len + ")");

        byte[] ret = new byte[len];
        System.arraycopy(buf, 0, ret, 0, len);
        Logger.d(Manager.getHexString(ret));
        Logger.d(new String(ret));
        return ret;
    }

    public Commander getCommander() throws InvalidResponseException {
        if(this.commander == null) {
            this.commander = new Commander(this);
            this.commander.enterSamplingMode();
        }
        return commander;
    }
}
