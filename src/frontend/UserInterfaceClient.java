package frontend;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.JOptionPane;

import backend.Tool;
import frontend.Client;

public class UserInterfaceClient extends Panel implements WindowListener{

	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowClosing(WindowEvent e){System.exit(1);}
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
	
	public static Frame f = new Frame("FAP-2");
	static Client client=new Client();

	//Labels und Strings
	Label authors = new Label("Autoren: Dennis Hinterwimmer, Maxim Aisel, Paul Wolff");
	Label title = new Label("Fahrtroutenplanung 2");
	Label card3Title = new Label("Route:");
	Label connLager = new Label("");
	
	String license = new String("Blablabla Linzenz-Ingo \nBlablabla Linzen-Ingo");
	String info = new String("Hier steht eine Kurzbeschreibung Hier steht eine Kurzbeschreibung");
	
	TextField addName = new TextField();
	TextField entfEing = new TextField();
	TextArea infoText = new TextArea(license + "\n\n" + info, 7, 1, TextArea.SCROLLBARS_NONE);
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
	
	//Contentpanels
	Panel startButtonPanel = new Panel();
	Panel selectionButtonPanel = new Panel();
	Panel routeButtonPanel = new Panel();
	Panel contentCard1 = new Panel(new GridLayout(2,1));
	Panel contentCard2 = new Panel(new GridLayout(2,1));
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
				connLager.setText(newL);
				try {
				//	client = getClient();
					client.addLager(newL);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				loadLager();
				showCard("Verbindungen");
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
			addName.setText("");
			connLager.setText(lagerList2.getSelectedItem());
			showCard("Verbindungen");
	}};
	
	ActionListener addConn = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			if(connLager.getText() != lagerList3.getSelectedItem()){
				if(inOut.getSelectedItem() == "ausgehend"){
					try {
						client.addVerbindung(connLager.getText(), lagerList3.getSelectedItem(), entfEing.getText());;
					} catch(Exception e1){
						
					}
					verbundeneLager.add("zu " + lagerList3.getSelectedItem() + " / " + Integer.parseInt(entfEing.getText())+ " km / ausgehend");
				} else if (inOut.getSelectedItem() == "eingehend"){
					try {
						client.addVerbindung(lagerList3.getSelectedItem(), connLager.getText(),entfEing.getText());
					} catch(Exception e2){
						
					}
					verbundeneLager.add("von " + lagerList3.getSelectedItem() +" / "+ Integer.parseInt(entfEing.getText())+ " km / eingehend");
				} else {
					try {
						client.addVerbindung(connLager.getText(), lagerList3.getSelectedItem(), entfEing.getText());;
					} catch(Exception e1){
						
					} 
					try {
						client.addVerbindung(lagerList3.getSelectedItem(), connLager.getText(),entfEing.getText());
					} catch(Exception e2){
						
					}
					
					verbundeneLager.add("zu " + lagerList3.getSelectedItem() + " / " + Integer.parseInt(entfEing.getText())+ " km / ausgehend");
					verbundeneLager.add("von " + lagerList3.getSelectedItem() +" / "+ Integer.parseInt(entfEing.getText())+ " km / eingehend");
				}
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
	
	UserInterfaceClient() throws Exception{
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
		card1.add(title, BorderLayout.NORTH);
		card1.add(contentCard1, BorderLayout.CENTER);
		card1.add(authors, BorderLayout.SOUTH);
		
		//adding content to card 2
		card2.add(title, BorderLayout.NORTH);
		card2.add(contentCard2, BorderLayout.CENTER);
		contentCard2.add(lagerList);
		contentCard2.add(selectionButtonPanel);
		card2.add(hinzufB, BorderLayout.SOUTH);
		
		//adding content to card 3
		card3.add(card3Title, BorderLayout.NORTH);
		card3.add(route, BorderLayout.CENTER);
		card3.add(routeButtonPanel, BorderLayout.SOUTH);
		
		//adding content to card 4
		card4.add(card4NorthPanel, BorderLayout.NORTH);
		card4NorthPanel.setLayout(new GridLayout(3,1));
		card4NorthPanel.add(new Label("Lagernamen eingeben:"));
		addName.setColumns(10);
		card4NorthPanel.add(addName);
		card4NorthPanel.add(createB);
		card4.add(card4CenterPanel, BorderLayout.CENTER);
		card4CenterPanel.setLayout(new GridLayout(3,1));
		Panel help = new Panel();
		help.add(new Label("Zu verbindendes Lager:"));
		help.add(lagerList2);
		card4CenterPanel.add((new Panel()).add(new Label("----------------------------------------------")));
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
		try {
			//client.getLager();
			lager = client.getLager();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			routeList = client.getRoute(lager);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i<routeList.length;i++){
			route.add(routeList[i]);
		}
		cards.show(this, "Ausgabe");
	}
	
//	private Client getClient() throws Exception{
//		if(client != null){
//			client.close();
//		}
//		return new Client();
//	}
	
	public static void main(String[] args) throws Exception{
		UserInterfaceClient g = new UserInterfaceClient();
	}
}



/*

new Haltestelle(bla, bla, bla) {
	Vertex vertex = new Haltestelle(bla, bla, bla);
	vertices.addVertex(vertex);
	return vertex;

}


*/