package com.example.bluetooth.model;

import java.io.Serializable;

import com.example.bluetooth.utils.NumberUtils;



public class SleepData  implements Serializable {	
	private static final long serialVersionUID = 3L;
	public int sleep_id;    		// 睡眠ID
	public int sleep_type;    		// 睡眠类型：0x00：睡着， 0x01：浅睡， 0x02：醒着，0x03：准备入睡，0x10（16）：进入睡眠模式；0x11（17）：退出睡眠模式
	public long sleep_time_stamp;   // 睡眠日期时间，时间戳，秒
	
	public SleepData(){};	
	
	public SleepData(int sleep_type , long sleep_time_stamp) 
	{
		super();
		this.sleep_id = -1;
		this.sleep_type = sleep_type;
		this.sleep_time_stamp = sleep_time_stamp;
		
	}
	
	

	public SleepData(int sleep_id, int sleep_type , long sleep_time_stamp) 
	{
		super();
		this.sleep_id = sleep_id;
		this.sleep_type = sleep_type;
		this.sleep_time_stamp = sleep_time_stamp;
		
	}
	
	
	@Override
	public String toString()
	{		
		return "id:" + sleep_id  + " type:" + sleep_type  + " time_stamp:"+ sleep_time_stamp + " 格式化：" + NumberUtils.timeStamp2format(sleep_time_stamp) +  "\n"  ;
	}

}
