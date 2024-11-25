package backend;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class TestsTool {

	@Test
	public void testTool() {
		// Tool erzeugen und Lagerliste leeren
		Tool t1 = new Tool();
		for (String lager : t1.getLagerliste())
			t1.delLager(lager);

		// Lagerliste ist leer, weil kein Lager im Graph existiert
		String[] testListe = t1.getLagerliste();
		assertEquals(0, testListe.length);

		// Ein Lager wird erstellt und 2.Instanz von Tool wird erzeugt
		// => t2 sieht unter t1 erstellten Lager
		t1.addLager("test");
		Tool t2 = new Tool();
		testListe = t2.getLagerliste();
		assertEquals(1, testListe.length);
	}

	@Test
	public void testAddLager() {
		// Tool erzeugen und Lagerliste leeren
		Tool t = new Tool();
		for (String lager : t.getLagerliste())
			t.delLager(lager);

		// Ein Lager wird erstellt und aus dem Graphen geholt => es kommt keine Fehlermeldung
		t.addLager("test");
		assertNull(t.getError());
		assertEquals("test", t.getLagerByName("test").name);

		// neu erstellter Lager ist in der Lagerliste
		int countLager = t.getLagerliste().length;
		assertEquals(t.getLagerByName("test").id, countLager);

		// ein Lager mit den gleichen Name wird estellt => Lagerliste bleibt
		// unverändert, es kommt eine Fehlermeldung
		t.addLager("test");
		assertEquals(t.getLagerliste().length, countLager);
		assertNotNull(t.getError());
	}

	@Test
	public void testEditLager() {
		// Tool erzeugen und Lagerliste leeren
		Tool t = new Tool();
		for (String lager : t.getLagerliste())
			t.delLager(lager);

		// Ein Lager wird erstellt und umbenannt => es kommt keine Fehlermeldung,
		// anschliessend wird der umbenannte Lager aus dem Graphen geholt
		t.addLager("alt");
		assertEquals(1, t.getLagerliste().length);
		t.editLager("alt", "neu");
		assertNull(t.getError());
		assertEquals("neu", t.getLagerByName("neu").name);
		assertEquals(1, t.getLagerliste().length);

		// Der Lager ist unter altem Namen nicht mehr im Graphen zu finden
		assertNull(t.getLagerByName("alt"));
		t.editLager("alt", "neu");
		assertNotNull(t.getError());
	}

	@Test
	public void testDelLager() {
		// Tool erzeugen und Lagerliste leeren
		Tool t = new Tool();
		for (String lager : t.getLagerliste())
			t.delLager(lager);

		// Ein Lager wird erstellt
		t.addLager("test");
		assertNotNull(t.getLagerByName("test"));
		assertEquals(1, t.getLagerliste().length);

		// Ein Lager wird gelöscht => Lager ist nicht mehr im Graphen zu finden
		t.delLager("test");
		assertNull(t.getError());
		assertNull(t.getLagerByName("test"));
		assertEquals(0, t.getLagerliste().length);

		// Ein Versuch einen nicht existierenden Lager zu löschen, verursacht
		// eine Fehlermeldung
		t.delLager("test");
		assertNotNull(t.getError());
	}

	@Test
	public void testGetLagerByName() {
		// Tool erzeugen und Lagerliste leeren
		Tool t = new Tool();
		for (String lager : t.getLagerliste())
			t.delLager(lager);

		// Ein Versuch nich exitierenden Lager aus dem Graph zu holen, liefert
		// NULL zurück
		assertNull(t.getLagerByName("test"));

		// Ein Lager wird erstellt und aus dem Graphen geholt
		t.addLager("test");
		assertSame(t.getLagerByName("test"), t.getLagerByName("test"));
		t.addLager("test2");
		assertNotSame(t.getLagerByName("test"), t.getLagerByName("test2"));
	}

	@Test
	public void testAddVerbindung() {
		// Tool erzeugen und Lagerliste leeren
		Tool t = new Tool();
		for (String lager : t.getLagerliste())
			t.delLager(lager);

		// Es wir versucht eine Verbindung zu erstellen, aber beide Lager existieren nicht => es
		// kommt eine Fehlermeldung
		t.addVerbindung("A", "B", 1);
		assertNotNull(t.getError());

		// Eine Verbindung wird erstellt, aber 2.Lager existiert nicht =>
		// es kommt eine Fehlermeldung
		t.addLager("A");
		t.addVerbindung("A", "B", 1);
		assertEquals(0, t.getLagerByName("A").ausgehende.size());

		// Eine Verbindung wird erstellt, aber Distanz ist kleiner als 1 =>
		// es kommt eine Fehlermeldung
		t.addLager("B");
		t.addVerbindung("A", "B", 0);
		assertEquals(0, t.getLagerByName("A").ausgehende.size());

		// Eine Verbindung wird erstellt und befindet sich im Graph
		t.addVerbindung("A", "B", 1);
		assertEquals(1, t.getLagerByName("A").ausgehende.size());
		assertEquals("B", t.getLagerByName("A").ausgehende.firstElement().ziellager.name);
		assertEquals("A", t.getLagerByName("A").ausgehende.firstElement().quellelager.name);
		assertEquals(0, t.getLagerByName("B").ausgehende.size());

		// Eine existierend Verbindung wird überschrieben, neue Distanz ist 2 km
		t.addVerbindung("B", "A", 1);
		t.addVerbindung("B", "A", 2);
		assertEquals(1, t.getLagerByName("B").ausgehende.size());
		assertEquals(2, t.getLagerByName("B").getVerbindungTo(t.getLagerByName("A")).km);
	}

	@Test
	public void testGetShortestPath() {
		// Tool erzeugen und Lagerliste leeren
		Tool t = new Tool();
		for (String lager : t.getLagerliste())
			t.delLager(lager);

		// Verursacht eine Fehlermeldung, weil Lagerliste weniger als 2 Lager
		// hat
		t.addLager("A");
		t.getShortestPath("A");
		assertNotNull(t.getError());

		// Verursacht eine Fehlermeldung, weil keine Verbindung zwischen A und B
		// existiert
		t.addLager("B");
		t.getShortestPath("A");
		assertNotNull(t.getError());

		// Verursacht eine Fehlermeldung, weil Lager B eine Sackgasse ist
		t.addVerbindung("A", "B", 1);
		t.getShortestPath("A");
		assertNotNull(t.getError());

		// Findet den kürzesten Weg => das erste Element der zurückgegebener
		// Liste der Startlager und letztes Element die Distanz
		t.addVerbindung("B", "A", 1);
		String[] path = t.getShortestPath("A");
		assertNull(t.getError());
		assertEquals(3, path.length);
		assertEquals("A", path[0]);
		assertEquals("B", path[1]);
		assertEquals("(1km)", path[2]);

		// Verursacht eine Fehlermeldung,weil keine Verbingung von A-B nach C-D
		// existiert
		t.addLager("C");
		t.addLager("D");
		t.addVerbindung("C", "D", 1);
		t.addVerbindung("D", "C", 1);
		assertNull(t.getShortestPath("A"));
		assertNotNull(t.getError());
		//Ergänzung der fehlender Verbindung behebt die Fehlermeldung
		t.addVerbindung("B", "C", 1);
		t.addVerbindung("C", "B", 1);
		t.getShortestPath("A");
		assertNull(t.getError());
		
		// Test mit komplexer Lagerstruktur
		for (String lager : t.getLagerliste())
			t.delLager(lager);
		t.addLager("A");
		t.addLager("B");
		t.addLager("C");
		t.addLager("D");
		t.addLager("E");
		t.addLager("F");
		t.addVerbindung("A", "B", 10);
		t.addVerbindung("A", "C", 20);	
		t.addVerbindung("B", "A", 10);
		t.addVerbindung("B", "E", 10);
		t.addVerbindung("B", "D", 50);
		t.addVerbindung("C", "A", 20);
		t.addVerbindung("C", "D", 20);
		t.addVerbindung("C", "E", 33);	
		t.addVerbindung("D", "B", 50);
		t.addVerbindung("D", "C", 20);
		t.addVerbindung("D", "E", 20);
		t.addVerbindung("D", "F", 2);	
		t.addVerbindung("E", "B", 10);
		t.addVerbindung("E", "C", 33);
		t.addVerbindung("E", "D", 20);
		t.addVerbindung("E", "F", 1);	
		t.addVerbindung("F", "D", 2);
		t.addVerbindung("F", "E", 1);	
		t.getShortestPath("A");
		String[] test ={"A","B","E","F","D","C","(43km)"};
		String[] shortetestPath = t.getShortestPath("A");
		for(int i =0; i< shortetestPath.length;i++){
			assertEquals(test[i],shortetestPath[i]);
		}
	}
	@Test
	public void testGetLagerliste() {
		// Tool erzeugen und Lagerliste leeren
		Tool t = new Tool();
		for (String lager : t.getLagerliste())
			t.delLager(lager);

		// Lagerliste ist leer, weil kein Lager im Graph existiert
		String[] testListe = t.getLagerliste();
		assertEquals(0, testListe.length);

		// neu ertellte Lagern sind in der Lagerliste
		t.addLager("test1");
		t.addLager("test2");
		testListe = t.getLagerliste();
		assertEquals(2, testListe.length);
	}

	@Test
	public void testGetError() {
		// Tool erzeugen und Lagerliste leeren
		Tool t = new Tool();
		for (String lager : t.getLagerliste())
			t.delLager(lager);

		// Fehlermeldung ist NULL solange kein Fehler kommt
		assertNull(t.getError());

		// Auflistung von allen möglichen Fehlermeldungen
		t.delLager("test");
		assertEquals("Lager: test wurde nicht gefunden", t.getError());

		t.editLager("test", "test2");
		assertEquals("Lager: test wurde nicht gefunden", t.getError());

		t.getShortestPath("test");
		assertEquals("Lagerliste hat weniger als 2 Lager", t.getError());

		t.addLager("test");
		t.addLager("test");
		assertEquals("Lager: test exitiert schon", t.getError());

		t.addVerbindung("test", "test2", 1);
		assertEquals("Lager: test2 wurde nicht gefunden", t.getError());

		t.addVerbindung("test2", "test", 1);
		assertEquals("Lager: test2 wurde nicht gefunden", t.getError());

		t.addLager("test2");
		t.addVerbindung("test", "test2", 0);
		assertEquals("Disntanz in km muss größer als 0 sein", t.getError());

		t.getShortestPath("test");
		assertEquals("Lager: test ist eine Sackgasse ", t.getError());

		t.addVerbindung("test", "test2", 1);
		t.getShortestPath("test");
		assertEquals("Lager: test2 ist eine Sackgasse ", t.getError());

		t.addVerbindung("test2", "test", 1);
		t.addLager("test3");
		t.addVerbindung("test3", "test", 1);
		t.getShortestPath("test");
		assertEquals("Lager: test3 hat zu wenig Verbindungen", t.getError());

		t.addVerbindung("test2", "test3", 1);
		assertNull(t.getError());
	}

}
