package mourovo.homeircontroller.irdroid;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.HashMap;

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

    private PendingIntent permissionIntent;

    private byte[] recordedCommand;

    public Manager(Context context) {
        this.context = context;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        permissionIntentName = context.getApplicationInfo().packageName + ".USB_PERMISSION";
    }


    public void checkAttachedDevice() {
        if(this.device == null) {
            Logger.d("no device attached.");
        } else {
            Logger.d("attached device: " + this.device.getProductName());
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

        Logger.d("receivers registered");

    }

    public void unregisterReceivers() {
        context.unregisterReceiver(usbPermissionReceiver);
        context.unregisterReceiver(usbDetachReceiver);

        Logger.d("receivers unregistered");
    }

    private UsbDevice detectDevice() {
        Logger.d("getting list of devices");
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            Logger.d("found device: " + device.getProductName() + " (" + device.getManufacturerName() + ")");
            // our device
            if (device.getVendorId() == VENDOR_ID && device.getProductId() == DEVICE_ID) {
                Logger.d("<-- this is our device!!");
                return device;
            }
        }

        Logger.d("there is no irdoid usb ir transciever.");
        return null;
    }

    private void attachDevice(UsbDevice device) {
        if (device != null) {
            Logger.d("Has permission? " + Boolean.toString(usbManager.hasPermission(device)));
            if(usbManager.hasPermission(device)) {
                setDevice(device);
            }else {
                requestPermission(device);
            }
        }
    }

    private void setDevice(UsbDevice device) {
        this.device = device;
        Logger.d("device" + device.getProductName() + " has been set.");
        Logger.d("device class: " + device.getDeviceClass());
    }

    private void removeDevice() {
        Logger.d("device " + this.device.getProductName() + " has been removed.");
        this.device = null;
    }

    public void detectAndAttachDevice() {
        UsbDevice device = detectDevice();
        attachDevice(device);
    }

    private void requestPermission(UsbDevice device) {
        usbManager.requestPermission(device, permissionIntent);
    }

    private final BroadcastReceiver usbPermissionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (permissionIntentName.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Logger.d("permission granted for device " + device.getProductName());
                            setDevice(device);
                        }
                    } else {
                        Logger.d("permission denied for device " + device.getProductName());
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
                    Logger.d("some device detached");
                    if (device != null) {
                        Logger.d("detached device " + device.getProductName());
                        if(device.equals(Manager.this.device)) {
                            Logger.d("that is our device!");
                            removeDevice();
                        }
                    }
                } else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    Logger.d("some device attached");
                    if(device != null) {
                        Logger.d("attached device " + device.getProductName());
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
            }catch (IrdroidException e) {
                e.printStackTrace();
                Logger.d("Connection failed");
                this.connection.close();

                return null;
            }
        }

        return this.connection;
    }

    public Commander getCommander() {
        try {
            return getConnection().getCommander();
        }catch (IrdroidException e) {
            e.printStackTrace();
            Logger.d(e.getMessage());
        }

        return null;
    }

    public void closeConnection() {
        if(this.connection != null) {
            this.connection.close();
            this.connection = null;
        }
    }

    public boolean isConnectionActive() {
        return this.connection != null;
    }

    public void startRecording() {
        RecordTask task = new RecordTask(this);
        task.execute();

        Logger.d("Started recording.");
    }

    public void setRecordedCommand(byte[] recordedCommand) {
        this.recordedCommand = recordedCommand;
        if(this.connection != null) {
            this.closeConnection();
        }

        if(recordedCommand == null) {
            Logger.d("No command recorded.");
            return;
        }

        Logger.d("Command recorded:");
        Logger.d(getHexString(this.recordedCommand));
    }


    public static String getHexString(byte[] data) {
        String hex = "";
        for (byte bajt : data) {
            hex = hex + String.format("0x%02X", (bajt)) + " ";
        }
        return hex;
    }

    public void stopAllActivity(){
        if(this.connection != null) {
            try {
                this.connection.getCommander().reset();
            } catch (InvalidResponseException e) {
                e.printStackTrace();
                Logger.d("Cannot send reset");
            }
            this.closeConnection();

            Logger.d("Stopped all activity");
        }

    }
}
