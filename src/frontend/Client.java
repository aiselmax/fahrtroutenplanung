package frontend;

import java.net.*;
import java.util.Objects;

import DirectedGraph.DirectedGraph;
import DirectedGraph.Vertex;

import java.io.*;

/**
 * 
 * @author Dennis
 * Baut Verbindung zum Server/Handler auf, öffnet Kommikationsschnittstellen
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
	 * Der Kontruktor öffnet die Ein- und Ausgabe-Streams für Strings
	 * sowie die Ein- und Ausgabe-Streams für Objekte
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
	 * Fügt ein neues Lager dem Graphen hinzu
	 * @param s zuübergebendes Lager 
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
	 * Frägt beim Server die Lagerliste und gibt die erhaltene Liste zurück
	 * @return gibt ein String[]-Objekt mit allen Lagern zurück
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
	 * Graphen Abfragen, vorher muss Server wissen, welcher Graph benötigt wird.
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
	 * Lässt eine Route ausgehend von einem übergebenen Knoten berechnen
	 * und liefert diese zurück.
	 * @param start Zuübergebender Startpunkt.
	 * @return gibt berechnete Route zurück.
	 * @throws IOException
	 */
	public String[] getRoute(String start) throws IOException {
		
		out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
		out.write("getRoute\r\n"); //Funktionsauswahl auf Server
		out.close();
	
		oout=new ObjectOutputStream(server.getOutputStream());
		oout.writeObject(start); //Startknoten übermittelteln
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
	 * Fügt eine Verbindung zwischen zwei Knoten hinzu.
	 * Die Verbindung wird in Kilometern angegeben.
	 * @param a Erster Knoten wird als String übergeben.
	 * @param b Zeiter Knoten wird als String übergeben.
	 * @param km Verbindung wird als int in km übergeben.
	 * @throws IOException
	 */
	public void addVerbindung(String a, String b, String km) throws IOException {
		
		out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
		out.write("addVerbindung\r\n"); //Funktionsauswahl auf Server
		out.close();
		oout=new ObjectOutputStream(server.getOutputStream());
		oout.writeObject(a); //Startknoten übermittelteln
		oout.close();
		oout=new ObjectOutputStream(server.getOutputStream());
		oout.writeObject(b);		//Server mitteielen, dass Endknoten übermitteltelt wird
		oout.close();
		oout=new ObjectOutputStream(server.getOutputStream());
		oout.writeObject(km); //Endknoten übermittelteln
		oout.close();
		
		
		return;
	}
	
	/**
	
	
	
	/**
	 * Sendet Befehl zum beenden des Handlers und schließt alle Client-Verbindungen.
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
