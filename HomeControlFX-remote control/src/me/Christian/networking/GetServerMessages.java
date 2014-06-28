package me.Christian.networking;

import me.Christian.other.OtherStuff;
import me.Christian.pack.Main;




public class GetServerMessages{
	public static String newestreply = null;


	public static void CheckServerMessages(String message){
		String[] temp = message.split(" ");
		if(message.startsWith("/PrivateKey")){
			Main.PrivateKey = temp[1];
		}
		OtherStuff.addToPrintQueue(OtherStuff.TheNormalTime() + " SERVER: " + message);
	}
}
