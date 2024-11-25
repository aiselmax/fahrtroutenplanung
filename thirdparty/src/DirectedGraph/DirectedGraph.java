package DirectedGraph;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.Serializable;


class EdgeEnumerator implements Enumeration {
  Enumeration vertexEnum;
  Enumeration adjacentEdgeEnum;

  EdgeEnumerator(DirectedGraph directedGraph) {
    vertexEnum=directedGraph.vertices.elements();
    adjacentEdgeEnum=new EmptyEnumeration();
    while ((!adjacentEdgeEnum.hasMoreElements())&&
	   vertexEnum.hasMoreElements())
      adjacentEdgeEnum=
	((Vertex)vertexEnum.nextElement()).adjacentEdges.elements();
  }

  public boolean hasMoreElements() {
    return adjacentEdgeEnum.hasMoreElements();
  }

  public Object nextElement() {
    Object nextElement=adjacentEdgeEnum.nextElement();
    while ((!adjacentEdgeEnum.hasMoreElements())&&
	   vertexEnum.hasMoreElements())
      adjacentEdgeEnum=
	((Vertex)vertexEnum.nextElement()).adjacentEdges.elements();
    return nextElement;
  }
}

class TargetEnumerator implements Enumeration {
  Enumeration edgeEnum;

  TargetEnumerator(Enumeration edgeEnum) {
    this.edgeEnum=edgeEnum;
  }

  public boolean hasMoreElements() {
    return edgeEnum.hasMoreElements();
  }

  public Object nextElement() {
    return ((Edge)edgeEnum.nextElement()).target;
  }
}

class SourceEnumerator implements Enumeration {
  Enumeration edgeEnum;

  SourceEnumerator(Enumeration edgeEnum) {
    this.edgeEnum=edgeEnum;
  }

  public boolean hasMoreElements() {
    return edgeEnum.hasMoreElements();
  }

  public Object nextElement() {
    return ((Edge)edgeEnum.nextElement()).source;
  }
}


/**
 * The class DirectedGraph implements an attributed directed graph. Such a
 * graph consists of vertices and directed edges between pairs of those
 * vertices. There is at most one edge between a pair of vertices. A vertex
 * is represented by an instance of class Vertex, an edge is represented by an
 * instance of class Edge. Both Vertex and Edge have no public fields and are
 * only used for reference purposes. Each node and each edge can have a
 * user-defined information.
 *
 * @author	Dietrich Timm
 */
public class DirectedGraph implements Serializable {

  protected Vector vertices = new Vector();       

  /**
   * Constructs a new, empty graph.
   */
  public DirectedGraph() {
  	
  }

  /**
   * Constructs a new vertex within the graph.
   * @return the new vertex.
   */
  public Vertex newVertex() {
    return newVertex(null);
  }
 
  /**
   * Constructs a new attributed vertex within the graph.
   * @param info the vertex information
   * @return the new vertex.
   */
  public Vertex newVertex(Object info) {
    Vertex vertex=new Vertex(info);
    vertex.directedGraph=this;
    vertices.addElement(vertex);
    return vertex;
  }
 
  /**
   * Constructs a new directed edge within the graph.
   * @param source the source vertex of the edge
   * @param target the target vertex of the edge
   * @return the new edge.
   */
  public Edge newEdge(Vertex source, Vertex target) {
    return newEdge(source,target,null);
  }

  /**
   * Constructs a new attributed, directed edge within the graph.
   * @param source the source vertex of the edge
   * @param target the target vertex of the edge
   * @param info the edge information
   * @return the new edge.
   */
  public Edge newEdge(Vertex source, Vertex target, Object info) {
    Edge edge=new Edge(source,target,info);
    source.adjacentEdges.addElement(edge);
    target.incidentEdges.addElement(edge);
    return edge;
  }

  /**
   * Ensures a directed edge from a source vertex to a target vertex. If no
   * such edge exists it constructs a new edge, otherwise no changes to the
   * graph are made.
   * @param source the source vertex of the edge
   * @param target the target vertex of the edge
   * @return the existing edge or the new edge.
   */
  	public Edge ensureEdge(Vertex source, Vertex target) {
  		Edge edge=edge(source,target);
  		if (edge!=null) return edge;
  		return newEdge(source,target);
  	}

	/**
	 * Adds a vertex to a directed graph.
	 * 
	 * @param the vertex
	 * @throws DirectedGraphModificationException
	 *             if the vertex has already been added to any directed graph
	 */
  	public void addVertex(Vertex vertex) {
  		if (vertex.directedGraph!=null)
  			throw
  			new DirectedGraphModificationException(
          "Vertex "+vertex+ " has already been added to another directed graph");
  		vertex.directedGraph=this;
  		vertices.addElement(vertex);
  	}
  
	/**
	 * Adds an edge to the adjacent edges of source vertex and the incident
	 * edges of target vertex.
	 * 
	 * @throws DirectedGraphModificationException
	 *             if the directed graph of the source vertex or the taget
	 *             vertex is empty, or if the graphs are not equal.
	 */
  public void addEdge(Vertex source, Vertex target, Edge edge) {
    if (source.directedGraph==null)
      throw
	new DirectedGraphModificationException(
          "Source "+source+" has not been added to any directed graph");
    if (target.directedGraph==null)
      throw
	new DirectedGraphModificationException(
          "Target "+target+" has not been added to any directed graph");
    if (source.directedGraph!=target.directedGraph)
      throw
	new DirectedGraphModificationException(
          "Source "+source+" and target "+target+
	  " have been added to different directed graphs");
    source.adjacentEdges.addElement(edge);
    target.incidentEdges.addElement(edge);
    edge.source=source;
    edge.target=target;
  }

  /**
   * Removes an edge from the graph. Doesn't remove any vertices.
   * @param edge the edge
   */
  public void removeEdge(Edge edge) {
    if (edge.source==null)
      throw
	new DirectedGraphModificationException(
          "Edge "+edge+" has not been added to any directed graph");
    edge.source.adjacentEdges.removeElement(edge);
    edge.target.incidentEdges.removeElement(edge);
    edge.source=null;
    edge.target=null;
  }

  /**
   * Removes all edges from the graph. Doesn't remove any vertices.
   */
  public void removeEdges() {
    for (Enumeration vertexEnum=vertices.elements();
	 vertexEnum.hasMoreElements();) {
      Vertex vertex=(Vertex)vertexEnum.nextElement();
      for (Enumeration adjacentEdgeEnum=vertex.adjacentEdges.elements();
	   adjacentEdgeEnum.hasMoreElements();) {
	Edge adjacentEdge=(Edge)adjacentEdgeEnum.nextElement();
	adjacentEdge.source=null;
	adjacentEdge.target=null;
      }
      vertex.adjacentEdges=new Vector();
      vertex.incidentEdges=new Vector();
    }
  }

  /**
   * Removes a vertex from the graph. Also removes all incident and
   * adjacent edges.
   * @param vertex the vertex to remove
   */
  public void removeVertex(Vertex vertex) {
    if (vertex.directedGraph==null)
      throw
	new DirectedGraphModificationException(
          "Vertex "+vertex+" has not been added to any directed graph");
    for (Enumeration adjacentEdgeEnum=vertex.adjacentEdges.elements();
	 adjacentEdgeEnum.hasMoreElements();) {
      Edge adjacentEdge=(Edge)adjacentEdgeEnum.nextElement();
      adjacentEdge.target.incidentEdges.removeElement(adjacentEdge);
      adjacentEdge.source=null;
      adjacentEdge.target=null;
    }
    for (Enumeration incidentEdgeEnum=vertex.incidentEdges.elements();
	 incidentEdgeEnum.hasMoreElements();) {
      Edge incidentEdge=(Edge)incidentEdgeEnum.nextElement();
      incidentEdge.source.adjacentEdges.removeElement(incidentEdge);
      incidentEdge.source=null;
      incidentEdge.target=null;
    }
    vertex.adjacentEdges=new Vector();
    vertex.incidentEdges=new Vector();
    vertex.directedGraph=null;
    vertices.removeElement(vertex);
  }
  
  /**
   * @return an enumeration of all vertices of the graph.
   */
  public Enumeration vertices() {
    return vertices.elements();
  }

	/**
	 * @return an enumeration of all edges of the graph.
	 */
  public Enumeration edges() {
    return new EdgeEnumerator(this);
  }

  /**
   * Checks if an edge from a source vertex to a target vertex exists.
   * @param source the source vertex of the edge
   * @param target the target vertex of the edge
   * @return the existing edge or null.
   */
  public Edge edge(Vertex source, Vertex target) {
    for (Enumeration adjacentEdgeEnum=source.adjacentEdges.elements();
	 adjacentEdgeEnum.hasMoreElements();) {
      Edge adjacentEdge=(Edge)adjacentEdgeEnum.nextElement();
      if (target==adjacentEdge.target) return adjacentEdge;
    }
    return null;
  }

  /**
   * @param vertex the vertex
   * @return an enumeration of all adjacent vertices.
   */
  public Enumeration adjacentVertices(Vertex vertex) {
    return new TargetEnumerator(vertex.adjacentEdges.elements());
  }

  /**
   * @param vertex the vertex
   * @return an enumeration of all incident vertices.
   */
  public Enumeration incidentVertices(Vertex vertex) {
    return new SourceEnumerator(vertex.incidentEdges.elements());
  }

  /**
   * @param vertex the vertex
   * @return an enumeration of all adjacent edges.
   */
  public Enumeration adjacentEdges(Vertex vertex) {
    return vertex.adjacentEdges.elements();
  }

  /**
   * @param vertex the vertex
   * @return an enumeration of all incident edges.
   */
  public Enumeration incidentEdges(Vertex vertex) {
    return vertex.incidentEdges.elements();
  }

  /**
   * @return the number of vertices of the graph.
   */
  public int numberOfVertices() {
    return vertices.size();
  }

  /**
   * @param edge the edge
   * @return the source vertex.
   */
  public Object source(Edge edge) {
    return edge.source;
  }

  /**
   * @param edge the edge
   * @return the target vertex.
   */
  public Vertex target(Edge edge) {
    return edge.target;
  }

  /**
   * @param vertex the vertex
   * @return the information of the vertex.
   */
  public Object getInfo(Vertex vertex) {
    return vertex.info;
  }

  /**
   * Sets the vertex information.
   * @param vertex the vertex
   * @param info the information
   */
  public void setInfo(Vertex vertex, Object info) {
    vertex.info=info;
  }

  /**
   * @param edge the edge
   * @return the information of the edge.
   */
  public Object getInfo(Edge edge) {
    return edge.info;
  }

  /**
   * Sets the edge information.
   * @param edge the edge
   * @param info the information
   */
  public void setInfo(Edge edge, Object info) {
    edge.info=info;
  }

  /**
   * Returns a pretty string representation of the graph. Each vertex appears
   * on a single line together with all its adjacent vertices.
   * @return the pretty string representation.
   */
  public String toPrettyString() {
    StringBuffer buf=new StringBuffer();
    Hashtable vertexPos=new Hashtable();
    int pos=0;
    for (Enumeration vertexEnum=vertices.elements();
	 vertexEnum.hasMoreElements();)
      vertexPos.put(vertexEnum.nextElement(),new Integer(pos++));
    for (Enumeration vertexEnum=vertices.elements();
	 vertexEnum.hasMoreElements();) {
      Vertex vertex=(Vertex)vertexEnum.nextElement();
      buf.append(vertexPos.get(vertex)).append('/').append(vertex)
      .append(':');
      for (Enumeration adjacentEdgeEnum=vertex.adjacentEdges.elements();
	   adjacentEdgeEnum.hasMoreElements();) {
	Edge adjacentEdge=(Edge)adjacentEdgeEnum.nextElement();
	buf.append(' ').append(vertexPos.get(adjacentEdge.target))
	.append('/').append(adjacentEdge);
      }
      buf.append('\n');
    }      
    return buf.toString();
  }

  public String toString() {
    return toPrettyString();
  }
	public Vector getVertices() {
		return vertices;
	}

	public void setVertices(Vector vertices) {
		this.vertices = vertices;
	}
}

