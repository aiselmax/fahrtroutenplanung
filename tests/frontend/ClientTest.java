package frontend;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.*;
import org.junit.*;

public class ClientTest {
	
	@Test
	public void testGetLager() throws ClassNotFoundException, IOException{
		String[] testListe = {"A","B","C","D"};
		Client client=new Client();
		
			String[] lagerliste = client.getLager();
			assertEquals(testListe.length,lagerliste.length);
			for(int i = 0; i< lagerliste.length;i++){
				assertEquals(testListe[i],lagerliste[i]);
				System.out.println(lagerliste[i]);
			}
		client.Close();	
		
	}
	
	@Test
	public void testAddLager() throws IOException, ClassNotFoundException, InterruptedException{
		String[] testListe = {"A","B","C","D","E"};
		Client client=new Client();
		client.addLager("E");
		String[] lagerliste = client.getLager();
		assertEquals(5,lagerliste.length);
		
		client.addLager("G");
		lagerliste = client.getLager();
		assertEquals(6,lagerliste.length);
		
	}

}
