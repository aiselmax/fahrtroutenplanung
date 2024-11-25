package frontend;

import java.net.*;
import java.util.Objects;

import DirectedGraph.DirectedGraph;
import DirectedGraph.Vertex;

import java.io.*;

/**
 * 
 * @author Dennis
 * Baut Verbindung zum Server/Handler auf, �ffnet Kommikationsschnittstellen
 * und liefert die Funktionen zur Kommikation zwischen Userinterface
 * und und Server.
 */

public class Client{
	static Socket server=null;	
	BufferedWriter out;
	
	ObjectOutputStream oout;
	ObjectInputStream oin;
	//Konstruktor/verbindung aufbauen
	/**
	 * Der Kontruktor �ffnet die Ein- und Ausgabe-Streams f�r Strings
	 * sowie die Ein- und Ausgabe-Streams f�r Objekte
	 * 
	 */
	public Client(){
		try
		{
			
			server= new Socket("localhost", 3141);		
		}
		catch(UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * F�gt ein neues Lager dem Graphen hinzu
	 * @param s zu�bergebendes Lager 
	 * @throws IOException
	 */
	
	public void addLager(String s) throws IOException{
	
		
		//out.write("\r");
		out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
		out.write("createlager\r"); //Funktionsauswahl--Server mitteilen, dass createlager erwartet wird
		out.close();
		oout=new ObjectOutputStream(server.getOutputStream());
		oout.writeObject(s);
		oout.close();
		
	}
	
	/**
	 * Fr�gt beim Server die Lagerliste und gibt die erhaltene Liste zur�ck
	 * @return gibt ein String[]-Objekt mit allen Lagern zur�ck
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public String[] getLager() throws IOException, ClassNotFoundException{
	
		String[] lagerList;
		out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
		out.write("getLager\r"); //Funktionsauswahl auf Server
		out.close();
//		while(oin.available()!=0){
			try {
				oin=new ObjectInputStream(server.getInputStream());
				lagerList=(String[])oin.readObject();
				oin.close();
				return lagerList;
			} catch (ClassNotFoundException e) {
				 e.printStackTrace();
//			       
			}
//					
//		}
		String[] test = (String[])oin.readObject();	
		return test;
	}
	
	/**
	 * Bisher nur update von Graph (noch nichts getestet)
	 * @return
	 */
	public DirectedGraph update(){
		DirectedGraph graph = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
			out.write("update\r"); //Funktionsauswahl--Server mitteilen, dass update erwartet wird
			out.close();
			graph = getGraph(); //Graph empfangen
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return graph;
	}
	
	/**
	 * Graphen Abfragen, vorher muss Server wissen, welcher Graph ben�tigt wird.
	 * @return Directed Graph
	 * @throws IOException
	 */
	public DirectedGraph getGraph() throws IOException {
		oin=new ObjectInputStream(server.getInputStream());
		DirectedGraph graph=null;
		while(oin.available()!=0){
			try {
				graph=(DirectedGraph)oin.readObject();
			
				return graph;
			} catch (ClassNotFoundException e) {
				 e.printStackTrace();
			        continue;
			}
					
		}
		oin.close();
		return null;
	}
	/**
	 * L�sst eine Route ausgehend von einem �bergebenen Knoten berechnen
	 * und liefert diese zur�ck.
	 * @param start Zu�bergebender Startpunkt.
	 * @return gibt berechnete Route zur�ck.
	 * @throws IOException
	 */
	public String[] getRoute(String start) throws IOException {
		
		out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
		out.write("getRoute\r\n"); //Funktionsauswahl auf Server
		out.close();
	
		oout=new ObjectOutputStream(server.getOutputStream());
		oout.writeObject(start); //Startknoten �bermittelteln
		oout.close();
		String[] route = null;
		try {
			oin=new ObjectInputStream(server.getInputStream());
			route = (String[])oin.readObject();
			oin.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//Route empfangen
		
		return route;
	}
	/**
	 * F�gt eine Verbindung zwischen zwei Knoten hinzu.
	 * Die Verbindung wird in Kilometern angegeben.
	 * @param a Erster Knoten wird als String �bergeben.
	 * @param b Zeiter Knoten wird als String �bergeben.
	 * @param km Verbindung wird als int in km �bergeben.
	 * @throws IOException
	 */
	public void addVerbindung(String a, String b, String km) throws IOException {
		
		out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
		out.write("addVerbindung\r\n"); //Funktionsauswahl auf Server
		out.close();
		oout=new ObjectOutputStream(server.getOutputStream());
		oout.writeObject(a); //Startknoten �bermittelteln
		oout.close();
		oout=new ObjectOutputStream(server.getOutputStream());
		oout.writeObject(b);		//Server mitteielen, dass Endknoten �bermitteltelt wird
		oout.close();
		oout=new ObjectOutputStream(server.getOutputStream());
		oout.writeObject(km); //Endknoten �bermittelteln
		oout.close();
		
		
		return;
	}
	
	/**
	
	
	
	/**
	 * Sendet Befehl zum beenden des Handlers und schlie�t alle Client-Verbindungen.
	 * 
	 */
	public void Close(){
		try {
			out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
			out.write("close\r");
			out.close();
			server.close();	
			out.close();
			oout.close();
			oin.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
