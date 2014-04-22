package com.example.bluetooth.le2;



//package com.example.bluetooth.le;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.model.SleepData;
import com.example.bluetooth.model.SportsData;
import com.example.bluetooth.service.DBService;
import com.example.bluetooth.utils.Logger;
import com.example.bluetooth.utils.NumberUtils;



/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = "DeviceControlActivity";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private TextView mServiceName;
    private TextView textview_show;
    private TextView textview_show2;
    private TextView textview_show3;
    private TextView textview_show4;
//    private EditText mEditText;
    private Button sendGattBtn;
    private String mDeviceName;
    private String mDeviceAddress;
   
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mGattWriteCharacteristic;
    
    private BluetoothGattService mGattPedometerService;
    private BluetoothGattCharacteristic mGattPedometerCharacteristic_1;
    private BluetoothGattCharacteristic mGattPedometerCharacteristic_2;
    
    private int orderType = 0; // 1:获取电池电量；2：获取最新的（当天汇总）运动数据； 3.请求睡眠数据0x02格式总数；4：请求睡眠详细数据；  5.获取运动详细数据
    
    private boolean isSaveData = false; // 获取睡眠数据是否已经完成
    
    private List<SportsData> mGetPedometerSportsDataList; // 从手环上传过来的运动数据
	private boolean isGetSportsDataFinished = true; // 获取运动数据是否已经完成
	
	private List<SleepData> mGetPedometerSleepDataList; // 从手环上传过来的睡眠数据
	private boolean isGetSleepDataFinished = true; // 获取睡眠数据是否已经完成
	
	private DBService dbService;
	

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    
    private int success = 0;// 成功获取到的数据次数
    private int clicked = 0;// 点击获取按钮的次数
    
    private long startTime = 0;
    


    
    
    private ProgressDialog mProgressDialog; // 进度条
	private static final int PROGRESSBAR_HIDE  = 5551;
	    
	    private Handler mHandler  = new Handler( )
		{
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
			switch (msg.what) {
	
	        case PROGRESSBAR_HIDE:
	        	if(mProgressDialog != null && mProgressDialog.isShowing())
	            {
	        	   mProgressDialog.dismiss();
	        	   
	            }
	        	break;
	
	
			}
		}
	};

    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
       
        //mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);
        mServiceName = (TextView) findViewById(R.id.service_name);
        textview_show = (TextView) findViewById(R.id.textview_show);
        textview_show2 = (TextView) findViewById(R.id.textview_show2);
        textview_show3 = (TextView) findViewById(R.id.textview_show3);
        textview_show4 = (TextView) findViewById(R.id.textview_show4);
        sendGattBtn = (Button) findViewById(R.id.send_value);
        sendGattBtn.setOnClickListener(new OnClickListener()
        {

			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				

		}
        });

	    getActionBar().setTitle(mDeviceName);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	    
	    /*if (mBluetoothLeService != null) {
	        final boolean result = mBluetoothLeService.connect(mDeviceAddress);
	        Log.d(TAG, "Connect request result=" + result);
	    }*/
	    dbService = new DBService(this);
	    orderType = 1; // 先获取电池电量
		
	    mGetPedometerSleepDataList = new ArrayList<SleepData>();
		mGetPedometerSportsDataList = new ArrayList<SportsData>();
    }
    
    
    
    

	// Tim 服务的绑定
	private final ServiceConnection mServiceConnection = new ServiceConnection() 
	{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            
            if (!mBluetoothLeService.initialize()) {
                Logger.e(TAG, "Unable to initialize Bluetooth");
//                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            // Tim 自动连接，在绑定后，立即进行一次了
            mBluetoothLeService.connect(mDeviceAddress);
            Log.i(TAG, "onServiceConnected()-->mBluetoothLeService=" + mBluetoothLeService);
            
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    
    // Tim 广播,接收service发过来的广播
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {        	
            final String action = intent.getAction();
            Logger.i(TAG, "BroadcastReceiver.action=" + action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
//                updateConnectionState(R.string.connected);
//                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) 
            {
                mConnected = false;
//                updateConnectionState(R.string.disconnected);
//                invalidateOptionsMenu();                
//                clearUI();
                if(mBluetoothLeService != null){
                	mBluetoothLeService.close();               	
                }           
                
                textview_show4.setText("运行时间（毫秒）：" + (System.currentTimeMillis() - startTime) );
                
                
                // 获取睡眠数据中间断开，将进行保存数据到sqlite的动作，然后再去获取一次
                Logger.w(TAG, "isSleeped=" + isGetSleepDataFinished + " SleepDataList=" + mGetPedometerSleepDataList);
                if(isGetSleepDataFinished == false && mGetPedometerSleepDataList!=null && mGetPedometerSleepDataList.size() > 0){
                	Logger.w(TAG,"先保存数据，获取剩余的睡眠详细数据");                	
                	new ThreadSaveSleepData().start();
                	
                }
                
                // 获取运动数据中间断开，将进行保存数据到sqlite的动作，然后再去获取一次
                Logger.w(TAG, "isSportsed=" + isGetSportsDataFinished + " SportsDataList=" + mGetPedometerSportsDataList);
                if(isGetSportsDataFinished == false && mGetPedometerSportsDataList!=null && mGetPedometerSportsDataList.size() > 0){
                	Logger.w(TAG,"先保存数据，获取剩余的运动详细数据");
//                	Toast.makeText(SynDataActivity.this, R.string.get_sports_data_break_off, Toast.LENGTH_SHORT).show();
            		
            		new ThreadSaveSportsData().start();
            		                 	
//            		mHandler.sendEmptyMessage(SPORTS_DATA_LOADED);
                }
               
                
//                mHandler.sendEmptyMessage(UNFINISH_CLOSE);
                
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            	startTime = System.currentTimeMillis();
            	getGattServiceAndSendData();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	// Tim 获取到的手环返回的byte数组
            	byte[] bytes = intent.getByteArrayExtra( BluetoothLeService.EXTRA_DATA);            	
            	Logger.e(TAG, "获取到的数据：" + NumberUtils.bytes2HexString(bytes));
            	displayData(NumberUtils.bytes2HexString(bytes));
            	parseBytesArray(bytes);
            	if(success%20 == 0){
            		// 开启监听 8002数据改变
           		 	mBluetoothLeService.setCharacteristicNotification(mGattPedometerCharacteristic_1, true );
            	}
            	
            }
        }
 
		
    };


    

    // Tim 解析获取到的byte[]
    private void parseBytesArray(byte[] bytes) 
    {
    	// 获取手环电池电量的数据解析：对应的命令是：bytes = new byte[]{0x6E, 0x01, 0x0F, 0x01, (byte) 0x8F};
    	// 返回的数据格式是:6E-01-00-14-8F
    	if (bytes.length == 5 && bytes[0] == 0x6e && bytes[2] == 0x00 && bytes[4] == (byte)0x8f ) 
    	{
    		int i = bytes[3] * 5;
    		if(i >100) i = 100;
    		
    		Logger.d(TAG, "电池电量是：" + i + "%");
    		textview_show.setText("电池电量是：" + i + "%");
    		
    		orderType = 2;  // 1:获取电池电量，2：获取最新的（当天汇总）运动数据 3.请求睡眠数据0x02格式总数
    		sendOrderToDevice(orderType);
    		
    	}
    	
    	
    	
    	// 发送当天的汇总数据解析： 对应的命令是： byte[] bytes = new byte[]{0x6E, 0x01, 0x1b, 0x01, (byte) 0x8F}; // 获取最新当天汇总的运动数据
    	// 返回的数据格式是：6E-01-0F-00000000-00000000-1B000000-00000000-8F（能力环值-卡路里-步数-当天的目标能量值）
    	if (bytes.length == 20 && bytes[0] == 0x6e && bytes[2] == 0x0F && bytes[19] == (byte)0x8f ) 
    	{

    		int currentSteps = NumberUtils.byteReverseToInt(new byte[]{bytes[11], bytes[12], bytes[13],bytes[14]});
    		Logger.d(TAG, "运动的总步数是：" + currentSteps + "%");
    		textview_show2.setText( "运动的总步数是：" + currentSteps + "");
    		orderType = 4;  // 4：请求睡眠详细数据；  5.获取运动详细数据
    		sendOrderToDevice(orderType);
    	}
    	

    	
//===============================获取睡眠详细数据返回的数据====================================================== 
    	
    	// 获取收到睡眠详细数据失败6E-01-01-31-03-8F；命令响应成功：6E-01-01-31-00-8F 
    	if (bytes.length == 6 && bytes[0] == 0x6e  && bytes[2] == 0x01 && bytes[3] == 0x31 && bytes[5] == (byte)0x8f ) {
    		
    		isGetSleepDataFinished = true; // 获取运动数据完成，将进行保存数据到sqlite的动作
			if( bytes[4] == 0x00 ){
				// 命令响应成功
				Logger.d(TAG, "后台线程启动-->保存的数据条数=" + mGetPedometerSleepDataList.size() + "<--");	    		
	    		new ThreadSaveSleepData().start();
			}else {
//    			Toast.makeText(this, R.string.get_sports_data_finish, Toast.LENGTH_SHORT).show();
//    			mHandler.sendEmptyMessage(PROGRESSBAR_HIDE);
    			orderType = 5; // 4：请求睡眠详细数据；  5.获取运动详细数据
    			sendOrderToDevice(orderType);
    		}
    		
    	}
    	
    	// 请求发送睡眠详细数据解析： 对应的命令是： byte[] bytes = new byte[]{0x6E, 0x01, 0x31, 0x01, (byte) 0x8F}; 
    	//      返回的数据格式是：6E-01-13-10-B0BC4C53-8F
        if (bytes.length == 9 && bytes[0] == 0x6e && bytes[2] == 0x13 && bytes[8] == (byte)0x8f ) 
        {
        	Logger.i(TAG, "收到睡眠详细数据-->1");
        	isGetSleepDataFinished = false; // 获取运动数据没有完成
        	
        	SleepData mData = new SleepData();
    		mData.sleep_type = NumberUtils.byteToInt(new byte[]{bytes[3]});;        		
    		mData.sleep_time_stamp = (long)NumberUtils.byteReverseToInt(new byte[]{bytes[4], bytes[5], bytes[6], bytes[7]});
    		
    		mGetPedometerSleepDataList.add(mData);
        	
        }

        
    	
//===============================获取运动详细数据返回的数据======================================================
    	// 获取收到运动详细数据失败6E-01-01-06-04-8F；命令响应成功：6E-01-01-06-00-8F 
    	if (bytes.length == 6 && bytes[0] == 0x6e  && bytes[2] == 0x01 && bytes[3] == 0x06 && bytes[5] == (byte)0x8f ) {
    		
    		
			if( bytes[4] == 0x00 ){
				
				Logger.d(TAG, "后台线程启动-->保存的数据条数=" + mGetPedometerSportsDataList.size() + "<--");
	    		if(!isGetSportsDataFinished) // 如果是true，说明在解析完运动数据的时候已经保存过一次了
	    		{
	    			new ThreadSaveSportsData().start();
	    		}
	    			
				
				// 命令响应成功，再次去读取一次数据
//				mBluetoothLeService.readCharacteristic(mGattPedometerCharacteristic_1);
			}else if(bytes[4] == 0x04){
    			Toast.makeText(this, "已经没有新的运动数据获取！", Toast.LENGTH_SHORT).show();
//    			mHandler.sendEmptyMessage(PROGRESSBAR_HIDE);
    		}else{
//    			Toast.makeText(this, R.string.get_sports_data_false, Toast.LENGTH_SHORT).show();
//    			mHandler.sendEmptyMessage(PROGRESSBAR_HIDE);
    		}
			
			isGetSportsDataFinished = true; // 获取运动数据完成，将进行保存数据到sqlite的动作
    		
    	}    	
    	// 收到运动详细数据6E-01-05-81-39683B53-1E000000-00000000-100A0000-8F
    	//                6E-01-05-81-423B3E53-12000000-00000000-82020000-8F
        if (bytes.length == 21 && bytes[0] == 0x6e && bytes[2] == 0x05 && bytes[20] == (byte)0x8f ) 
        {
        	Logger.i(TAG, "收到运动详细数据-->1");
        	isGetSportsDataFinished = false; // 获取运动数据没有完成
            //0x81说明后面还有数据,0x01是最后一条数据了
        	if(bytes[3] == (byte)0x81 || bytes[3] == (byte)0x01)
        	{  
        		SportsData mSportsData = new SportsData();
        		mSportsData.sport_type = 1;        		
        		mSportsData.sport_time_stamp = (long)NumberUtils.byteReverseToInt(new byte[]{bytes[4], bytes[5], bytes[6], bytes[7]});
        		mSportsData.sport_steps =      NumberUtils.byteReverseToInt(new byte[]{bytes[8], bytes[9], bytes[10],bytes[11]});
        		mSportsData.sport_energy =     NumberUtils.byteReverseToInt(new byte[]{bytes[12],bytes[13],bytes[14],bytes[15]});
        		mSportsData.sport_cal =        NumberUtils.byteReverseToInt(new byte[]{bytes[16],bytes[17],bytes[18],bytes[19]});
        		
        		mGetPedometerSportsDataList.add(mSportsData);
//        		Logger.i(TAG, "收到运动详细数据-->2 time=" + mSportsData.sport_time_stamp + " steps=" + mSportsData.sport_steps 
//        				+ " energy=" + mSportsData.sport_energy + " cal=" + mSportsData.sport_cal);
        		
        		
        		if(bytes[3] == (byte)0x01)
            	{
        			
        			new ThreadSaveSportsData().start();
        			Logger.d(TAG, "收到运动详细数据-->3完成，条数=" + mGetPedometerSportsDataList.size());
        			isGetSportsDataFinished = true;
            	}
        		
        	}

        }
		
		
	}
 
  

    
    
    // Tim 获取service以及发送获取数据指令
    private void getGattServiceAndSendData()
    {
    	mGattPedometerService = mBluetoothLeService.getPedometerGattService();
    	mGattPedometerCharacteristic_1 = mBluetoothLeService.getPedometerGattCharacteristic1();
    	mGattPedometerCharacteristic_2 = mBluetoothLeService.getPedometerGattCharacteristic2();
    	Logger.i(TAG, "Characteristic_1=" + mGattPedometerCharacteristic_1.getUuid() + " Characteristic_2=" + mGattPedometerCharacteristic_2.getUuid() );
    	
        
        sendOrderToDevice(orderType);
    }
    
    

    // 发送的命令类型选择
    private void sendOrderToDevice(int orderType){
    	
    	/*try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
    	 if(mBluetoothLeService !=null && mGattPedometerCharacteristic_1 != null && mGattPedometerCharacteristic_2 != null)
         {
    		 byte[] bytes = null;
    		 switch (orderType) 
    		 {
    		 
    		 case -1:	  // 自动删除运动与睡眠数据：0x6e-01-32-03-8f，
    			 bytes = new byte[]{0x6E, 0x01, 0x32, 0x03, (byte) 0x8F}; 
    			 break;
    		 
    		 case 0:	  // 手动删除运动与睡眠数据： 0x6e-01-32-04-8f
    			 bytes = new byte[]{0x6E, 0x01, 0x32, 0x04, (byte) 0x8F}; 
    			 break;
    		 
			case 1:	  // 获取手环的电量
				bytes = new byte[]{0x6E, 0x01, 0x0F, 0x01, (byte) 0x8F}; 
				break;
				
			case 2:	  // 获取最新当天汇总的运动数据
				bytes = new byte[]{0x6E, 0x01, 0x1B, 0x01, (byte) 0x8F}; 
				break;
				
			// 为了简化：少了获取睡眠详细数据
			/*case 3:  // 请求运动记录0x01、睡眠数据0x02格式总数 
				bytes = new byte[]{0x6E, 0x01, 0x30, 0x02, (byte) 0x8F};
				break;*/
				
			case 4:  // 4：请求睡眠详细数据；  5.获取运动详细数据
				bytes = new byte[]{0x6E, 0x01, 0x31, 0x01, (byte) 0x8F};
				break;
				
			case 5:  // 4：请求睡眠详细数据；  5.获取运动详细数据
				bytes = new byte[]{0x6E, 0x01, 0x06, 0x01, (byte) 0x8F};
				break;

			default:
				break;
			}
    		
         	mBluetoothLeService.sendDataToPedometer(mGattPedometerCharacteristic_1, mGattPedometerCharacteristic_2, bytes );
         	Logger.e(TAG, "要发送的命令是=" + NumberUtils.bytes2HexString(bytes) );
         }
    }
    
    
    // 后台线程保存运动详细数据到数据库里面
 	private class ThreadSaveSportsData extends Thread  // Thread threadGetDBData = new Thread()
 	{
 		@Override
 	     public synchronized void  run() 
 	     { 
 	    	
 			isSaveData = true; // 已经有数据保存
 			
 			if( mGetPedometerSportsDataList != null &&mGetPedometerSportsDataList.size() > 0)
			{
				Logger.w(TAG, "保存的--运动--数据条数=" + mGetPedometerSportsDataList.size());
				dbService.saveSportsDataList(mGetPedometerSportsDataList);
				mGetPedometerSportsDataList.clear();
			}
 			
 			
			
			// 说明在获取详细数据的时候断连接了，保存详细运动数据后，再重新去获取一次运动详细数据：
			if(isGetSportsDataFinished == false && orderType == 5 )
			{
				if (mBluetoothLeService != null) {
		            final boolean result1 = mBluetoothLeService.connect(mDeviceAddress);
		           Logger.d(TAG, "mBluetoothLeService.connect(mDeviceAddress)--》" + result1 + " orderType=" + orderType);
		        }
           	
           }else{
//           	mHandler.sendEmptyMessage(FINISH_CLOSE);
           }
 	     }
 	 };
 	 
 	 // 后台线程保存睡眠详细数据到数据库里面
 	 private class ThreadSaveSleepData extends Thread  // Thread threadGetDBData = new Thread()
 	 {
 		 @Override
 		 public synchronized void  run() 
 		 { 
 			 
 			 isSaveData = true; // 已经有数据保存
 			 
 			 if( mGetPedometerSleepDataList != null &&mGetPedometerSleepDataList.size() > 0)
 			 {
 				 Logger.w(TAG, "保存的--睡眠--数据条数=" + mGetPedometerSleepDataList.size()); 				
 				dbService.saveSleepDataList(mGetPedometerSleepDataList);
 				mGetPedometerSleepDataList.clear();
 			 }
 			 
 			 
 			 
 			 // 说明在获取详细数据的时候断连接了，保存详细运动数据后，再重新去获取一次运动详细数据：
 			 if(isGetSportsDataFinished == false && orderType == 4 )
 			 {
 				 if (mBluetoothLeService != null) {
 					 final boolean result1 = mBluetoothLeService.connect(mDeviceAddress);
 					 Logger.d(TAG, "mBluetoothLeService.connect(mDeviceAddress)--》" + result1 + " orderType=" + orderType);
 				 }
 				 
 			 }else{
//           	mHandler.sendEmptyMessage(FINISH_CLOSE);
 			 }
 		 }
 	 };
    
    	


    @Override
    protected void onResume() {
        super.onResume();
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
//            	success = 0;// 成功获取到的数据次数
//                private int clicked = 0;// 点击获取按钮的次数
            	clicked++;
            	
                boolean b = mBluetoothLeService.connect(mDeviceAddress);
                Logger.e(TAG, "与蓝牙通信开始了--》点击了连接按钮,点击次数=" + clicked + " connect()=" + b);
//                mProgressDialog = ProgressDialog.show(this,null, "loading...", true, true);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
        	
            success++;
            mDataField.setText("点击次数=" + clicked + " 成功次数=" + success + "-->" + data);
        }
    }
    
    private void displayServiceName(String service) {
        if (service != null) {
            mServiceName.setText(service);
        }
    }

    

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}

