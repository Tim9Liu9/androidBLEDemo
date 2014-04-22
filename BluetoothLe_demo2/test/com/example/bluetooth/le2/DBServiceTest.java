package com.example.bluetooth.le2;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.bluetooth.model.SleepData;
import com.example.bluetooth.model.SportsData;
import com.example.bluetooth.service.DBService;
import com.example.bluetooth.utils.Logger;


public class DBServiceTest extends AndroidTestCase {
	private static final String TAG = "DBServiceTest";
	
	private Calendar calendar;
	
	 public DBServiceTest(String fn) {
	        super();
	        super.setName(fn);
	    }
	// Tim 修改方法的执行顺序   
    public static Test suite() {
    	TestSuite suite = new TestSuite();
//        suite.addTest(new DBServiceTest("testSaveSportsData"));
//        suite.addTest(new DBServiceTest("testGetSportsDataList"));
//    	suite.addTest(new DBServiceTest("testSaveRemindNotes"));
//        suite.addTest(new DBServiceTest("testGetRemindNotesCount"));
//        suite.addTest(new DBServiceTest("testDeleteRemindNotesTableData"));
        suite.addTest(new DBServiceTest("testGetSleepDataList"));
//        suite.addTest(new DBServiceTest("testGetAllRecordDataList"));
        return suite;
    }
	
	
	// 保存运动数据
	public void testSaveSportsData() throws Throwable
	{
		Log.i(TAG, "testsaveSportsData()------>" );
		long time1= System.currentTimeMillis() ;
		Context context = getContext();
		DBService service = new DBService(context);
		SportsData mSportsData = new SportsData(1,1396615911,22,0,894);
		service.saveSportsData(mSportsData);
		
		
		Log.i(TAG,mSportsData.toString());
		long time2= System.currentTimeMillis() ;
		Log.i(TAG, "testsaveSportsData()------>runtims:" + (time2 - time1));
	}
	
	// 获取运动数据
	public void testGetSportsDataList() throws Throwable
	{
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -0);
		long time1= System.currentTimeMillis() ;
		Context context = getContext();
		DBService service = new DBService(context);		
		
		List<SportsData> titles = service.getSportsDataList(calendar);
		Log.i(TAG,titles.toString());
		long time2= System.currentTimeMillis() ;
//		Log.i(TAG, "testGetSportsDataList()->runtims:" + (time2 - time1));
		Log.i(TAG, "titles->size:" + titles.size() );
	}
	
	
	
	// 获取睡眠数据
	public void testGetSleepDataList() throws Throwable
	{
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		long time1= System.currentTimeMillis() ;
		Context context = getContext();
		DBService service = new DBService(context);		
		
//		List<SleepData> mSleepDataList = service.getSleepDataList(calendar);
//		Log.i(TAG,"->" + mSleepDataList.toString());

		
	}

	
	
	
}
