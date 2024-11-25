package DirectedGraph;

import java.util.Vector;
import java.util.Enumeration;
import java.io.Serializable;


public class Vertex implements Serializable {
  DirectedGraph directedGraph;
  protected Vector adjacentEdges=new Vector();
  protected Vector incidentEdges=new Vector();
  protected Object info;                 

  Vertex(Object info) {
    this.info=info;
  }

  public Vertex() {
  }
  
  /**
	* @return the information of the vertex.
	*/
	public Info getInfo() {
		return (Info)this.info;
	}
	
	/**
	* Sets the vertex information.
	* @param info the information
	*/
	public void setInfo(Info info) {
		this.info=info;
	}

  public DirectedGraph directedGraph() {
    return directedGraph;
  }

  public Enumeration adjacentEdges() {
    return adjacentEdges.elements();
  }

  public Enumeration incidentEdges() {
    return incidentEdges.elements();
  }

  public Enumeration adjacentVertices() {
    return new TargetEnumerator(adjacentEdges.elements());
  }

  public Enumeration incidentVertices() {
    return new SourceEnumerator(incidentEdges.elements());
  }

  public String toString() {
    return "<"+info+">@0x"+Integer.toHexString(hashCode());
  }
}
