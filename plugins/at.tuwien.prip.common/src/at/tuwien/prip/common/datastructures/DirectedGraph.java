/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.prip.common.datastructures;

/**
 * DirectedGraph.java
 *
 * implemets a directed graph and some related algorithms
 *
 * Created: Wed Mar 13 01:19:59 2002
 *
 * @author Michal Ceresna
 * @version
 */
import java.io.*;
import java.util.*;

public class DirectedGraph<Name,Info>
    implements Serializable
{

    private static final long serialVersionUID = 5330891889259418544L;

    protected class Vertex
        implements Serializable
    {

        private static final long serialVersionUID = 5227951788989949836L;

        /**
         * list of edges incoming tom and outgoing from
         * this vertex
         * Set<Edge>
         */
        private final Set<Edge> edges = new HashSet<Edge>();

        /**
         * user definined name of this vertex
         */
        private final Name vname;

        /**
         * user's additional info associated to this vertex
         */
        private Info additional_info = null;

        public Vertex(Name vname) {
            this.vname = vname;
        }

        public void addEdge(Edge e) {
            edges.add(e);
        }

        public void removeEdge(Edge e) {
            edges.remove(e);
        }

        /**
         * edges of this vertex (both incoming and outgoing)
         */
        public Iterator<Edge> edges() {
            return edges.iterator();
        }

        abstract class EdgeFilter implements Iterator<Edge> {

            private Iterator<Edge> it = edges();
            private Edge next;

            public EdgeFilter() {
                findNext();
            }

            abstract boolean accept(Edge e);

            private void findNext() {
                while (it.hasNext()) {
                    Edge e = it.next();
                    if (accept(e)) {
                        next = e;
                        return;
                    }
                }
                next = null;
            }

            public boolean hasNext() {
                return next!=null;
            }

            public Edge next() {
                Edge curr = next;
                findNext();
                return curr;
            }

            public void remove() {
                throw new RuntimeException("not implemented");
            }

        }

        public Iterator<Edge> outgoingEdges() {
            return new EdgeFilter() {
                boolean accept(Edge e) {
                    return !e.to.equals(Vertex.this);
                }
            };
        }

        public Iterator<Edge> incomingEdges() {
            return new EdgeFilter() {
                boolean accept(Edge e) {
                    return !e.from.equals(this);
                }
            };
        }

        /**
         * returns number of outgoing edges of this vertex
         * i.e. edges of form *<----this
         */
        public int getNumOfOutgoingEdges() {
            int num = 0;
            Iterator<Edge> it = edges();
            while (it.hasNext()) {
                Edge e = (Edge) it.next();
                if (e.from.equals(this))
                    num++;
            }
            return num;
        }

        /**
         * returns number of incoming edges of this vertex
         * i.e. edges of form this<----*
         */
        public int getNumOfIncomingEdges() {
            int num = 0;
            Iterator<Edge> it = edges();
            while (it.hasNext()) {
                Edge e = (Edge) it.next();
                if (e.to.equals(this))
                    num++;
            }
            return num;
        }

        public Name getName() {
            return vname;
        }

        public void setAdditionalInfo(Info info) {
            additional_info = info;
        }

        public Info getAdditionalInfo() {
            return additional_info;
        }

        public boolean equals(Object o) {
            if (!Vertex.class.isInstance(o))
                return false;
            Vertex v = (Vertex) o;
            return vname.equals(v.vname);
        }

        public int hashCode() {
            return (vname==null?0:vname.hashCode());
        }

        //used for debuging only
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(vname.toString()+": \n");
            Iterator<Edge> it = edges();
            while (it.hasNext()) {
                Edge e = (Edge) it.next();
                ret.append(" "+e.toString());
            }
            ret.append("\n");
            return ret.toString();
        }

    }

    protected class Edge
        implements Serializable
    {

        private static final long serialVersionUID = 3871868896077968576L;

        /**
         * ending vertex of a directed edge
         * i.e. "i" in i<---j
         */
        private final Vertex to;

        /**
         * starting vertex of a directed edge
         * i.e. "j" in i<---j
         */
        private final Vertex from;

        /**
         * creates new to<---from edge
         */
        public Edge(Vertex to, Vertex from) {
            this.to = to;
            this.from = from;
        }

        public boolean equals(Object o) {
            if (!Edge.class.isInstance(o))
                return false;
            Edge e = (Edge) o;
            return
                from.equals(e.from) &&
                to.equals(e.to);
        }

        public int hashCode() {
            return (from==null?0:from.hashCode());
        }

        //used for debuging only
        public String toString() {
            return to.vname+"<---"+from.vname;
        }

        public Vertex getFrom() {
            return from;
        }

        public Vertex getTo() {
            return to;
        }

    }

    /**
     * maps user defined name of the vertex to
     * intrnally used class Vertex
     */
    protected Hashtable<Name, Vertex> vertices = new Hashtable<Name, Vertex>();


    public boolean hasVertex(Name vname) {
        return getVertices().contains(vname);
    }

    /**
     * Tests, whether for each vertex in the graph, there is a directed
     * path from the vertex to at least one of vertices given in the set
     * to_reach
     */
    public boolean isReachableForAllVertices(Set<Name> to_reach) {
        Set<Name> reached = getTransitivePathClosureOfIncomingEdges(to_reach);
        //all vertices must have a directed path to at least
        //one of vertices from set to_reach
        return reached.containsAll(getVertices());
    }

    public boolean isAcyclic() {
      Iterator<Name> it = getVertices().iterator();
      while ( it.hasNext() ) {
        Name name = it.next();
        Vertex testedv = getVertice(name);
        //construct set of vertices reachable with
        //a directed path from vertex testedv. if we
        //reached the testedv again, then there is a cycle
        Set<Vertex> reached = new HashSet<Vertex>();
        LinkedList<Vertex> queue = new LinkedList<Vertex>();
        queue.add(testedv);
        while ( !queue.isEmpty() ) {
          Vertex v = queue.removeFirst();
          reached.add(v);

          Iterator<Edge> eit = v.edges();
          while (eit.hasNext()) {
            Edge e = (Edge) eit.next();
            if (e.getFrom().equals(v)) { //is outgoing edge
              if (e.getTo().equals(testedv)) {
                //there is a cycle, because we have reached the
                //testedv again
                return false;
              }
              else if (!reached.contains(e.getTo())) {
                //do not add already reached vertices
                queue.add(e.getTo());
              }
            }
          }
        }

      }
      //no directed cycle found
      return true;
    }

    /**
     * Returns list of all user's names (all vertices)
     * in the graph. The list is sorted according to
     * a topological order.
     *
     * Topological ordering '<<' is defined as follows:
     *   vertex1 << vertex2  iff there is an directed edge
     *   path from vertex2 to vertex1.
     *
     * This algorithm works only for acyclic graphs.
     */
    public List<Name> getTopologicalyOrderedVertices() {
      LinkedList<Name> ordered_names = new LinkedList<Name>();

      //find a vertex with no incoming edge
      //remember the vertex in ordered_names
      //and then remove the vertex and all its edges
      //from the graph
      DirectedGraph<Name,Info> dr = new DirectedGraph<Name,Info>(this);
      while (dr.getNumOfVertices() > 0) {
        //find a vertex with no incoming edge
        //there is always such an edge
        //(because the graph is acyclic)
        Iterator<Name> vnames_en = dr.getVertices().iterator();
        while (vnames_en.hasNext()) {
          Name vname = vnames_en.next();
          Vertex v = dr.getVertice(vname);
          if (v.getNumOfOutgoingEdges()==0) {
            ordered_names.add(vname);
            dr.removeVertex(vname);
            break;
          }
        }
      }

      return ordered_names;
    }

    /**
     * returns list of inner lists with vertices.
     * Vertices that belong to the same inner list
     * are at the same level in topological ordering.
     * The outer list is sorted wrt. topological ordering.
     *
     * Topological ordering '<<' is defined as follows:
     *   vertex1 << vertex2  iff there is an directed edge
     *   path from vertex2 to vertex1.
     *
     * This algorithm works only for acyclic graphs.
     */
    public List<List<Name>> getTopologicalVertexLevelOrdering() {
        List<List<Name>> ordered_names = new LinkedList<List<Name>>();

        //find a vertices with no incoming edge
        //remember these vertices in list 'ordered_names'
        //and then remove the vertices and all their edges
        //from the graph
        DirectedGraph<Name,Info> dr = new DirectedGraph<Name,Info>(this);
        while (dr.getNumOfVertices() > 0) {
            //find vertices with no incoming edge
            //there is always at least one such an edge
            //(because the graph is acyclic)
            LinkedList<Name> vertices_no_incoming = new LinkedList<Name>();
            Iterator<Name> vnames_it = dr.getVertices().iterator();
            while (vnames_it.hasNext()) {
                Name vname = vnames_it.next();
                Vertex v = dr.getVertice(vname);
                if (v.getNumOfOutgoingEdges()==0) {
                    vertices_no_incoming.add(vname);
                }
            }
            Iterator<Name> it = vertices_no_incoming.iterator();
            while (it.hasNext()) {
                Name vname = it.next();
                dr.removeVertex(vname);
            }
            ordered_names.add(vertices_no_incoming);
        }

        return ordered_names;
    }

    public List<Name> getVerticesWithoutIncomingEdges() {
          LinkedList<Name> ordered_names = new LinkedList<Name>();

         Iterator<Name> vnames_it = getVertices().iterator();
         while (vnames_it.hasNext()) {
           Name vname = vnames_it.next();
           Vertex v = getVertice(vname);
           if (v.getNumOfIncomingEdges()==0) {
               ordered_names.add(vname);
              }
         }
         return ordered_names;
    }

    public Set<Name> getVerticesWithoutOutgoingEdges() {
        Set<Name> ordered_names = new LinkedHashSet<Name>();

       Iterator<Name> vnames_it = getVertices().iterator();
       while (vnames_it.hasNext()) {
         Name vname = vnames_it.next();
         Vertex v = getVertice(vname);
         if (v.getNumOfOutgoingEdges()==0) {
             ordered_names.add(vname);
            }
       }
       return ordered_names;
    }

    /**
     * Returns a set of vertices, such that for each vertex in the set,
     * there is a directed path from the vertex to at least one of
     * vertices given in the set 'closure_of'
     */
    public Set<Name> getTransitivePathClosureOfIncomingEdges(Set<Name> closure_of) {
      return getTransitivePathClosure(closure_of, true);
    }

    /**
     * Returns a set of vertices, such that there is a directed path
     * from at least one of vertices given in the set 'closure_of'
     * to each vertex in the returned set.
     */
    public Set<Name> getTransitivePathClosureOfOutgoingEdges(Set<Name> closure_of) {
      return getTransitivePathClosure(closure_of, false);
    }

    /**
     * Constructs a path closure following only either incoming or
     * outgoing edges
     */
    private Set<Name> getTransitivePathClosure(Set<Name> closure_of,
                                                boolean closure_of_incoming_edges)
    {
        Iterator<Name> it = closure_of.iterator();
        LinkedList<Vertex> queue = new LinkedList<Vertex>();
        while (it.hasNext()) {
            Name vname = it.next();
            Vertex v = getVertice(vname);
            queue.add(v);
        }

        //construct a set of vertices. A vertex 'v' will be contained
        //in the set, if there is an directed path from vertex 'v' to
        //at least one of vertices given in set to_reach
        Set<Name> reached = new HashSet<Name>();
        while (!queue.isEmpty()) {
            Vertex v = queue.removeFirst();
            reached.add(v.getName());

            //add vertices from all edges incoming
            //to the current vertex
            Iterator<Edge> eit = v.edges();
            while (eit.hasNext()) {
                Edge e = eit.next();

                if (closure_of_incoming_edges &&
                    e.getTo().equals(v)) //is incoming edge
                {
                    if (!reached.contains(e.getFrom().getName())) {
                        //do not add already reached vertices
                        queue.add(e.getFrom());
                    }
                }
                if (!closure_of_incoming_edges &&
                    e.getFrom().equals(v)) //is outgoing edge
                {
                    if (!reached.contains(e.getTo().getName())) {
                        //do not add already reached vertices
                        queue.add(e.getTo());
                    }
                }

            }
        }

        return reached;
    }

    public Set<Name> getInputs(Name l)
    {
        Vertex v = getVertice(l);

        Set<Name> inputs = new HashSet<Name>();
        Iterator<Edge> eit = v.edges();
        while (eit.hasNext()) {
            Edge e = eit.next();
            inputs.add(e.getFrom().getName());
        }

        return inputs;
    }

    public Set<Name> getOutputs(Name l)
    {
        Vertex v = getVertice(l);

        Set<Name> outputs = new HashSet<Name>();
        Iterator<Edge> eit = v.outgoingEdges();
        while (eit.hasNext()) {
            Edge e = eit.next();
            outputs.add(e.to.getName());
        }

        return outputs;
    }

    /**
     * Decomposes graph into connected components.
     *
     * Returns sets of vertices. Vertices in the same
     * set belong to the same connected component
     */
    public Set<Set<Name>> getConnectedComponents() {

        //find connected components with a depth search
        Hashtable<Vertex, Set<Vertex>> vertex2component = new Hashtable<Vertex, Set<Vertex>>();
        Iterator<Name> vit = getVertices().iterator();
        while (vit.hasNext()) {
            Name vname = vit.next();
            Vertex v = getVertice(vname);
            if (!vertex2component.containsKey(v)) {
                //vertex has not been visited yet
                Set<Vertex> component = new HashSet<Vertex>();
                component.add(v);
                vertex2component.put(v, component);
                createConnectedSubComponents(v, vertex2component);
            }
        }

        //collect components that were found
        Set<Set<Name>> components = new HashSet<Set<Name>>();
        Enumeration<Set<Vertex>> cen = vertex2component.elements();
        while (cen.hasMoreElements()) {
            Set<Name> component_names = new HashSet<Name>();
            Set<Vertex> component_vertices = cen.nextElement();
            Iterator<Vertex> it = component_vertices.iterator();
            while (it.hasNext()) {
                Vertex v = it.next();
                component_names.add(v.getName());
            }
            components.add(component_names);
        }

        return components;
    }

    /**
     * Does a depth search through a directed graph, assigning
     * vertices into components, i.e. fills the hastable
     * vertex2component
     */
    private void createConnectedSubComponents(Vertex from,
                                              Hashtable<Vertex, Set<Vertex>> vertex2component)
    {
        //do a depth search through the directed graph
        Iterator<Edge> eit = from.edges();
        while (eit.hasNext()) {
            Edge e = eit.next();
            if (e.getFrom().equals(from)) { //is outgoing edge
                Vertex to = e.getTo();
                if (!vertex2component.containsKey(to)) {
                    //vertex is not assigned to a component yet,
                    //(we didn't visit this vertex yet)
                    //so assign the vertex 'to' to a component
                    //of vertex 'from'
                    Set<Vertex> component = vertex2component.get(from);
                    component.add(to);
                    vertex2component.put(to, component);
                    //do a depth search for vertices
                    createConnectedSubComponents(to, vertex2component);
                }
                else {
                    Set<Vertex> component1 = vertex2component.get(from);
                    Set<Vertex> component2 = vertex2component.get(to);
                    if (component1!=component2) {
                        //vertex is already assigned in a differenf component,
                        //but there is a connected path between component1 and
                        //component2, so join the components
                        Iterator<Vertex> vit = component2.iterator();
                        while (vit.hasNext()) {
                            Vertex v = vit.next();
                            vertex2component.put(v, component1);
                        }
                        component1.addAll(component2);
                    }
                    else {
                        //vertex is already assigned in correct component
                    }
                }
            }
        }
    }

    public DirectedGraph(DirectedGraph<Name,Info> adg) {
        Iterator<Name> vnames_it = adg.getVertices().iterator();
        while (vnames_it.hasNext()) {
            Name vname = vnames_it.next();
            Vertex v = adg.getVertice(vname);
            Info info = v.getAdditionalInfo();
            addVertex(vname);
            setVertexAdditionalInfo(vname, info);
        }
        //copy edges
        //it is sufficient to copy for each vertex only
        //incoming edges
        vnames_it = adg.getVertices().iterator();
        while (vnames_it.hasNext()) {
            Name vname1 = vnames_it.next();
            Vertex v1 = adg.getVertice(vname1);
            Iterator<Edge> edges_it = v1.edges();
            while (edges_it.hasNext()) {
                Edge e = edges_it.next();
                if (e.getTo().equals(v1)) {
                    //e in incoming edge for v1
                    Vertex v2 = e.getFrom();
                    //e is edge of form v1<----v2
                    Name vname2 = v2.getName();
                    addEdge(vname1, vname2);
                }
            }
        }
    }

    public DirectedGraph() {
    }

    public boolean containsVertex(Name vname) {
        return vertices.containsKey(vname);
    }

    public void addVertex(Name vname) {
        if (vertices.containsKey(vname))
            throw new RuntimeException("node with this name already exists in the graph");
        vertices.put(vname, new Vertex(vname));
    }

    public void removeVertex(Name vname) {
        //remove the vertex
        Vertex vertex = vertices.get(vname);
        vertices.remove(vname);

        //remove all edges of removed vertex
        Iterator<Edge> it = vertex.edges();
        while (it.hasNext()) {
            Edge e = it.next();
            if (e.to.equals(vertex)) {
                e.from.removeEdge(e);
            }
            else {
                e.to.removeEdge(e);
            }
        }
    }

    public void clear() {
        vertices.clear();
    }

    /**
     * tests whether a vertex is a leaf in directed
     * graph (has no incoming edges)
     */
    public boolean isLeaf(Name vname) {
        Vertex vertex = vertices.get(vname);
        return vertex.getNumOfIncomingEdges()==0;
    }

    /**
     * attaches to the vertex an additional user's information
     */
    public void setVertexAdditionalInfo(Name vname, Info info) {
        Vertex vertex = vertices.get(vname);
        vertex.setAdditionalInfo(info);
    }

    /**
     * retrieves from the vertex an additional user's information
     */
    public Info getVertexAdditionalInfo(Name vname) {
        Vertex vertex = vertices.get(vname);
        return vertex.getAdditionalInfo();
    }

    /**
     * Adds an i<----j edge to the graph.
     * Vertices must already exist.
     */
    public void addEdge(Name vname1, Name vname2) {
        Vertex vertex1 = vertices.get(vname1);
        Vertex vertex2 = vertices.get(vname2);

        //create vertex1<----vertex2 edge
        Edge e = new Edge(/*to*/ vertex1, /*from*/ vertex2);
        vertex1.addEdge(e);
        vertex2.addEdge(e);
    }


    /**
     * returns list of all user's names (all vertices)
     * in the graph
     */
    public Set<Name> getVertices() {
        return vertices.keySet();
    }

    /**
     * returns the vertex with the specified name,
     * null if the name is not present.
     */
    protected Vertex getVertice(Name name) {
        return vertices.get(name);
    }

    /**
     * returns number of vertices in the graph
     */
    public int getNumOfVertices() {
        return vertices.size();
    }

    /**
     * create copy of the graph
     */
    public Object clone() {
        DirectedGraph<Name, Info> dr = new DirectedGraph<Name, Info>(this);
        return dr;
    }

    //used for debuging only
    public String toString() {
        String ret = "";
        Enumeration<Name> en = vertices.keys();
        while (en.hasMoreElements()) {
            Name vname = en.nextElement();
            Vertex v = vertices.get(vname);
            ret += v.toString();
        }
        return ret;
    }

}// DirectedGraph
