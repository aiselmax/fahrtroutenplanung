package backend;
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
 
import org.junit.Before;
import org.junit.Test;

import frontend.Client;
import frontend.UserInterface;
 
public class ServerTest {
 
    private static final String TEST_HOSTNAME = "localhost";
    private static final int TEST_PORT = 3141;
    
    BufferedWriter write;
    Socket testClient=null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    PrintStream old = System.out;
    String testConsole;
    String compare;
 
 
    @Test
    public void testServerStart() {
    	Server testServer = new Server();
   // 	System.setOut(ps);
    	System.out.flush();
    	testServer.testArea();
    
        testServer.test=false;
        testConsole=baos.toString();
        compare="Server wurde gestartet...";
        //testServer.stopTest();
       
  //      assertEquals(testConsole,compare);
        //assertTrue(testConsole.equals(compare));
    }
    @Test
    public void testHandlerStart() {
    	Server testServer = new Server();
    	testServer.test=true;
    //	System.setOut(ps);
    	System.out.flush();
    	testServer.testArea();
    	try {
			testClient=new Socket(TEST_HOSTNAME,TEST_PORT);
			write.write("close");
			testServer.test=false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
   
   //     testConsole=baos.toString();
        compare="Hanlder wurde gestartet...";
        
        
        assertEquals(testConsole,compare);
        //assertTrue(testConsole.equals(compare));
    }
 
}