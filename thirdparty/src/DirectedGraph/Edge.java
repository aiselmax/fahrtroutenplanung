package DirectedGraph;

import java.io.Serializable;


public class Edge implements Serializable {
  protected Vertex source;
  protected Vertex target;
  protected Object info;                     

  Edge(Vertex source, Vertex target, Object info) {
    this.source=source;
    this.target=target;
    this.info=info;
  }

  public Edge() {
  }

  public DirectedGraph directedGraph() {
    return source.directedGraph;
  }
  
  /**
	 * @return the information of the edge.
	 */
	public Info getInfo() {
	 return (Info)this.info;
	}
	
	/**
	 * Sets the edge information.
	 * @param info the information
	 */
	public void setInfo(Info info) {
		this.info=info;
	}
	
  public Vertex source() {
    return source;
  }

  public Vertex target() {
    return target;
  }

  public String toString() {
    return
      "("+source+","+target+")/"+info+"@0x"+Integer.toHexString(hashCode());
  }
  
	public boolean equals(Edge e)
	{
		return(e.source == this.source && e.target == this.target && e.info == this.info);
	}
}
