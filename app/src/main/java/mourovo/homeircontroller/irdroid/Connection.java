package mourovo.homeircontroller.irdroid;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.io.ByteArrayInputStream;

public class Connection {
    private UsbInterface usbInterface;
    private UsbEndpoint inEndpoint;
    private UsbEndpoint outEndpoint;
    private UsbDeviceConnection connection;

    Manager manager;


    public Connection(Manager manager) {

        this.manager = manager;

        UsbDevice device = manager.getDevice();

        this.connection = manager.getUsbManager().openDevice(device);

        for(int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface intf = device.getInterface(i);
            if(intf.getInterfaceClass() == UsbConstants.USB_CLASS_CDC_DATA) {
                this.usbInterface = intf;
                manager.log("found cdc interface: intf " + i);

                for(int j = 0; j < intf.getEndpointCount(); j++) {
                    UsbEndpoint ep = intf.getEndpoint(j);
                    if(ep.getDirection() == UsbConstants.USB_DIR_IN) {
                        inEndpoint = ep;
                        manager.log("found IN endpoint: " + j);
                    } else {
                        manager.log("found OUT endpoint: " + j);
                        outEndpoint = ep;
                    }
                }
            } else {
                this.connection.releaseInterface(intf);
            }
        }

        if(inEndpoint == null || this.usbInterface == null || outEndpoint == null) {
            manager.log("No suitable endpoint found.");

            return;
        }

        write(new byte[] {0,0,0,0,0});
        write(new byte[] {'s'});
        read();
    }

    public void close()
    {
        if(this.connection != null) {
            this.connection.releaseInterface(usbInterface);
            this.connection.close();
            manager.log("connection closed");
        }
    }

    public void write (byte[] data) {
        if(!connection.claimInterface(usbInterface,true)) {
            manager.log("cannot claim interface.");
            return;
        }
        int result = connection.bulkTransfer(outEndpoint, data, data.length, 0);
        manager.log("transferred (" + result + "): " +  getHexString(data) );
    }

    private String getHexString(byte[] data) {
        String hex = "";
        for (int i = 0; i < data.length; i++) {
            hex = hex + String.format("0x%02X", (data[i])) + " ";
        }
        return hex;
    }

    public byte[] read() {
        byte[] buf = new byte[255];

        int len = connection.bulkTransfer(inEndpoint, buf, buf.length, 100);
        if(len == -1) {
            return null;
        }
        manager.log("read(" + len + ")");

        byte[] ret = new byte[len];
        System.arraycopy(buf,0,ret,0,len);
        manager.log(getHexString(ret));
        manager.log(new String(ret));
        return ret;
    }
}
