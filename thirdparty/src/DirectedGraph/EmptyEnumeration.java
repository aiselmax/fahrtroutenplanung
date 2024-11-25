// EmptyEnumeration.java
// $Id: EmptyEnumeration.java,v 1.1.1.1 2005/10/27 09:43:56 fritzsch Exp $
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html
package DirectedGraph;

import java.util.*;

/**
 * An empty enumeration.
 */

public class EmptyEnumeration implements Enumeration {

    public final boolean hasMoreElements() {
	return false;
    }

    public final Object nextElement() {
	throw new NoSuchElementException("empty enumeration");
    }

}

