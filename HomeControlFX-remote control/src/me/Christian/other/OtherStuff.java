package me.Christian.other;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Random;

import me.Christian.pack.Main;

public class OtherStuff {
	public static Hashtable<String, String> UserCredicalsDatabase = new Hashtable<String, String>();
	public static Hashtable<String, Integer> UserPermissionsDatabase = new Hashtable<String, Integer>();
	public static Hashtable<String, String> PrivateKeys = new Hashtable<String, String>();

	public static String TheNormalTime(){
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy | HH:mm:ss | ");
		String time = sdf.format(cal.getTime());
		String currenttime =  time ;
		return currenttime;
	}
	public static String TheSimpleNormalTime(){
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time = sdf.format(cal.getTime());
		String currenttime =  time ;
		return currenttime;
	}

	public static void addToPrintQueue(String string){
		int x = 0;
		while(Main.todoprint[x] != ""){
			x++;
		}
		Main.todoprint[x] = string;
		Main.todoprintsize = x;
	}

	public static void addToCmdQueue(String string){
		int x = 0;
		while(Main.todocmd[x] != ""){
			x++;
		}
		Main.todocmd[x] = string;
		Main.todocmdsize = x;
	}

	public static void initDatabase(){
		// Just Test Accounts, data will be decryted in a database later
		UserCredicalsDatabase.put("Totenfluch", "s123C");
		UserPermissionsDatabase.put("Totenfluch", 1000);
		
		UserCredicalsDatabase.put("Soulrescuer", "s321C");
		UserPermissionsDatabase.put("Soulrescuer", 1000);
		
		UserCredicalsDatabase.put("root", Main.MasterPassword);
		UserPermissionsDatabase.put("root", 1000);
	}

	public static String GeneratePrivateKey(){
		Random r = new Random(); 
		String a = "";
		for(int i=0; i<32; i++){
			final char randomChar = (char) (33 + r.nextInt(91));
			a += randomChar;
		}
		return a;
	}
	
	public static File jarlocation(){
		return new File(OtherStuff.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	}
	
	public static String getMacAdress(){
		String ComputerMac = null;
		try{
			InetAddress ComputerIP = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ComputerIP);
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
			ComputerMac = sb.toString();

		}catch ( Exception e){
			e.printStackTrace();
		}
		return ComputerMac;
	}

}
