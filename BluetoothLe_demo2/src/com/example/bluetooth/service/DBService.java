package com.example.bluetooth.service;

/**
 * Created with Eclipse.
 * Author: Tim Liu  email:9925124@qq.com
 * Date: 14-4-7
 * Time: 11:27
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bluetooth.model.SleepData;
import com.example.bluetooth.model.SportsData;
import com.example.bluetooth.utils.Logger;
import com.example.bluetooth.utils.TimesrUtils;



public class DBService {
	
	
	private static final String TAG = "DBService";
	private DBOpenHelper dbOpenHelper;
	
	public DBService(Context context){
		dbOpenHelper = new DBOpenHelper(context);
	}
	
	public DBService(){}
	
	
// ======================运动数据===========================
	// 数据库保存运动数据： 注意事务的处理
	public synchronized  void saveSportsData(SportsData mSportsData ){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();		
		db.beginTransaction(); // 事务
		try{
			db.execSQL("insert or replace into tb_sports_data values(null, ? , ? , ? ,? , ?)", 
				new Object[]{ mSportsData.sport_type, mSportsData.sport_time_stamp, mSportsData.sport_steps, mSportsData.sport_energy, mSportsData.sport_cal} );
			
			db.setTransactionSuccessful();
		}finally {
		    db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
		}
		
		db.close();
	}
	
	// 把查询到的记录数组保存到数据库： 注意事务的处理
	public synchronized  void saveSportsDataList(List<SportsData> mSportsDataList ){
//		Logger.i(TAG, "saveSportsDataList-->mSportsDataList=" + mSportsDataList);
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();		
		db.beginTransaction(); // 事务
		try{
			for(SportsData mSportsData : mSportsDataList)
			{
				db.execSQL("insert or replace into tb_sports_data values(null, ? , ? , ? ,? , ?)", 
					new Object[]{ mSportsData.sport_type, mSportsData.sport_time_stamp, mSportsData.sport_steps, mSportsData.sport_energy, mSportsData.sport_cal} );
//				Logger.i(TAG, "saveSportsDataList-->mSportsData=" + mSportsData);
			}
			db.setTransactionSuccessful();
		}finally {
			db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
		}
		
		db.close();
	}
	
	
	//  删除数据库里面的SportsData信息： 注意事务的处理
	public synchronized  void deleteSportsData(int id){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.beginTransaction(); // 事务
		try{
			db.execSQL("delete from tb_sports_data where sports_key_pk_id = ?" ,new Object[]{id}); // 删除新闻内容表的数据			
			db.setTransactionSuccessful();
		}finally {
		    db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
		}
		
		db.close();
	}
	

	// 查询出当前日历的当天的运动数据列表
	public synchronized List<SportsData> getSportsDataList( Calendar cal )
	{
		String whereStr = "";
		if(cal != null )
		{
			int currentDayFirst = TimesrUtils.getTimesMorning(cal);
			whereStr = "where " + currentDayFirst + " <= sport_time_stamp AND sport_time_stamp < " + (currentDayFirst + 24*60*60);
		}
		
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor =db.rawQuery("select * from  tb_sports_data " + whereStr + " ORDER BY sports_key_pk_id desc" , null ); // 倒叙排列
		List<SportsData> mDatas =  new ArrayList<SportsData>();
		SportsData mSportsData = null;
		while(cursor.moveToNext())
		{
											
			int sport_type = cursor.getInt(cursor.getColumnIndex("sport_type"));
			long sport_time = cursor.getLong(cursor.getColumnIndex("sport_time_stamp"));									
			int sport_steps = cursor.getInt(cursor.getColumnIndex("sport_steps"));
			int sport_energy = cursor.getInt(cursor.getColumnIndex("sport_energy"));
			int sport_cal = cursor.getInt(cursor.getColumnIndex("sport_cal"));
			
			mSportsData = new SportsData(sport_type, sport_time, sport_steps, sport_energy, sport_cal);
			Logger.i(TAG, "getSportsDataList->mSportsData=" + mSportsData);
			mDatas.add(mSportsData);
		}
		cursor.close();
		db.close();
//		Logger.i(TAG,"mDatas=" + mDatas);
		return mDatas;
		
		
	}
	
	
	
		
	
	
	
	// 数据库里面做了SportsData的数据条数：
	public synchronized  int getSportsDataCount()
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select count( * ) from  tb_sports_data where 1=1", null );
		cursor.moveToFirst();
		int x = cursor.getInt(0);
		cursor.close();
		db.close();
		return x;
	}
	

	
// ======================睡眠===========================	
	// 数据库保存睡眠数据： 注意事务的处理
	public synchronized  void saveSleepData(SleepData mData ){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();		
		db.beginTransaction(); // 事务
		try{
			db.execSQL("insert or replace into tb_sleep values(null, ? , ? )", 
				new Object[]{ mData.sleep_type, mData.sleep_time_stamp } );
			
			db.setTransactionSuccessful();
		}finally {
		    db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
		}
		
		db.close();
	}
	
	// 把睡眠记录数组保存到数据库： 注意事务的处理
	public synchronized  void saveSleepDataList(List<SleepData> mDataList ){
//		Logger.i(TAG, "saveSportsDataList-->mSportsDataList=" + mSportsDataList);
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();		
		db.beginTransaction(); // 事务
		try{
			for(SleepData mData : mDataList)
			{
				db.execSQL("insert or replace into tb_sleep values(null, ? , ? )", 
					new Object[]{ mData.sleep_type, mData.sleep_time_stamp} );
//				Logger.i(TAG, "saveSportsDataList-->mSportsData=" + mSportsData);
			}
			db.setTransactionSuccessful();
		}finally {
			db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
		}
		
		db.close();
	}
	
	

	
	
	
	
	
	
}
