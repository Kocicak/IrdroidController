package mourovo.homeircontroller.irdroid;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;

import java.util.HashMap;

import mourovo.homeircontroller.activity.DebugActivity;
import mourovo.homeircontroller.app.Logger;
import mourovo.homeircontroller.irdroid.exception.InvalidResponseException;
import mourovo.homeircontroller.irdroid.exception.IrdroidException;

public class Manager {
    private final int VENDOR_ID = 1240; // irdroid
    private final int DEVICE_ID = 64776; // usd infrared transciever

    private Context context;
    private UsbManager usbManager;
    private UsbDevice device;
    private String permissionIntentName;

    private Connection connection;

    private Commander commander;

    private PendingIntent permissionIntent;

    public Manager(Context context) {
        this.context = context;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        permissionIntentName = context.getApplicationInfo().packageName + ".USB_PERMISSION";
    }


    public void checkAttachedDevice() {
        if(this.device == null) {
            log("no device attached.");
        } else {
            log("attached device: " + this.device.getProductName());
        }
    }

    public void registerReceivers() {
        permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(permissionIntentName), 0);
        IntentFilter filterPermission = new IntentFilter(permissionIntentName);
        context.registerReceiver(usbPermissionReceiver, filterPermission);

        IntentFilter connectFilter = new IntentFilter();
        connectFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        connectFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(usbDetachReceiver, connectFilter);

        Log.d("receivers","receivers registered");

    }

    public void unregisterReceivers() {
        context.unregisterReceiver(usbPermissionReceiver);
        context.unregisterReceiver(usbDetachReceiver);

        Log.d("receivers","receivers unregistered");
    }

    private UsbDevice detectDevice() {
        log("getting list of devices");
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            log("found device: " + device.getProductName() + " (" + device.getManufacturerName() + ")");
            // our device
            if (device.getVendorId() == VENDOR_ID && device.getProductId() == DEVICE_ID) {
                log("<-- this is our device!!");
                return device;
            }
        }

        log("there is no irdoid usb ir transciever.");
        return null;
    }

    private void attachDevice(UsbDevice device) {
        if (device != null) {
            log("Has permission? " + Boolean.toString(usbManager.hasPermission(device)));
            if(usbManager.hasPermission(device)) {
                setDevice(device);
            }else {
                requestPermission(device);
            }
        }
    }

    private void setDevice(UsbDevice device) {
        this.device = device;
        log("device" + device.getProductName() + " has been set.");
        log("device class: " + device.getDeviceClass());
    }

    private void removeDevice() {
        log("device " + this.device.getProductName() + " has been removed.");
        this.device = null;
    }

    public void detectAndAttachDevice() {
        UsbDevice device = detectDevice();
        attachDevice(device);
    }

    public void log(String text) {

    }

    private void requestPermission(UsbDevice device) {
        usbManager.requestPermission(device, permissionIntent);
    }

    public void transmitData(byte[] data) {

    }

    private final BroadcastReceiver usbPermissionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (permissionIntentName.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            log("permission granted for device " + device.getProductName());
                            setDevice(device);
                        }
                    } else {
                        log("permission denied for device " + device.getProductName());
                    }

                }
            }
        }
    };

    private final BroadcastReceiver usbDetachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            synchronized (this) {
                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    log("some device detached");
                    if (device != null) {
                        log("detached device " + device.getProductName());
                        if(device.equals(Manager.this.device)) {
                            log("that is our device!");
                            removeDevice();
                        }
                    }
                } else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    log("some device attached");
                    if(device != null) {
                        log("attached device " + device.getProductName());
                        attachDevice(device);
                    }
                }
            }
        }
    };

    public Connection getConnection() {
        if(this.connection == null) {
            try {
                this.connection = new Connection(this.usbManager, this.device);
                this.commander = new Commander(this.connection);
                this.commander.enterSamplingMode();
            }catch (IrdroidException e) {
                e.printStackTrace();
                Logger.d("Connection failed");
                this.connection.close();
            }
        }
        return this.connection;
    }

    public void closeConnection() {
        if(this.connection != null) {
            this.connection.close();
            this.connection = null;
        }
    }

    public void blink() {

    }
}
