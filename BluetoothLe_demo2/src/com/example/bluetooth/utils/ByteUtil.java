package com.example.bluetooth.utils;



//  org.echo.androidbluesppnfc;
public class ByteUtil {
	private final static String TAG = "ByteUtil";
	
	public static byte[] integer2Bytes(int intValue){
	  byte[] result = new byte[4];
	  result[0] = (byte) ((intValue & 0xFF000000) >> 24);
	  result[1] = (byte) ((intValue & 0x00FF0000) >> 16);
	  result[2] = (byte) ((intValue & 0x0000FF00) >> 8);
	  result[3] = (byte) ((intValue & 0x000000FF));
	  return result;
	 }
	 	 
	 public static byte[] get2Bytes(int intValue) {
	  byte[] tmp = integer2Bytes(intValue);  
	  byte[] result = new byte[2];
	  result[0] = tmp[3];
	  result[1] = tmp[2];
	  return result;
	 }
	  
	 public static byte[] get1Bytes(int intValue) {
	  byte[] tmp = integer2Bytes(intValue);  
	  byte[] result = new byte[1];
	  result[0] = tmp[3];
	  return result;
	 }
	 
	 public static int bytes2Integer(byte[] byteVal){
	  int result = 0;
	  for (int i = 0; i < byteVal.length; i++)
	  {
	   int tmpVal = (byteVal[i] << (8 * (3 - i)));
	   switch (i)
	   {
	    case 0:
	     tmpVal = tmpVal & 0xFF000000;
	     break;
	    case 1:
	     tmpVal = tmpVal & 0x00FF0000;
	     break;
	    case 2:
	     tmpVal = tmpVal & 0x0000FF00;
	     break;
	    case 3:
	     tmpVal = tmpVal & 0x000000FF;
	     break;
	   }
	   result = result | tmpVal;
	  }
	  return result;
	 }
	 
	 public static int anybytes2Integer(byte[] byteVal){ 
	  int len = byteVal.length;
	  if(len>4){
	   return 0;
	  }
	  byte[]tmp = {0,0,0,0};
	  for(int i = 0;i<len;i++){
	   tmp[3-i] = byteVal[len-1-i];
	  }
	  
	  int result = 0;
	  for (int i = 0; i < tmp.length; i++)
	  {
	   int tmpVal = (tmp[i] << (8 * (3 - i)));
	   switch (i)
	   {
	    case 0:
	     tmpVal = tmpVal & 0xFF000000;
	     break;
	    case 1:
	     tmpVal = tmpVal & 0x00FF0000;
	     break;
	    case 2:
	     tmpVal = tmpVal & 0x0000FF00;
	     break;
	    case 3:
	     tmpVal = tmpVal & 0x000000FF;
	     break;
	   }
	   result = result | tmpVal;
	  }
	  return result;
	 }
	 
	 public static void putShort(byte b[], short s, int index) {
	         b[index] = (byte) (s >> 8);
	         b[index + 1] = (byte) (s >> 0);
	  }
	 
	  
	 public static int getOneByteValue(byte b) {
	         return (( b& 0xff));
	 }
	 
	 public static int getShort(byte[] b, int index) {		
	         return (((b[index+1] << 8) | b[index] & 0xff));
	 } 
	 
	 public static int[] getIntArray(byte[] b, int start, int lenth){
		 int[] ret = new int[lenth/2];
		 
		 for(int i=0; i<lenth/2; i++){
			 ret[i] = getShort(b,start+i*2);
			 
		 }
		 return ret;
	 }
	 
	 public static void putInt(byte[] bb, int x, int index) {
	         bb[index + 0] = (byte) (x >> 24);
	         bb[index + 1] = (byte) (x >> 16);
	         bb[index + 2] = (byte) (x >> 8);
	         bb[index + 3] = (byte) (x >> 0);
	 }
	    
	    
	 public static int getInt(byte[] bb, int index) {
		/* if(Debug.DEBUG) Log.e("byteutil","((bb = " + bytes2HexStringIndex(bb, 4, 4));
		 
	         if(Debug.DEBUG) Log.e("byteutil","((bb[index + 3] & 0xff) << 24) = " + ((bb[index + 3] & 0xff) << 24));
	         if(Debug.DEBUG) Log.e("byteutil","((bb[index + 2] & 0xff) << 16) = " + ((bb[index + 2] & 0xff) << 16));
	         if(Debug.DEBUG) Log.e("byteutil","((bb[index + 1] & 0xff) << 8) = " + ((bb[index + 1] & 0xff) << 8));
	         if(Debug.DEBUG) Log.e("byteutil","((bb[index + 0] & 0xff) << 0) = " + ((bb[index + 0] & 0xff) << 0));*/
		 return ((((bb[index + 3] & 0xff) << 24)
	                 | ((bb[index + 2] & 0xff) << 16)
	                 | ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
	 }
	    
	     // /////////////////////////////////////////////////////////
	 public static void putLong(byte[] bb, long x, int index) {
		 
	         bb[index + 0] = (byte) (x >> 56);
	         bb[index + 1] = (byte) (x >> 48);
	         bb[index + 2] = (byte) (x >> 40);
	         bb[index + 3] = (byte) (x >> 32);
	         bb[index + 4] = (byte) (x >> 24);
	         bb[index + 5] = (byte) (x >> 16);
	         bb[index + 6] = (byte) (x >> 8);
	         bb[index + 7] = (byte) (x >> 0);
	 }
	    
	 public static long getLong(byte[] bb, int index) {
	        
		 return ((((long) bb[index + 0] & 0xff) << 56)
	                 | (((long) bb[index + 1] & 0xff) << 48)
	                 | (((long) bb[index + 2] & 0xff) << 40)
	                 | (((long) bb[index + 3] & 0xff) << 32)
	                 | (((long) bb[index + 4] & 0xff) << 24)
	                 | (((long) bb[index + 5] & 0xff) << 16)
	                 | (((long) bb[index + 6] & 0xff) << 8)
	                 | (((long) bb[index + 7] & 0xff) << 0));
	 }
	    
	 public static byte[] getNewByteByTwo(byte[] byte1, byte[] byte2){
		 
	      byte[] returnValue = new byte[byte1.length + byte2.length];
	      System.arraycopy( byte1, 0, returnValue, 0, byte1.length );
	      System.arraycopy( byte2, 0, returnValue, byte1.length, byte2.length );
	      return returnValue;
	 }
	 
	 public static int[] getNewIntByTwo(int[] byte1, int[] byte2){
		  if(byte1 == null && byte2 == null){
			  return null;
		  }
		  
		  if(byte1 == null){
			  return byte2;
		  }
		  
		  if(byte2 == null){
			  return byte1;
		  }
		  
	      int[] returnValue = new int[byte1.length + byte2.length];
	      System.arraycopy( byte1, 0, returnValue, 0, byte1.length );
	      System.arraycopy( byte2, 0, returnValue, byte1.length, byte2.length );
	      return returnValue;
	 }
	 
	 public static byte[] getNewByteByThree(byte[] byte1, byte[] byte2,byte[] byte3){
		 
	      byte[] returnValue = new byte[byte1.length + byte2.length + byte3.length];
	      System.arraycopy( byte1, 0, returnValue, 0, byte1.length );
	      System.arraycopy( byte2, 0, returnValue, byte1.length, byte2.length );
	      System.arraycopy( byte3, 0, returnValue, byte1.length + byte2.length, byte3.length );
	      return returnValue;
	 }
	    
	 public static int[] getNewIntByThree(int[] byte1, int[] byte2, int[] byte3){
		 
	      int[] returnValue = new int[byte1.length + byte2.length + byte3.length];
	      System.arraycopy( byte1, 0, returnValue, 0, byte1.length );
	      System.arraycopy( byte2, 0, returnValue, byte1.length, byte2.length );
	      System.arraycopy( byte3, 0, returnValue, byte1.length + byte2.length, byte3.length );
	      return returnValue;
	 }
	 
	 
	 public static String byte2hex(byte[] b) {
	      String hs="";
	      String stmp="";
	      for (int n=0 ;n < b.length;n ++) {
	       stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
	       
	       hs = hs + stmp;
	      }
	      return hs.toUpperCase();
	     }   
	    
	  public static void printHexString(String hint, byte[] b) {
	      for (int i = 0; i < b.length; i++) {
	       String hex = Integer.toHexString(b[i] & 0xFF);
	          if (hex.length() == 1) {
	           hex = '0' + hex;
	          }
	       }
	  }
	    
	  public static String bytes2HexString(byte[] b) {
	      String ret = "";
	      for (int i = 0; i < b.length; i++) {
	       String hex = Integer.toHexString(b[i] & 0xFF);
	       if (hex.length() == 1) {
	        hex = '0' + hex;
	       }
	       ret += hex.toUpperCase();
	      }
	      return ret;
	     }
	  
	  public static String bytes2HexStringIndex(byte[] b , int start , int lenth) {
	      String ret = "";
	      for (int i = start; i < start + lenth ; i++) {
	       String hex = Integer.toHexString(b[i] & 0xFF);
	       if (hex.length() == 1) {
	        hex = '0' + hex;
	       }
	       ret += hex.toUpperCase();
	      }
	      return ret;
	  }
	    
	 public static byte uniteBytes(byte src0, byte src1) {
	      byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
	      _b0 = (byte)(_b0 << 4);
	      byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
	      byte ret = (byte)(_b0 ^ _b1);
	      return ret;
	     }
	    
	 public static byte[] hexString2Bytes(String src){
	      byte[] ret = new byte[src.length()/2];
	      byte[] tmp = src.getBytes();
	      int len = src.length()/2;
	      for(int i=0; i<len; i++){
	       ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
	      }
	      return ret;
	 }   
	    
	 public static byte[] bytes2Bytes(byte[] b1,byte[] b2){
	      byte[] ret = new byte[b1.length+b2.length];
	      System.arraycopy(b1, 0, ret, 0, b1.length);
	      System.arraycopy(b2, 0, ret, b1.length, b2.length);
	      return ret;
	 }    
	    
	 public static byte[] copyBytes(byte[] b1,int start,int length){
	      byte[] ret = new byte[length];
	      if (start + length > b1.length) return null;      
	      System.arraycopy(b1, start, ret, 0, length);
	      return ret;
	 }   
	 
	 public static int[] copyInts(int[] b1,int start,int length){
	      int[] ret = new int[length];
	      if (start + length > b1.length) return null;      
	      System.arraycopy(b1, start, ret, 0, length);
	      return ret;
	 }  
	    
	 public static int indexOfBytes(byte[] b1,byte[] b2){
	      int ret = -1;
	      if (null == b1 || null == b2) return ret;
	      if (b1.length < b2.length) return ret;
	      byte[] newByte = new byte[b2.length];
	      for (int i = 0;i < b1.length;i++) {
	       newByte = copyBytes(b1,i,b2.length);
	       if (null != newByte && newByte.length == b2.length) {
	        int j=0;
	        for (j=0;j < newByte.length;j++) {
	         if (newByte[j] != b2[j]) break;
	        }
	        if (j == b2.length) {
	         ret = i;
	         break;
	        }
	       }
	      }   
	   return ret;
	 } 
	 
	 public static byte[] convertBytes(byte[] data){
		 
		 int len = data.length;
		 byte[] tmp = new byte[len];
		 
		 for(int i = 0; i < len; i++){
			 tmp[len-1-i] = data[i];
		 }
		 
		 return tmp;
		 
	 }
	 
	 public static int[] appendElementFromHead(int[] data , int element , int maxlenth){
		 
		 int dataLen;
		 if(data == null)
			return new int[]{element};
		 else
			 dataLen = data.length;
		 if(dataLen < maxlenth){
			 int[] retValue = new int[dataLen + 1];
			 System.arraycopy(data, 0, retValue, 1, dataLen);
			 retValue[0] = element;
			 return retValue;
		 }else{
			 int[] retValue = new int[dataLen];
			 System.arraycopy(data, 0, retValue, 1, dataLen-1);
			 retValue[0] = element;
			 return retValue;
		 }
		 		
	 }

}
