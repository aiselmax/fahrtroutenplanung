package DirectedGraph;

public class InfoString implements Info {
	
	private String theinfo = new String();
	
	public InfoString(String inf){ 
		theinfo=inf;
	}

	public String getTheinfo() {
		return theinfo;
	}

	public void setTheinfo(String theinfo) {
		this.theinfo = theinfo;
	}

}
