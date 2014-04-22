package com.example.bluetooth.le2;

import java.util.List;
import java.util.UUID;

import com.example.bluetooth.utils.Logger;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;



/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = "BluetoothLeService";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
//    private IBluetoothGatt mService;
    
    private BluetoothGattCharacteristic mGattPedometerCharacteristic_1;
    private BluetoothGattCharacteristic mGattPedometerCharacteristic_2;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static UUID UUID_GATT_MEASUREMENT =
        UUID.fromString(SampleGattAttributes.FMP_MEASUREMENT_CHARACTERISTIC);
    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HRP_MEASUREMENT_CHARACTERISTIC);
    public final static UUID UUID_CSCP_MEASUREMENT =
        UUID.fromString(SampleGattAttributes.CSCP_MEASUREMENT_CHARACTERISTIC);
    public final static UUID UUID_BLOOD_PRESSURE_MEASUREMENT =
        UUID.fromString(SampleGattAttributes.BLP_MEASUREMENT_CHARACTERISTIC);

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        	Log.i(TAG, "BluetoothLeService-->mGattCallback-->onConnectionStateChange-->STATE_CONNECTED=" + BluetoothProfile.STATE_CONNECTED + " STATE_DISCONNECTED=" + BluetoothProfile.STATE_DISCONNECTED);
        	Log.d(TAG, "BluetoothLeService-->mGattCallback-->onConnectionStateChange-->status=" + status + " newState=" + newState);
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Logger.w(TAG, "Connected to GATT server.mBluetoothGatt=" + mBluetoothGatt);
                // Attempts to discover services after successful connection.
                boolean b = mBluetoothGatt.discoverServices();
                Logger.w(TAG, "Attempting to start service discovery:" + b + " -STATE_CONNECTED->newState=" + newState );

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Logger.e(TAG, "Disconnected from GATT server.-STATE_DISCONNECTED->newState=" + newState);
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        	Log.w(TAG, "BluetoothLeService-->mGattCallback-->onServicesDiscovered-->status=" + status + " BluetoothGatt.GATT_SUCCESS=" + BluetoothGatt.GATT_SUCCESS );
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
            	Logger.e(TAG, "onServicesDiscovered received: " + status);
                System.out.println("onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        	
        	Log.i(TAG, "BluetoothLeService-->mGattCallback-->onCharacteristicRead-->status=" + status + " characteristic.uuid=" + characteristic.getUuid().toString() + " characteristic.getValue()=" + characteristic.getValue());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
            
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
        	Log.i(TAG, "BluetoothLeService-->omGattCallback-->onCharacteristicChanged-->" + " characteristic.uuid=" + characteristic.getUuid().toString() + " characteristic.getValue()=" + characteristic.getValue());
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        
        Log.i(TAG, "BluetoothLeService-->broadcastUpdate->characteristic.getUuid()=" + characteristic.getUuid() + "　characteristic.getValue()=" + characteristic.getValue() );

        final byte[] data = characteristic.getValue();
        /*if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA, new String(data) + " " + stringBuilder.toString());
        }*/
        
        // Tim 判断是否是特征8002通道，返回的数据是否是0x01，条件成立再读取特征8001的值
        if(characteristic != null && SampleGattAttributes.GATT_PEDOMETER_CHARACTERISTIC2.equals(characteristic.getUuid().toString()) && data != null && data[0] == 0x01)
        {
        	Log.i(TAG, "BluetoothLeService-->broadcastUpdate->CHARACTERISTIC2=" + characteristic.getUuid() + "　Value()16=" + bytes2HexString(data));
        	readCharacteristic(mGattPedometerCharacteristic_1);
        }
        
        // Tim 判断是否是特征8001通道，是的话，发送广播
        if(characteristic != null && SampleGattAttributes.GATT_PEDOMETER_CHARACTERISTIC1.equals(characteristic.getUuid().toString())  && data !=null)
        {
        	Log.i(TAG, "BluetoothLeService-->broadcastUpdate->CHARACTERISTIC1=" + characteristic.getUuid() + "　Value()16=" + bytes2HexString(data) );
            intent.putExtra(EXTRA_DATA, data);
            sendBroadcast(intent);
        }
        
        
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        Log.w(TAG, "device.getBondState=="+device.getBondState());
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
        
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        boolean isenable = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        
//        characteristic = null;
        
        
       /* final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            // If there is an active notification on a characteristic, clear
            // it first so it doesn't update the data field on the user interface.
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification(
                        mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            mBluetoothLeService.readCharacteristic(characteristic);*/
        
                
       /* if(characteristic != null && characteristic.getUuid().toString().equals(SampleGattAttributes.GATT_PEDOMETER_CHARACTERISTIC1))
    	{			
        	BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.GATT_PEDOMETER_CHARACTERISTIC1));
        	byte[] bytes = new byte[]{0x6E, 0x01, 0x04, 0x01, (byte) 0x8F}; // 获取watchID
            descriptor.setValue(bytes);
            mBluetoothGatt.writeDescriptor(descriptor);
		}
		
		
		if(characteristic != null && characteristic.getUuid().toString().equals(SampleGattAttributes.GATT_PEDOMETER_CHARACTERISTIC2))
		{			
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.GATT_PEDOMETER_CHARACTERISTIC2));
            descriptor.setValue(new byte[]{3});
            mBluetoothGatt.writeDescriptor(descriptor);
		}*/

        /*if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid()) ||
            UUID_CSCP_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
        if (UUID_BLOOD_PRESSURE_MEASUREMENT.equals(characteristic.getUuid()) ||
                UUID_CSCP_MEASUREMENT.equals(characteristic.getUuid())) {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                        UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }*/
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    
    /**
     * 
     * Tim 返回一个手环支持的service ：uuid：6006
     * @return A pedometer service.
     */
    public BluetoothGattService getPedometerGattService() {
    	if (mBluetoothGatt == null) return null;
    	
    	UUID uuid = UUID.fromString(SampleGattAttributes.GATT_PEDOMETER_SERVICE);
    	return mBluetoothGatt.getService(uuid);
    }
    /**
     * 
     * Tim 返回一个手环支持的特征1:service ：uuid：6006对应的特征1:8001
     * @return A pedometer service.
     */
    public BluetoothGattCharacteristic getPedometerGattCharacteristic1() {
    	BluetoothGattService mBluetoothGattService =  getPedometerGattService();
    	if (mBluetoothGattService == null) return null;
    	
    	UUID uuid = UUID.fromString(SampleGattAttributes.GATT_PEDOMETER_CHARACTERISTIC1);
    	
    	return mBluetoothGattService.getCharacteristic(uuid);
    }
    /**
     * 
     * Tim 返回一个手环支持的特征2:service ：uuid：6006对应的特征2:8002
     * @return A pedometer service.
     */
    public BluetoothGattCharacteristic getPedometerGattCharacteristic2() {
    	BluetoothGattService mBluetoothGattService =  getPedometerGattService();
    	if (mBluetoothGattService == null) return null;
    	
    	UUID uuid = UUID.fromString(SampleGattAttributes.GATT_PEDOMETER_CHARACTERISTIC2);
    	return mBluetoothGattService.getCharacteristic(uuid);
    }
    
    // Tim 修改返回类型void 为boolean
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic){
    	if (mBluetoothGatt != null) {
    		/*// 把 8001与8002区分出来，为了在，
    		if(characteristic != null && characteristic.getUuid().toString().equals(SampleGattAttributes.GATT_PEDOMETER_CHARACTERISTIC2)){
    			mGattPedometerCharacteristic_2 = characteristic;
    		}else if(characteristic != null && characteristic.getUuid().toString().equals(SampleGattAttributes.GATT_PEDOMETER_CHARACTERISTIC1)){
    			mGattPedometerCharacteristic_1 = characteristic;
    		}*/
    		return mBluetoothGatt.writeCharacteristic(characteristic);
    	}
    	return false;
    }
    
 // Tim 发送数据:new byte[]{0x6E, 0x01, 0x04, 0x01, (byte) 0x8F}
    public void sendDataToPedometer(BluetoothGattCharacteristic mGattPedometerCharacteristic_1, BluetoothGattCharacteristic mGattPedometerCharacteristic_2, byte[] bytes){
    	
    	this.mGattPedometerCharacteristic_1 = mGattPedometerCharacteristic_1;
    	this.mGattPedometerCharacteristic_2 = mGattPedometerCharacteristic_2;
    	if(mGattPedometerCharacteristic_1 != null && mGattPedometerCharacteristic_1.getUuid().toString().equals(SampleGattAttributes.GATT_PEDOMETER_CHARACTERISTIC1))
    	{			
			Logger.i(TAG, "mGattPedmeterWriteCharacteristic.getUuid().toString()=" + mGattPedometerCharacteristic_1.getUuid().toString());			
			// WRITE_TYPE_DEFAULT, WRITE_TYPE_NO_RESPONSE or WRITE_TYPE_SIGNED.
			 mGattPedometerCharacteristic_1.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
			// 开启监听 8002数据改变
			setCharacteristicNotification(mGattPedometerCharacteristic_1, true );
			
			mGattPedometerCharacteristic_1.setValue(bytes);
			boolean b = writeCharacteristic(mGattPedometerCharacteristic_1);		
			Logger.i(TAG, "mGattPedometerCharacteristic_1 写入是否成功：" + b);
		}
		
		
		if(mGattPedometerCharacteristic_2 != null && mGattPedometerCharacteristic_2.getUuid().toString().equals(SampleGattAttributes.GATT_PEDOMETER_CHARACTERISTIC2))
		{			
			Logger.i(TAG, "mGattPedometerReadCharacteristic.getUuid().toString()=" + mGattPedometerCharacteristic_2.getUuid().toString());			
			mGattPedometerCharacteristic_2.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
			// 开启监听 8002数据改变必须要true
			setCharacteristicNotification(mGattPedometerCharacteristic_2, true );	
			// 结束标志
			mGattPedometerCharacteristic_2.setValue(new byte[]{3}); 
			boolean b = writeCharacteristic(mGattPedometerCharacteristic_2);		
			Logger.i(TAG, "mGattPedometerCharacteristic_2 写入是否成功：" + b);
		}
    }
    
    // Tim byte数组转换为16进制的字符串
    public static String bytes2HexString(byte[] b) {
		  String ret = "";
		  for (int i = 0; i < b.length; i++) {
		   String hex = Integer.toHexString(b[ i ] & 0xFF);
		   if (hex.length() == 1) {
		    hex = '0' + hex;
		   }
		   ret += hex.toUpperCase();
		  }
		  return ret;
	}
}
