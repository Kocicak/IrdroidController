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
        this.connection = manager.openDevice(device);

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
            if (this.usbInterface != null) {
                this.connection.releaseInterface(usbInterface);
            }
            this.connection.close();
            Logger.d("connection closed");
        }
    }

    public int write(byte[] data) {
        if (!connection.claimInterface(usbInterface, true)) {
            Logger.d("cannot claim interface.");
            return 0;
        }
        int result = connection.bulkTransfer(outEndpoint, data, data.length, 100);
        Logger.d("transferred (" + result + " of " + data.length + "): " + Manager.getHexString(data));
        return result;
    }


    public int read(byte[] buf, int size) {

        int len = 0;
        int retries = 0;
        while(len < size) {
            int ret = connection.bulkTransfer(inEndpoint, buf, len, size-len, 100);
            if(ret <= 0) {
                retries++;
                if(retries == 10) {
                    break;
                }
                continue;
            }
            retries=0;
            len += ret;
        }

        if(len > 0) {
            Logger.d("read(" + len + ")");
            Logger.d(Manager.getHexString(buf));
        }
        return len;
    }

    public Commander getCommander() {
        return this.commander;
    }

    public Commander getCommander(boolean init) throws InvalidResponseException {
        if (this.commander == null && init) {
            this.commander = new Commander(this);
            this.commander.enterSamplingMode();
        }
        return getCommander();
    }
}
