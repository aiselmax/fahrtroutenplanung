package backend;

import java.net.*;
import java.util.concurrent.*;
import java.io.*;

/**
 * 
 * @author Dennis
 * Klasse Server verwaltet einkommende Clientanfragen.
 */
public class Server {
	Boolean test=false;
	static ServerSocket server;
	/**
	 * 
	 * @param arg
	 * Server läuft dauerhaft und übergibt einkommende Anfragen an Handler-Threads.
	 */
	public static void main(String arg[]){
		
		ExecutorService executor = Executors.newFixedThreadPool(30);
		
		try {
			server = new ServerSocket(3141);
			System.out.println("Server wurde gestartet...");
			while(true)
			{
				
				try
				{
					
					Socket client =server.accept();
					Thread t= new Thread(new Handler(client));
					t.start();
					
					//executor.execute(new Handler(client));
				}
				catch(IOException e){
					e.printStackTrace();
				}
				
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	
	public void testArea(){
		
		
		ExecutorService executor = Executors.newFixedThreadPool(30);
		try {
			server = new ServerSocket(3141);
			System.out.print("Server wurde gestartet...");
			while(test)
			{
				
				try
				{
					
					Socket client =server.accept();
					Thread t= new Thread(new Handler(client));
					t.start();
					
					//executor.execute(new Handler(client));
				}
				catch(IOException e){
					e.printStackTrace();
				}
				
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
