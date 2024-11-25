package backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import DirectedGraph.*;

/**
 * Klasse Tool verwaltet die Fahrtroutenplanung
 *
 */

public class Tool {
	private DirectedGraph g = null;
	private Vector<Lager> lagerliste = new Vector<Lager>();
	private static int edgeCount = 0;
	private String error = null;
	private boolean rTinitialised = false;

	/**
	 * Constructor.
	 */
	public Tool() {
		initLagerliste();

		Enumeration<Edge> enu = getGraph().edges();
		while (enu.hasMoreElements()) {
			enu.nextElement();
			edgeCount++;
		}
	}

	/**
	 * Liefert mittels der Deserialisierung auf dem Server bereits befindlichen
	 * Graphen zurück oder erstellt einen neuen Graphen, falls der Graph nicht
	 * existiert
	 * 
	 * @return DirectedGraph - der gerichtete Graph
	 */
	private DirectedGraph getGraph() {

		if (g == null) {
			g = new DirectedGraph();
			InputStream fis = null;
			try {
				fis = new FileInputStream("graph.ser");
				ObjectInputStream in = new ObjectInputStream(fis);
				g = (DirectedGraph) in.readObject();
				in.close();

			} catch (IOException e) {
				/// Hier kommt neuer graph rein.
				setGraph(g);
				System.err.println(e);
				return g;
			} catch (ClassNotFoundException e) {
				System.err.println(e);
			} finally {
				try {
					fis.close();
					return g;
				} catch (Exception e) {
				}
			}
		}
		return g;
	}

	/**
	 * Serialisiert den gerichteten Graphen
	 * 
	 * @param graph
	 *            - zu serialiserender Graph
	 */
	private static void setGraph(DirectedGraph graph) {

		OutputStream fos = null;
		File file = new File("graph.ser");

		try {

			if (file.exists()) {
				file.delete();
			}
			fos = new FileOutputStream(file);
			ObjectOutputStream o = new ObjectOutputStream(fos);
			o.writeObject(graph);
			o.flush();
			o.close();

		} catch (IOException e) {
			System.err.println(e);

		} finally {
			try {
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Klasse Route speichert einzelne Routen
	 */
	class Route {
		Vector<Lager> durch = new Vector<Lager>();
		int kosten = 0;
		String name;
		String[] weg;
		Lager ziel;
		Lager quelle;

		/**
		 * 1.Konstruktor der Klasse Route
		 * 
		 * @param ziel
		 *            - Ziellager
		 * @param quelle
		 *            - Quelllager
		 */
		public Route(Lager ziel, Lager quelle) {
			this.name = ziel.name;
			this.kosten = 0;
			this.weg = new String[1];
			this.weg[0] = this.name;
			this.ziel = ziel;
			this.quelle = quelle;
		}

		/**
		 * 2.Konstruktor der Klasse Route
		 * 
		 * @param ziel
		 *            - Ziellager
		 * @param quelle
		 *            - Quelllager
		 * @param liste
		 *            - Vector mit allen Zwischenlagern
		 */
		public Route(Lager ziel, Lager quelle, Vector<Lager> liste) {
			this.name = ziel.name;
			this.kosten = 0;
			this.ziel = ziel;
			this.quelle = quelle;

			for (Lager l : liste) {
				durch.addElement(l);
			}
			this.weg = new String[1 + durch.size()];
			for (int i = 0; i < durch.size(); i++)
				this.weg[i] = durch.get(i).name;
			this.weg[durch.size()] = this.name;
		}

		/**
		 * Ändert eine direkte Route
		 * 
		 * @param ziel
		 *            - Ziellager
		 * @param kosten
		 *            - Entfernung in km
		 */
		public void setRoute(Lager ziel, int kosten) {
			this.ziel = ziel;
			this.name = ziel.name;
			this.kosten = kosten;
		}

		/**
		 * Ersetzt eine Route durch eine neue indirekte Route
		 * 
		 * @param liste
		 *            - Vector mit allen Zwischenlagern
		 * @param kosten
		 *            - Entfernung in km
		 */
		public void newRoute(Vector<Lager> liste, int kosten) {

			this.kosten = kosten;
			durch.removeAllElements();
			for (Lager l : liste) {
				durch.addElement(l);
			}
			this.weg = new String[1 + durch.size()];
			for (int i = 0; i < durch.size(); i++)
				this.weg[i] = durch.get(i).name;
			this.weg[durch.size()] = this.name;
		}
	}

	/**
	 * Verwaltet Routingtabellen
	 */
	class RouteTable {
		Lager quelle;
		Vector<Route> route = new Vector<Route>();

		/**
		 * Konstruktor der Klasse RouteTable
		 * 
		 * @param quelle
		 *            - Quelllager
		 * @param liste
		 *            - Legt eine neue Route zwischen Quelllager und allen
		 *            Lagern aus dieser Liste
		 */
		public RouteTable(Lager quelle, Vector<Lager> liste) {
			this.quelle = quelle;
			Route r;
			int abstand;

			for (Lager l : liste) {
				if (quelle.name != l.name) {
					r = new Route(l, quelle);
					route.addElement(r);

					for (Verbindung v : quelle.ausgehende) {
						abstand = v.km;
						if (v.ziellager.name == l.name && (r.kosten == 0 || abstand < r.kosten))
							route.get(route.indexOf(r)).setRoute(l, v.km);
					}
				}
			}
		}

		/**
		 * Liefert eine Liste mit allen Zwischenlager auf dem Weg zur gesuchten
		 * Lager zurück
		 * 
		 * @param l
		 *            - Ziellager
		 * @return eine Vector-liste mit allen Zwischenlager
		 */
		public Vector<Lager> getPathTo(Lager l) {
			Vector<Lager> tmp = new Vector<Lager>();
			for (Route r : route) {
				if (r.ziel.name == l.name) {
					tmp.addAll(r.durch);
					tmp.addElement(r.ziel);
					return tmp;
				}
			}
			return tmp;
		}

		/**
		 * Liefert eine Route zum Ziellager zurück
		 * 
		 * @param l
		 *            - Ziellager
		 * @return eine Route zum Ziellager oder null falls solche Route nicht
		 *         existiert
		 */
		public Route getRoute(Lager l) {
			for (Route r : route) {
				if (r.ziel.name == l.name) {
					return r;
				}
			}
			return null;
		}

		/**
		 * Liefert Distanz zum Ziellager zurück
		 * 
		 * @param l
		 *            - Ziellager
		 * @return int Distanz in km oder 0 falls keine Route zum Ziellager
		 *         führt oder -1 falls Ziellager nicht existiert
		 */
		public int getDistanceTo(Lager l) {
			int tmp = -1;
			for (Route r : route) {
				if (r.ziel.name == l.name)
					return r.kosten;
			}
			return tmp;
		}
	}

	/**
	 * Klasse Lager stellt einen Knoten im Graph dar
	 */
	static class Lager extends Vertex {

		String name;
		int id;
		Vector<Verbindung> ausgehende = new Vector<Verbindung>();
		RouteTable table;

		/**
		 * Konstuktor der Klasse
		 * 
		 * @param name
		 *            - Lagerbezeichnung
		 */
		public Lager(String name) {
			this.name = name;

		}

		/**
		 * Liefer Lagerbezeichnung zurück
		 * 
		 * @return
		 */
		public String getName() {
			return name;
		}

		/**
		 * Setzt Routingtabelle für desen Lager
		 * 
		 * @param table
		 *            - Routingtabelle
		 */
		public void setRouteTable(RouteTable table) {
			this.table = table;
		}

		/**
		 * @param lager
		 *            - Ziellager
		 * @return Verbindung zu dem Ziellager oder NULL, falls es keine
		 *         Verbindung zu dem Lager existiert
		 */
		public Verbindung getVerbindungTo(Lager lager) {
			Verbindung v = null;
			for (Verbindung tmp : ausgehende) {
				if (tmp.ziellager == lager)
					return tmp;
			}
			return v;
		}
	}

	/**
	 * Klasse Lager stellt eine Kante im Graph dar
	 */
	static class Verbindung extends Edge {

		String name;
		int km;
		Lager ziellager;
		Lager quellelager;

		/**
		 * Konstuktor der Klasse
		 * 
		 * @param name
		 *            - Verbingungsbezeichnung
		 * @param distance
		 *            - Disntanz zwischen zwei Lagern in km
		 * @param quelle
		 *            - Quelllager
		 * @param ziel
		 *            - Ziellager;
		 */
		public Verbindung(String name, int distance, Lager quelle, Lager ziel) {

			this.name = name;
			this.km = distance;
			this.ziellager = ziel;
			this.quellelager = quelle;
			quelle.ausgehende.add(this);
		}

	}

	/**
	 * Fügt einen neuen Lager zu dem Graphen hinzu oder setzt eine
	 * Fehlermeldung, falls Lager schon existiert
	 * 
	 * @param name
	 *            - Lagerbezeichnung
	 */
	public void addLager(String name) {
		if (getLagerByName(name) == null) {
			Lager l = new Lager(name);
			lagerliste.addElement(l);
			getGraph().addVertex(l);
			l.id = getGraph().numberOfVertices();
			setGraph(getGraph());
			initLagerliste();
			this.error = null;
			this.rTinitialised = false;
		} else {
			setError(3, name);
		}
	}

	/**
	 * Ändert Lagerbezeihnung oder setzt eine Fehlermeldung, falls Lager nicht
	 * existiert
	 * 
	 * @param name
	 *            - alte Lagerbeizeihnung
	 * @param newName
	 *            - neue Lagerbeizeichnung
	 */
	public void editLager(String name, String newName) {
		Lager l = getLagerByName(name);
		if (l != null) {
			l.name = newName;
			initLagerliste();
			this.rTinitialised = false;
		} else
			setError(1, name);
	}

	/**
	 * Löscht einen Lager sowie seine eingehende und ausgehende Verbindungen oder
	 * setzt eine Fehlermeldung, falls Lager nicht existiert
	 * 
	 * @param name
	 *            - Lagerbeizeihnung
	 */
	public void delLager(String name) {

		Lager l = getLagerByName(name);
		if (l != null) {
			getGraph().removeVertex(l);
			setGraph(getGraph());
			initLagerliste();
			this.rTinitialised = false;
		} else
			setError(1, name);
	}

	/**
	 * Liefert einen Lager aus Lagerliste zurück
	 * 
	 * @param name
	 *            - Lagerbezeichnung
	 * @return Lager oder NULL falls ein Lager mit dieser Bezeichnung nicht
	 *         exitiert
	 */
	public Lager getLagerByName(String name) {
		Lager tmp = null;
		for (Lager l : lagerliste)
			if (l.name.equals(name)) {
				tmp = l;
				return tmp;
			}
		return tmp;
	}

	/**
	 * Fügt eine neue Verbindung zwischen zwei Lager im Graphen hinzu. Falls die
	 * Verbindung zwischen Quelllager und Ziellagern schon existiert wird sie
	 * überschrieben. Setzt eine Fehlermeldung, falls Quelllager/Ziellager nicht
	 * existiert oder Distantz <1 ist
	 * 
	 * @param out
	 *            Quelllagerbezeichnung
	 * @param in
	 *            Ziellagerbezeichnung
	 * @param km
	 *            Distanz in km
	 */
	public void addVerbindung(String out, String in, int km) {
		this.error = null;
		Lager quelle = getLagerByName(out);
		Lager ziel = getLagerByName(in);
		if (quelle == null)
			setError(1, out);
		if (ziel == null)
			setError(1, in);
		if (km <= 0)
			setError(4, null);

		if (this.error == null) {
			Verbindung v = quelle.getVerbindungTo(ziel);
			if (v != null)
				delVerbindung(quelle, ziel, v);
			getGraph().addEdge(quelle, ziel, new Verbindung("v" + Integer.toString(edgeCount), km, quelle, ziel));
			edgeCount++;
			setGraph(getGraph());
			this.rTinitialised = false;
		}
	}

	/**
	 * Löscht Verbindung zwischen beiden Lagern
	 * 
	 * @param quelle
	 *            - Quelllager
	 * @param ziel
	 *            - Ziellager
	 * @param v
	 *            - Verbindung zwischen beiden Lagern
	 */
	private void delVerbindung(Lager quelle, Lager ziel, Verbindung v) {
		quelle.ausgehende.remove(v);
		getGraph().removeEdge(getGraph().ensureEdge(quelle, ziel));
		Enumeration<Verbindung> enuEdges = getGraph().edges();
		edgeCount = 0;
		while (enuEdges.hasMoreElements()) {
			Verbindung tmp = enuEdges.nextElement();
			tmp.name = "v" + Integer.toString(edgeCount);
			edgeCount++;
		}
	}

	/**
	 * Intialisiert RouteTable für alle Lager Setzt eine Fehlermeldung, falls:
	 * Lagerliste weniger als 2 Lager hat oder ein Lager aus der Lagerliste zu
	 * wenig Verbindungen hat
	 * 
	 * @param liste
	 */
	private void initRouteTable() {
		this.error = null;
		if (lagerliste.size() > 1) {
			if (checkDeadEnd() == null) {
				for (Lager l : lagerliste) {
					RouteTable table = new RouteTable(l, lagerliste);
					l.setRouteTable(table);
				}
				String err = berechneRouteTable();
				if (err != null)
					setError(6, err);
				else
					this.rTinitialised = true;
			}
		} else
			setError(2, null);
	}

	/**
	 * Prüft ob Sackgasse im Graph exitieren
	 * 
	 * @return Fehlermeldung oder NULL, falls keine Sackgassen existieren
	 */
	private String checkDeadEnd() {
		String str = null;
		for (Lager l : lagerliste) {
			if (l.ausgehende.size() == 0) {
				str = l.name;
				setError(5, l.name);
				return str;
			}
		}
		return str;
	}

	/**
	 * Berechnet Routingtabellen von allen Lagern mit dem Algorithmus von
	 * Dijkstra
	 * 
	 * @return Fehlermeldung oder NULL beim Erfolg
	 */
	private String berechneRouteTable() {
		String err = null;
		for (Lager l : lagerliste) {
			err = dijkstra(l);
			if (err != null)
				return err;
		}
		return err;
	}

	/**
	 * Der Algorithmus von Dijkstra berechnet einen kürzesten Pfad zwischen dem
	 * gegebenen Startlager und übrigen Lagern
	 * 
	 * @param start
	 *            - Strartlager
	 * @return Fehlermeldung, falls ein Lager zu wenig Verbindungen hat oder
	 *         NULL beim Erfolg
	 */
	private String dijkstra(Lager start) {
		Vector<Lager> liste = new Vector<Lager>();
		liste.addAll(lagerliste);
		Vector<Lager> toDo = new Vector<Lager>();
		Vector<Lager> durch = new Vector<Lager>();
		int d[] = new int[liste.size()];
		Lager parent[] = new Lager[liste.size()];
		int distanz;
		Lager akt, tmp;
		Route r;

		for (int i = 0; i < d.length; i++) {
			d[i] = Integer.MAX_VALUE;
			parent[i] = null;
		}
		d[liste.indexOf(start)] = 0;
		parent[liste.indexOf(start)] = start;

		toDo.addElement(start);

		while (toDo.isEmpty() == false) {
			akt = toDo.firstElement();
			toDo.removeElementAt(0);
			for (Verbindung v : akt.ausgehende) {
				distanz = d[liste.indexOf(akt)] + v.km;

				if (toDo.contains(v.ziellager) && d[liste.indexOf(v.ziellager)] > distanz) {
					d[liste.indexOf(v.ziellager)] = distanz;
					parent[liste.indexOf(v.ziellager)] = akt;
				}
				else if (parent[liste.indexOf(v.ziellager)] == null) {
					d[liste.indexOf(v.ziellager)] = distanz;
					parent[liste.indexOf(v.ziellager)] = akt;
					toDo.addElement(v.ziellager);
				}
			}
		}

		liste.removeElement(start);

		String str = null;
		for (Lager l : liste) {
			tmp = parent[lagerliste.indexOf(l)];
			if (tmp == null) {
				str = l.name;
				return str;
			} else {
				while (tmp != start) {
					durch.add(0, tmp);
					tmp = parent[lagerliste.indexOf(tmp)];
				}
				r = start.table.getRoute(l);
				r.newRoute(durch, d[lagerliste.indexOf(l)]);
				durch.clear();
			}
		}
		return str;
	}

	/**
	 * Berechnet Entfernung zwischen allen Lagern aus der gegebenen Liste
	 * 
	 * @param liste
	 *            - Vector-liste mit Lagern
	 * @return int - Entfernung in km oder 0 falls die Liste leer ist;
	 */
	private int berechneEntfernung(Vector<Lager> liste) {
		int entfernung = 0;
		Lager a, b;
		if (liste.size() > 1) {
			for (int i = 0; i < liste.size() - 1; i++) {
				a = liste.get(i);
				b = liste.get(i + 1);
				entfernung += a.table.getDistanceTo(b);
			}
		}
		return entfernung;
	}

	/**
	 * Rekursive Suche nach allen möglichen Routen, die alle Lager verbinden
	 * 
	 * @param start
	 *            - Startlager
	 * @param doneList
	 *            - bereits abgearbeitete Lager
	 * @param toDoList
	 *            - noch nicht abgearbeitete Lager
	 * @param routen
	 *            - temporäre Routen die alle Lager verbinden
	 * @return - Routen die alle Lager verbinden
	 */
	private Vector<Route> verbindeAlle(Lager start, Vector<Lager> doneList, Vector<Lager> toDoList, Vector<Route> routen) {
		Vector<Lager> done = new Vector<Lager>();
		Vector<Lager> toDo = new Vector<Lager>();
		Vector<Lager> tmpListe = new Vector<Lager>();

		Route neueRoute;
		done.addAll(doneList);
		if (done.isEmpty() || done.lastElement() != start)
			done.addElement(start);

		toDo.addAll(toDoList);
		toDo.removeElement(start);

		if (routen == null)
			routen = new Vector<Route>();

		if (toDo.size() == 0) {
			Vector<Lager> durch = new Vector<Lager>();
			durch.addAll(done.subList(1, done.size() - 1));
			neueRoute = new Route(done.lastElement(), done.firstElement(), durch);
			neueRoute.kosten = berechneEntfernung(done);

			routen.addElement(neueRoute);
		}

		if (toDo.isEmpty() == false && verbundet(lagerliste, done) == false) {
			for (Lager l : toDo) {
				tmpListe.addAll(start.table.getPathTo(l));
				if (done.isEmpty() == false) {
					if (done.lastElement() == tmpListe.firstElement())
						tmpListe.removeElementAt(0);

					done.addAll(tmpListe);
				}

				verbindeAlle(l, done, toDo, routen);

				for (int i = tmpListe.size(); i > 0; i--)
					done.removeElementAt(done.size() - 1);

				tmpListe.clear();
			}
		}
		return routen;
	}

	/**
	 * Liefert kürzeste Route, die alle Lager ausgehend vom Startlager
	 * verbindet, zurück
	 * 
	 * @param lager
	 *            - Startlagerbezeichnung
	 * @return String[] path - kürzeste Route, letztes Element in der LIst ist
	 *         (Distanz in km)
	 */
	public String[] getShortestPath(String lager) {
		this.error = null;
		Lager start = getLagerByName(lager);
		Route tmp = null;

		if (rTinitialised == false)
			initRouteTable();

		if (this.error == null && rTinitialised) {
			int entfernung = Integer.MAX_VALUE;
			Vector<Route> liste = new Vector<Route>();
			Vector<Lager> test = new Vector<Lager>();
			liste.addAll(verbindeAlle(start, test, lagerliste, null));
			for (Route r : liste) {
				if (r.kosten < entfernung) {
					entfernung = r.kosten;
					tmp = r;
				}
			}
			String path[] = new String[tmp.weg.length + 2];
			path[0] = tmp.quelle.name;
			for (int i = 0; i < tmp.weg.length; i++)
				path[i + 1] = tmp.weg[i];
			path[tmp.weg.length + 1] = "(" + tmp.kosten + "km)";
			return path;
		}
		return null;

	}

	//
	/**
	 * Überprüft ob 2.Liste alle Lager aus der 1.List beinhaltet
	 * 
	 * @param volleListe
	 *            - 1.Liste
	 * @param teilListe
	 *            - 2.Liste
	 * @return true falls ja, sonst false
	 */
	private boolean verbundet(Vector<Lager> volleListe, Vector<Lager> teilListe) {
		if (teilListe.containsAll(volleListe))
			return true;
		else
			return false;

	}

	/**
	 * Initialisiert Vector Lagerliste mit den Knoten aus dem Graph
	 */
	private void initLagerliste() {
		lagerliste = new Vector<Lager>();
		Enumeration<Lager> enu = getGraph().vertices();
		while (enu.hasMoreElements()) {
			Lager l = enu.nextElement();
			lagerliste.addElement(l);
		}
	}

	/**
	 * Liefert Liste mit den Bezeichnung von allen Lagern
	 * 
	 * @return String[]
	 */
	public String[] getLagerliste() {
		String[] liste = new String[lagerliste.size()];
		for (int i = 0; i < lagerliste.size(); i++)
			liste[i] = lagerliste.get(i).getName();
		return liste;
	}

	/**
	 * Setzt eine Fehlermeldung
	 * 
	 * @param errorNr
	 *            - Fehlernummer
	 * @param cause
	 *            - Fehlerverursacher
	 */
	private void setError(int errorNr, String cause) {
		switch (errorNr) {
		case 1:
			this.error = "Lager: " + cause + " wurde nicht gefunden";
			break;
		case 2:
			this.error = "Lagerliste hat weniger als 2 Lager";
			break;
		case 3:
			this.error = "Lager: " + cause + " exitiert schon";
			break;
		case 4:
			this.error = "Disntanz in km muss größer als 0 sein";
			break;
		case 5:
			this.error = "Lager: " + cause + " ist eine Sackgasse ";
			break;
		case 6:
			this.error = "Lager: " + cause + " hat zu wenig Verbindungen";
			break;
		default:
			break;
		}
	}

	/**
	 * Liefert aktuelle Fehlermeldung zurück
	 * 
	 * @return String - Fehlermeldung
	 */
	public String getError() {
		return this.error;
	}

//	public static void main(String[] args) {
//
//// ----------------Beispiel der Benutzung------------------
//	// ---- 0) Tool erzeugen
//		Tool tool = new Tool();
//	
//	// ---- 1) Lager hinzufügen
//		tool.addLager("A"); if (tool.getError() != null) System.out.println(tool.getError());
//		tool.addLager("B"); if (tool.getError() != null) System.out.println(tool.getError());
//		tool.addLager("C"); if (tool.getError() != null) System.out.println(tool.getError());
//		tool.addLager("D"); if (tool.getError() != null) System.out.println(tool.getError());
//	
//	// ----- 2) Verbindungen hinzufügen
//		tool.addVerbindung("A", "B", 1); if (tool.getError() != null) System.out.println(tool.getError());
//		tool.addVerbindung("A", "C", 1); if (tool.getError() != null) System.out.println(tool.getError());
//		tool.addVerbindung("B", "C", 1); if (tool.getError() != null) System.out.println(tool.getError());
//		tool.addVerbindung("B", "D", 1); if (tool.getError() != null) System.out.println(tool.getError());
//		tool.addVerbindung("C", "A", 1); if (tool.getError() != null) System.out.println(tool.getError());
//		tool.addVerbindung("D", "B", 1); if (tool.getError() != null) System.out.println(tool.getError());
//
//	// ----- 3) Kürzeste Pfade berechnen
//		String[] fromA = tool.getShortestPath("A");
//		for (String str : fromA)
//			System.out.print(str);
//		System.out.println();
//		
//		String[] fromB = tool.getShortestPath("B");
//		for (String str : fromB)
//			System.out.print(str);
//		System.out.println();
//		
//		String[] fromC = tool.getShortestPath("C");
//		for (String str : fromC)
//			System.out.print(str);
//		System.out.println();
//		
//		String[] fromD = tool.getShortestPath("D");
//		for (String str : fromD)
//			System.out.print(str);
//		System.out.println();
//// -------------------------Ende------------------------------
//	}
}