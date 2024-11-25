package frontend;

import java.awt.*;
import java.awt.event.*;


public class Launcher extends Panel implements WindowListener{
	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowClosing(WindowEvent e){System.exit(1);}
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
	
	public static Frame f = new Frame("FAP-2 Launcher");
	
	Button toolButton = new Button("Tool-Direkt Version");
	Button clientButton = new Button("Server-Client Version");
	Checkbox admin = new Checkbox("Admin", false);
	
	Panel buttonPanel = new Panel();
	
	
	ActionListener toolB = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			try {
				UserInterface ui = new UserInterface();
				if (admin.getState())
					ui.admin = true;
				f.setVisible(false);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}};
	
	ActionListener clientB = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			try {
				UserInterfaceClient uic = new UserInterfaceClient();
				f.setVisible(false);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}};
	
	Launcher() {
		setLayout(new BorderLayout());
		this.add(buttonPanel, BorderLayout.CENTER);
		this.add((new Panel().add(admin)), BorderLayout.SOUTH);
		toolButton.addActionListener(toolB);
		clientButton.addActionListener(clientB);
		buttonPanel.add(toolButton);
		buttonPanel.add(clientButton);
		f.add(this);
		f.addWindowListener(this);
		f.pack();
		f.setVisible(true);
		f.repaint();
	}
	
	public static void main(String[] args){
		Launcher l = new Launcher();
	}
}
