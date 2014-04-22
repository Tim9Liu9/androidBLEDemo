package com.example.bluetooth.le2;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class SampleGattAttributes {
	private static HashMap<String, String> attributes = new HashMap();
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	// public static String GATT_SERVICE =
	// "0000ff00-0000-1000-8000-00805f9b34fb";

	public static String GATT_PEDOMETER_SERVICE = "00006006-0000-1000-8000-00805f9b34fb";

	public static String HRP_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";
	public static String CSCP_SERVICE = "00001816-0000-1000-8000-00805f9b34fb";
	public static String BLP_SERVICE = "00001810-0000-1000-8000-00805f9b34fb";
	public static String FMP_SERVICE = "00001802-0000-1000-8000-00805f9b34fb";

	// public static String GATT_READ_CHARACTERISTIC =
	// "0000ff01-0000-1000-8000-00805f9b34fb";
	// public static String GATT_WRITE_CHARACTERISTIC =
	// "0000ff02-0000-1000-8000-00805f9b34fb"; mGattPedmeterWriteCharacteristic
	public static String GATT_PEDOMETER_CHARACTERISTIC1 = "00008001-0000-1000-8000-00805f9b34fb";
	public static String GATT_PEDOMETER_CHARACTERISTIC2 = "00008002-0000-1000-8000-00805f9b34fb";
	
	public static String HRP_MEASUREMENT_CHARACTERISTIC = "00002a37-0000-1000-8000-00805f9b34fb";
	public static String CSCP_MEASUREMENT_CHARACTERISTIC = "00002a5b-0000-1000-8000-00805f9b34fb";
	public static String BLP_MEASUREMENT_CHARACTERISTIC = "00002a35-0000-1000-8000-00805f9b34fb";
	public static String FMP_MEASUREMENT_CHARACTERISTIC = "00002a06-0000-1000-8000-00805f9b34fb";

	static {
		// Sample Services.
		attributes.put(HRP_SERVICE, "Heart Rate Service");
		attributes.put(GATT_PEDOMETER_SERVICE, "GATT Service");
		attributes.put(CSCP_SERVICE, "CSCP Service");
		attributes.put(BLP_SERVICE, "BLP Service");
		attributes.put(FMP_SERVICE, "FMP Service");

	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}
}