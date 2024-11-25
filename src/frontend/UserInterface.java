package frontend;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.JOptionPane;

import backend.Tool;

public class UserInterface extends Panel implements WindowListener{

	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowClosing(WindowEvent e){System.exit(1);}
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
	
	public static Frame f = new Frame("Fahrtroutenplanung - FAP-2");
	private Tool tool = new Tool();
	public boolean admin = false;

	//Labels und Strings
	Label authors = new Label("Autoren: Dennis Hinterwimmer, Maxim Aisel, Paul Wolff");
	Label card3Title = new Label("Route:");
	Label connLager = new Label("");
	
	String license = new String("Dies ist ein Programm, welches im Rahmen der Lehrveranstaltung "
			+ "Softwareengineering II im Studiengang Medieninformatik an der Hochschule für "
			+ "Technik und Wirtschaft Dresden enstanden ist. \n"
			+ "Das entwickelte Produkt ist Open Source, eine kommerzielle Nutzung ist daher "
			+ "ausgeschlossen. Weitere Lizenzangaben sind in der Dokumentation zu finden.");
	String info = new String("Das Programm verwaltet einen gerichteten Graphen von Lagern,"
			+ "die mit in Kilometern angegebenen Kanten(Routen) verbunden sind. Bei Auswahl"
			+ "eines Startlagers, werden alle Lager verbunden und die Route sowie der zurückgelegte"
			+ "Weg in Kilometern ausgegeben.");
	
	TextField addName = new TextField();
	TextField entfEing = new TextField();
	TextArea infoText = new TextArea(license + "\n\n" + info, 7, 1, TextArea.SCROLLBARS_VERTICAL_ONLY);
	List route = new List();
	List verbundeneLager = new List();
	
	
	//Buttons
	Button nextB = new Button("Weiter");
	Button showB = new Button("Route zeigen");
	Button backB = new Button("Zurueck");
	Button hinzufB = new Button("Lager/Verbindungen hinzufuegen");
	Button cancelB = new Button("Abbrechen");
	Button createB = new Button("Lager erstellen");
	Button doneB = new Button("Fertig");
	Button addB = new Button("Hinzufuegen");
	Button addNextB = new Button("Verbindungen hinzufuegen");
	Button adminDeleteB = new Button("Löschen");
	Button adminDeleteAll = new Button("Alle löschen");
	
	//Contentpanels
	Panel startButtonPanel = new Panel();
	Panel selectionButtonPanel = new Panel();
	Panel routeButtonPanel = new Panel();
	Panel contentCard1 = new Panel(new GridLayout(2,1));
	Panel contentCard2 = new Panel(new GridLayout(4,1));
	Panel contentCard5 = new Panel(new GridLayout(2,1));
	Panel addConnGrid = new Panel(new GridLayout(3,1));
	Panel card4NorthPanel = new Panel();
	Panel card4CenterPanel = new Panel();
	Panel addLagerButtonPanel= new Panel();
	Panel newConButtonPanel = new Panel();
	Panel addConnPanel1 = new Panel(new FlowLayout());
	Panel addConnPanel2 = new Panel(new FlowLayout());
	Panel addConnPanel3 = new Panel();
	Panel connDoneButtonP = new Panel();
	
	
	Choice lagerList = new Choice();
	Choice lagerList2 = new Choice();
	Choice lagerList3 = new Choice();
	
	Choice inOut = new Choice();
	
	
	CardLayout cards = new CardLayout();
	Panel card1 = new Panel();
	Panel card2 = new Panel();
	Panel card3 = new Panel();
	Panel card4 = new Panel();
	Panel card5 = new Panel();
	
	
	
	ActionListener next = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			if (!admin){
				adminDeleteB.setVisible(false);
				adminDeleteAll.setVisible(false);
			}
			showCard("Eingabe");
	}};
	
	ActionListener show = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			show(lagerList.getSelectedItem());
	}};
	
	ActionListener back = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			showCard("Eingabe");
	}};
	
	ActionListener hinzuf = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			showCard("Hinzufuegen");
	}};
	
	ActionListener cancel = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			showCard("Eingabe");
			addName.setText("");
			entfEing.setText("");
	}};
	
	ActionListener create = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			String newL = new String(addName.getText());
			if (checkLagerName(newL)){
				int before = tool.getLagerliste().length;
				tool.addLager(newL);
				int after = tool.getLagerliste().length;
				connLager.setText(newL);
				loadLager();
				if (before == after){
					JOptionPane.showMessageDialog(f,
						    "Lager mit dem Namen (" + newL +") existiert bereits ...",
						    "Ungültiger Lagername!",
						    JOptionPane.ERROR_MESSAGE);
				} else if (tool.getLagerliste().length>1)
					showCard("Verbindungen");
				else
					showCard("Eingabe");
			}
			addName.setText("");
	}};
	
	boolean checkLagerName(String lager){
		if (lager.length() == 0){
			JOptionPane.showMessageDialog(f,
				    "Bitte einen Lagernamen eingeben ...",
				    "Ungültiger Lagername!",
				    JOptionPane.ERROR_MESSAGE);
			return false;
		} else if(lager.startsWith(" ")){
			JOptionPane.showMessageDialog(f,
				    "Bitte den Lagernamen nicht mit einem Leerzeichen beginnen ...",
				    "Ungültiger Lagername!",
				    JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			return true;
		}
	}
	
	ActionListener addLager = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			if(tool.getLagerliste().length>=2){
				addName.setText("");
				connLager.setText(lagerList2.getSelectedItem());
				showCard("Verbindungen");
			} else{
				JOptionPane.showMessageDialog(f,
						"Zu wenige Lager zum verbinden vorhanden ...",
					    "Zu wenige Lager!",
					    JOptionPane.ERROR_MESSAGE);
			}
	}};
	
	ActionListener addConn = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			if(connLager.getText() != lagerList3.getSelectedItem()){
				if(inOut.getSelectedItem() == "ausgehend"){
					try {
						tool.addVerbindung(connLager.getText(), lagerList3.getSelectedItem(), Integer.parseInt(entfEing.getText()));;
						verbundeneLager.add("zu " + lagerList3.getSelectedItem() + " / " + Integer.parseInt(entfEing.getText())+ " km / ausgehend");
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(f,
								"Bitte eine gültige Zahl als Entfernung eingeben ...",
							    "Ungültige Entfernung!",
							    JOptionPane.ERROR_MESSAGE);
					}
				} else if (inOut.getSelectedItem() == "eingehend"){
					try {
						tool.addVerbindung(lagerList3.getSelectedItem(), connLager.getText(), Integer.parseInt(entfEing.getText()));
						verbundeneLager.add("von " + lagerList3.getSelectedItem() +" / "+ Integer.parseInt(entfEing.getText())+ " km / eingehend");
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(f,
								"Bitte eine gültige Zahl als Entfernung eingeben ...",
							    "Ungültige Entfernung!",
							    JOptionPane.ERROR_MESSAGE);
					}
				} else {
					try {
						verbundeneLager.add("von " + lagerList3.getSelectedItem() +" / "+ Integer.parseInt(entfEing.getText())+ " km / eingehend");
						verbundeneLager.add("zu " + lagerList3.getSelectedItem() + " / " + Integer.parseInt(entfEing.getText())+ " km / ausgehend");
						tool.addVerbindung(lagerList3.getSelectedItem(), connLager.getText(), Integer.parseInt(entfEing.getText()));
						tool.addVerbindung(connLager.getText(), lagerList3.getSelectedItem(), Integer.parseInt(entfEing.getText()));;
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(f,
								"Bitte eine gültige Zahl als Entfernung eingeben ...",
							    "Ungültige Entfernung!",
							    JOptionPane.ERROR_MESSAGE);
				}
				}
			} else {
				JOptionPane.showMessageDialog(f,
						"Ziel und Quelle der Verbindung sind identisch.\nBitte unterschiedliche Lager wählen ...",
					    "Ungültige Verbindung!",
					    JOptionPane.ERROR_MESSAGE);
			}
			
	}};
	
	ActionListener done = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			//TODO
			//hier müssen die Lager und Felder aus Card5 wieder gelöscht werden
			verbundeneLager.removeAll();
			inOut.select(0);
			lagerList3.select(0);
			addConnPanel2.add(new Label("Verbindung:"));
			entfEing.setText("");
			showCard("Eingabe");
	}};
	
	ActionListener del = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			try{
				tool.delLager(lagerList.getSelectedItem());
				lagerList.remove(lagerList.getSelectedItem());
				loadLager();
			}catch (IllegalArgumentException ex){
				JOptionPane.showMessageDialog(f,
						"Kein Lager zum löschen vorhanden ...",
					    "Keine Lager ... ",
					    JOptionPane.ERROR_MESSAGE);
			}
	}};
	
	ActionListener delAll = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			String[] l = tool.getLagerliste();
			for (String s: l){
				tool.delLager(s);
			}
			loadLager();
	}};
	
	UserInterface() throws Exception{
		setUp();
		f.add(this);
		f.addWindowListener(this);
		f.pack();
		f.setVisible(true);
		f.repaint();
		//getClient();
		
	}
	
	private void setUp(){
		
		//setting layouts for cards
		card1.setLayout(new BorderLayout());
		card2.setLayout(new BorderLayout());
		card3.setLayout(new BorderLayout());
		card4.setLayout(new BorderLayout());
		card5.setLayout(new BorderLayout());
		
		//setting Layout for UserInterface and adding cards
		setLayout(cards);
		add(card1, "Titelseite");
		add(card2, "Eingabe");
		add(card3, "Ausgabe");
		add(card4, "Hinzufuegen");
		add(card5, "Verbindungen");
		
		//setting up Buttons
		nextB.addActionListener(next);
		startButtonPanel.add(nextB);
		showB.addActionListener(show);
		selectionButtonPanel.add(showB);
		adminDeleteB.addActionListener(del);
		selectionButtonPanel.add(adminDeleteB);
		adminDeleteAll.addActionListener(delAll);
		selectionButtonPanel.add(adminDeleteAll);
		backB.addActionListener(back);
		routeButtonPanel.add(backB);
		hinzufB.addActionListener(hinzuf);
		cancelB.addActionListener(cancel);
		addLagerButtonPanel.add(cancelB);
		addNextB.addActionListener(addLager);
		newConButtonPanel.add(addNextB);
		doneB.addActionListener(done);
		connDoneButtonP.add(doneB);
		addB.addActionListener(addConn);
		createB.addActionListener(create);
		
		
		//laedt die Lager in die Dropdowns
		loadLager();
		
		//Adding content to card 1
		contentCard1.add(infoText);
		infoText.setEditable(false);
		contentCard1.add(startButtonPanel);
		card1.add(contentCard1, BorderLayout.CENTER);
		card1.add(authors, BorderLayout.SOUTH);
		
		//adding content to card 2
		card2.add(contentCard2, BorderLayout.CENTER);
		Panel lagerListPanel = new Panel();
		lagerListPanel.add(new Label("Lager wählen: "));
		lagerListPanel.add(lagerList);
		contentCard2.add(lagerListPanel);
		contentCard2.add(selectionButtonPanel);
		Panel hinzufPanel = new Panel();
		hinzufPanel.add(hinzufB);
		card2.add(hinzufPanel, BorderLayout.SOUTH);
		
		//adding content to card 3
		card3.add(card3Title, BorderLayout.NORTH);
		card3.add(route, BorderLayout.CENTER);
		card3.add(routeButtonPanel, BorderLayout.SOUTH);
		
		//adding content to card 4
		card4.add(card4NorthPanel, BorderLayout.NORTH);
		card4NorthPanel.setLayout(new GridLayout(3,1));
		Panel card4LabelPanel = new Panel();
		card4LabelPanel.add(new Label("Lagernamen eingeben:"));
		card4NorthPanel.add(card4LabelPanel);
		addName.setColumns(10);
		Panel addNamePanel = new Panel();
		addNamePanel.add(addName);
		card4NorthPanel.add(addNamePanel);
		Panel createPanel = new Panel();
		createPanel.add(createB);
		card4NorthPanel.add(createPanel);
		card4.add(card4CenterPanel, BorderLayout.CENTER);
		card4CenterPanel.setLayout(new GridLayout(3,1));
		Panel help = new Panel();
		help.add(new Label("Zu verbindendes Lager:"));
		help.add(lagerList2);
		Panel card4BorderPanel = new Panel();
		card4BorderPanel.add(new Label("---------------------------------------------------------------------"));
		card4CenterPanel.add(card4BorderPanel);
		card4CenterPanel.add(help);
		card4CenterPanel.add(newConButtonPanel);
		card4.add(addLagerButtonPanel, BorderLayout.SOUTH);
		
		//adding content to card 5
		card5.add(contentCard5);
		contentCard5.add(addConnGrid);
		contentCard5.add(verbundeneLager);
		
		
		addConnPanel1.add(connLager);
		addConnPanel1.add(new Label(" zu "));
		addConnPanel1.add(lagerList3);
		
		addConnPanel2.add(new Label("Verbindung:"));
		addConnPanel2.add(entfEing);
		addConnPanel2.add(new Label("km"));
		addConnPanel3.add(addB);
		inOut.add("ausgehend");
		inOut.add("eingehend");
		inOut.add("bidirectional");
		addConnPanel2.add(inOut);
		addConnGrid.add(addConnPanel1);
		addConnGrid.add(addConnPanel2);
		addConnGrid.add(addConnPanel3);
		card5.add(connDoneButtonP, BorderLayout.SOUTH);
		//card5.add(addLagerButtonPanel, BorderLayout.SOUTH);
		
		
		
		
	}
	
	public void showCard(String c){
		//this.setLayout(new GridLayout());
		cards.show(this, c);
	}
	
	//laedt die Lager von Tool
	public void loadLager(){
		String[] lager = null;
		lager = tool.getLagerliste();
		
		
		lagerList.removeAll();
		lagerList2.removeAll();
		lagerList3.removeAll();
		for(int i = 0; i<lager.length;i++){
			lagerList.add(lager[i]);
			lagerList2.add(lager[i]);
			lagerList3.add(lager[i]);
		}
	}
	
	public void show(String lager){
		route.removeAll();
		loadLager();
		String[] routeList = null;
		try {
			routeList = tool.getShortestPath(lager);
			
			for(int i = 0; i<routeList.length;i++){
				route.add(routeList[i]);
			}
			cards.show(this, "Ausgabe");
		} catch(NullPointerException e){
			JOptionPane.showMessageDialog(f,
					tool.getError(),
				    "Tool Fehler!!!",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
//	private Client getClient() throws Exception{
//		if(client != null){
//			client.close();
//		}
//		return new Client();
//	}
	
	public static void main(String[] args) throws Exception{
		UserInterface g = new UserInterface();
	}
}



/*

new Haltestelle(bla, bla, bla) {
	Vertex vertex = new Haltestelle(bla, bla, bla);
	vertices.addVertex(vertex);
	return vertex;

}


*/