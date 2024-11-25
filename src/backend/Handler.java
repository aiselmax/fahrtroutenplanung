package backend;

import java.io.*;
import java.net.*;

import DirectedGraph.DirectedGraph;
import DirectedGraph.Vertex;
import backend.Tool;

public class Handler implements Runnable {
	private static final String GET_LAGER = "getLager";
	private static final String ADD_VERBINDUNG = "addVerbindung";
	private static final String GET_ROUTE = "getRoute";
	private static final String CREATELAGER = "createlager";
	private static final String UPDATE = "update";

	private Socket client;

	String s = new String("Hallo Welt!");
	String lager;
	String start = null;

	ObjectOutputStream out;
	ObjectInputStream in;
	BufferedReader question;
	BufferedWriter answer;

	Vertex end = null;
	String[] allLager = null;
	Tool tool = new Tool();

	public Handler(Socket client) {
		this.client = client;
		// new Thread(this).start(); //Test
	}
/**
 * run() läuft bei Verbindung mit einem Client in Schleife, nimmt die Anfragen entgegen und Funktionsanfragen übergibt an engine()
 *
 */
	@Override
	public void run() {
		try {
			System.out.print("Handler wurde gestartet...");
		
			answer = new BufferedWriter(new OutputStreamWriter(
					client.getOutputStream()));

			int check = 0;
			// /Funktionen
			while (true) {
				question.ready();
				question = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String t = question.readLine();
				question.close();
				if (t == null || t.equals("close")) {
					break;
				}
				engine(t);

			}

		//	in.close();
		//	out.close();
			answer.close();
			client.close();
			question.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
/**
 * engine() übergibt die ankommende Anfrage und wählt die dazugehörige Funktion aus.
 * @param t Funktionsauswahl
 */
	private void engine(String t) {
		System.out.println("calling: " + t);

		if (t != null) {
			switch (t) {
			case UPDATE:
				// send Graph
				break;

			case GET_ROUTE:
				try {
					in = new ObjectInputStream(client.getInputStream());
					start = (String) in.readObject();
					in.close();
					String[] route = tool.getShortestPath(start);
					out = new ObjectOutputStream(client.getOutputStream());
					out.writeObject(route);
					out.close();
					System.out.println("case: Get Route");
				} catch (ClassNotFoundException e1) {

					e1.printStackTrace();
				} catch (IOException e1) {

					e1.printStackTrace();
				}
				break;

			case GET_LAGER:
				allLager = tool.getLagerliste();
				try {
					out = new ObjectOutputStream(client.getOutputStream());
					out.writeObject(allLager);
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("case getLager:");

				break;

			case CREATELAGER:
				System.out.println("case createLager:");

				// answer.write("ok");
				try {
					in = new ObjectInputStream(client.getInputStream());
					lager = (String) in.readObject();
					in.close();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tool.addLager(lager);
				System.out.println("case createLager: beenden");
				break;

			case ADD_VERBINDUNG:
				
				if (tool.getError() != null)
					System.out.println(tool.getError());
				// answer.write("ok");
				try {
					in = new ObjectInputStream(client.getInputStream());
					String a = (String) in.readObject();
					in.close();
					in = new ObjectInputStream(client.getInputStream());
					String b = (String) in.readObject();
					in.close();
					in = new ObjectInputStream(client.getInputStream());
					String skm = (String) in.readObject();
					in.close();
					int km= Integer.parseInt(skm);
					tool.addVerbindung(a, b, km);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;

			// Ende Switch
			}
		}

		// /Ende

	}

}

