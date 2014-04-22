package com.example.bluetooth.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBOpenHelper extends SQLiteOpenHelper{
	
	
	public DBOpenHelper(Context context){
		super(context,"pedometer_app.db",null,3);
	}


	
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		
		//创建运动数据详细表
		db.execSQL("create table tb_sports_data (" + 
					" sports_key_pk_id integer primary key autoincrement," +   // 自动产生的主键
					" sport_type int," +					// 运动类型			
					" sport_time_stamp long null unique," +		// 运动日期时间，时间戳，秒
					" sport_steps int," +					// 步数					
					" sport_energy int," + 					// 能量环值					
					" sport_cal int )"  					// 卡路里

		);		//  排列順序
				
		//创建睡眠数据详细表
		db.execSQL("create table tb_sleep (" + // IF NOT EXISTS
				" sleep_key_pk_id integer primary key autoincrement," +   // 自动产生的主键
				" sleep_type int," +	            					  // 睡眠类型    0:睡着；1：浅睡；2：醒着；3：准备入睡；4：退出入睡；16:进入睡眠模式；17:退出睡眠模式；	
				" sleep_time_stamp long null unique )"  				  // 睡眠时间，时间戳，秒
		);		//  排列順序
		
		//创建提醒表
		db.execSQL("create table tb_remind_notes (" + // IF NOT EXISTS
				" remind_key_pk_id integer primary key autoincrement," +   // 自动产生的主键
				" remind_type int," +	            // 提醒类型	1：运动；2：睡觉；3：吃饭；4：吃药；5:喝水；6:自定义；						
				" remind_text varchar(20)," +		// 提醒文字
				" remind_time_hours int ," +		// 提醒时间，小时：24小时制
				" remind_time_minutes int ," +		// 提醒时间，分钟Hours and minutes
				" remind_week varchar(7)," +		// 星期提醒：从周日、六、五、四、三、二到周一，1是提醒，0是不提醒；星期一星期二提醒：0000011；				
				" remind_set_ok int )"  	        // 提醒是否已经提交到手环上，成功是：1，没有提交是：0
				);		//  排列順序
		
	}

	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
}