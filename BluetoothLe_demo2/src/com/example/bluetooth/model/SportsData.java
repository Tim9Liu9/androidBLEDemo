package com.example.bluetooth.model;

import java.io.Serializable;

import com.example.bluetooth.utils.NumberUtils;




public class SportsData implements Serializable{
	private static final long serialVersionUID = 1L;
	public int sport_type;    // 运动类型：0x00：其他， 0x01：走路， 0x02：跑步，0x03：仰卧起坐，目前只有走路
	public long sport_time_stamp;    // 运动时间，时间戳，秒
	public int sport_steps;      // 步数
	public int sport_energy;      // 能量环值Energy 
	public int sport_cal;      // 卡路里
	
	
	
	public SportsData(){};	
		
	public SportsData(int sport_type , long sport_time_stamp, int sport_steps, int sport_energy , int sport_cal) 
	{
		super();
		this.sport_type = sport_type;
		this.sport_time_stamp = sport_time_stamp;
		this.sport_steps = sport_steps;
		this.sport_energy = sport_energy;
		this.sport_cal = sport_cal;	
	}
	
	
	@Override
	public String toString()
	{		
		return "sport_type:" + sport_type  +" sport_time_stamp:"+ sport_time_stamp + "=" + NumberUtils.timeStamp2format(sport_time_stamp) + "\n" + " sport_steps:"+ sport_steps + " sport_energy:"+ sport_energy  +" sport_cal:"+ sport_cal ;
	} 
	
}
