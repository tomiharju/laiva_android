package com.me.Battleships;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class PortListener implements Runnable {
	Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;
	
	public void run(){
		
		try{
			 socket = new Socket("192.168.1.239", 8888);
	         dataOutputStream = new DataOutputStream(socket.getOutputStream());
	         dataInputStream = new DataInputStream(socket.getInputStream());
	        
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		
	}

}
