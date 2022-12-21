import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.EdgeType;

import org.apache.commons.collections15.Factory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Graph representation using Adjacency lists and node lists.
 * 
 * @author Jafar Moukalled
 */
class ThreeTenGraph implements Graph<GraphNode, GraphEdge>, UndirectedGraph<GraphNode, GraphEdge> {

    /**
     * Maximum number of allowed nodes.
     */
    private static final int MAX_NUMBER_OF_NODES = 200;

    /**
     * Linked list of nodes representing vertices.
     */
    private LinkedList<GraphNode> nodeList = null;

    /**
     * Linked list of destinations representing edges and their endpoints.
     */
    private LinkedList<Destination>[] adjList = null;

    /**
     * Representation of edge/vertex destination.
     */
    private class Destination {

        /**
         * Vertex of the destination object.
         */
        GraphNode node;

        /**
         * Edge of the destination object.
         */
        GraphEdge edge;

        /**
         * Create a destination given a node and an edge.
         * 
         * @param n vertex
         * @param e edge
         */
        Destination(GraphNode n, GraphEdge e) {
            this.node = n;
            this.edge = e;
        }
    }

    /**
     * Default constructor.
     */
    @SuppressWarnings("unchecked")
    public ThreeTenGraph() {
        // instantiate both lists
        this.nodeList = new LinkedList<GraphNode>();
        this.adjList = (LinkedList<Destination>[]) new LinkedList[MAX_NUMBER_OF_NODES];
    }

    /**
     * Returns a view of all edges in this graph. In general, this
     * obeys the Collection contract, and therefore makes no guarantees
     * about the ordering of the vertices within the set.
     * 
     * @return a Collection view of all edges in this graph
     */
    public Collection<GraphEdge> getEdges() {
        LinkedList<GraphEdge> edges = new LinkedList<GraphEdge>(); // linked list of all edges in the graph
        ListIterator<GraphNode> itr = this.nodeList.listIterator(); // list iterator for nodes in the list
        ListIterator<Destination> destItr = null; // list iterator for destinations to grab edges
        GraphNode tempNode = null; // temp graph node to store info
        Destination tempDest = null; // temp destination node to store info

        // for each node in nodeList, go to that index in adjList
        while (itr.hasNext()) {

            // set temp node
            tempNode = itr.next();

            // set the eItr for index of this node ID
            destItr = this.adjList[tempNode.id].listIterator();

            // for each destination from this node, record the edge
            while (destItr.hasNext()) {

                // set destination node
                tempDest = destItr.next();

                // make sure edge is already not in the list
                if (!edges.contains(tempDest.edge)) {
                    // add edge to the list
                    edges.add(tempDest.edge);
                }
            } // end while dest
        } // end while nodes

        return edges;
    } // end getEdges

    /**
     * Returns a view of all vertices in this graph. In general, this
     *  obeys the Collection contract, and therefore makes no guarantees
     * about the ordering of the vertices within the set.
     * @return Collection of graph nodes
     */ 
    public Collection<GraphNode> getVertices() {

        // create a new list
        LinkedList<GraphNode> vertices = new LinkedList<GraphNode>();

        // create iterator for nodeList
        ListIterator<GraphNode> itr = this.nodeList.listIterator();

        // for each node
        while (itr.hasNext()) {
            // add all vertices
            vertices.add(itr.next());
        }

        return vertices;
    } // end getVertices

    /**
     * Returns the number of edges in this graph.
     * 
     * @return the number of edges in this graph
     */
    public int getEdgeCount() {
        ListIterator<GraphNode> itr = this.nodeList.listIterator(); // list iterator for nodes in the list
        GraphNode temp = null; // temp graph node to store info
        int numEdges = 0;

        // for each node in the list
        while (itr.hasNext()) {
            // set temp node
            temp = itr.next();

            // get size of each adjList according to node
            numEdges += this.adjList[temp.id].size();
        }

        // the answer is edges / 2 since this is an undirected graph and edges go both
        // ways every time
        numEdges /= 2;
        return numEdges;
    } // end getEdgeCount

    /**
     * Returns the number of vertices in this graph.
     * 
     * @return the number of vertices in this graph
     */
    public int getVertexCount() {
        return this.nodeList.size();
    }// end getVertexCount

    /**
     * Returns true if this graph's vertex collection contains vertex.
     * Equivalent to getVertices().contains(vertex).
     * 
     * @param vertex the vertex whose presence is being queried
     * @return true iff this graph contains a vertex vertex
     */
    public boolean containsVertex(GraphNode vertex) {
        // just use .conatains method in collections framework
        if (this.nodeList.contains(vertex)) {
            return true;
        } else {
            return false;
        }
    } // end containsVertex

    /**
     * Returns the collection of vertices which are connected to vertex
     * via any edges in this graph.
     * If vertex is connected to itself with a self-loop, then
     * it will be included in the collection returned.
     * 
     * @param vertex the vertex whose neighbors are to be returned
     * @return the collection of vertices which are connected to vertex,
     *         or null if vertex is not present
     */
    public Collection<GraphNode> getNeighbors(GraphNode vertex) {

        // check if vertex is present
        if (!this.nodeList.contains(vertex)) {
            return null;
        }

        // declare iterator for destinations for this node
        ListIterator<Destination> itr = this.adjList[vertex.id].listIterator();
        LinkedList<GraphNode> neighbors = new LinkedList<GraphNode>();

        // for each destination of the vertex
        while (itr.hasNext()) {
            // add each vertex to neighbors
            neighbors.add(itr.next().node);
        }

        return neighbors;
    } // end getNeighbors

    /**
     * Returns the number of vertices that are adjacent to vertex
     * (that is, the number of vertices that are incident to edges in vertex's
     * incident edge set).
     * 
     * 
     * <p>Equivalent to getNeighbors(vertex).size().
     * 
     * @param vertex the vertex whose neighbor count is to be returned
     * @return the number of neighboring vertices
     */
    public int getNeighborCount(GraphNode vertex) {
        // just get size of adjList at vertex index and return
        int numNeighbors = this.adjList[vertex.id].size();

        return numNeighbors;
    }// end getNeighborCount

    /**
     * Returns the collection of edges in this graph which are connected to vertex.
     * 
     * @param vertex the vertex whose incident edges are to be returned
     * @return the collection of edges which are connected to vertex,
     *         or null if vertex is not present
     */
    public Collection<GraphEdge> getIncidentEdges(GraphNode vertex) {
        // check if vertex is present
        if (!this.nodeList.contains(vertex)) {
            return null;
        }

        ListIterator<Destination> itr = this.adjList[vertex.id].listIterator();//iterator for destination nodes in adjList
        LinkedList<GraphEdge> incidentEdges = new LinkedList<GraphEdge>(); // empty list of incident edges
        Destination temp = null; // temp destination node to store info about edges

        // for each destination
        while (itr.hasNext()) {
            // set temp node
            temp = itr.next();

            // add edge to the list
            incidentEdges.add(temp.edge);
        }
        return incidentEdges;
    } // end getIncidentEdges

    /**
     * Returns the endpoints of edge as a Pair.
     * 
     * @param edge the edge whose endpoints are to be returned
     * @return the endpoints (incident vertices) of edge
     */
    public Pair<GraphNode> getEndpoints(GraphEdge edge) {
        // check if edge is null
        if (edge == null) {
            return null;
        }

        // delare necessary iterators and objects
        LinkedList<GraphNode> nodes = new LinkedList<GraphNode>();
        ListIterator<GraphNode> nodeItr = this.nodeList.listIterator();
        ListIterator<Destination> destItr = null;
        GraphNode v = null;
        Destination d = null;

        // for each node in nodeList
        while (nodeItr.hasNext()) {

            // set temp node
            v = nodeItr.next();

            // set second iterator for destinations
            destItr = this.adjList[v.id].listIterator();

            // for each destination at the index
            while (destItr.hasNext()) {

                // set temp dest node
                d = destItr.next();

                // add the node to nodes if the edge dest matches param
                if (d.edge.equals(edge)) {
                    nodes.add(d.node);
                }
            } // end while2
        } // end while1

        // create pair with the linked list
        Pair<GraphNode> endpoints = new Pair<GraphNode>(nodes);

        return endpoints;
    }// end getEndpoints

    /**
     * Returns an edge that connects v1 to v2.
     * If this edge is not uniquely
     * defined (that is, if the graph contains more than one edge connecting
     * v1 to v2), any of these edges
     * may be returned. findEdgeSet(v1, v2) may be
     * used to return all such edges.
     * Returns null if either of the following is true:
     * <ul>
     * <li/>v1 is not connected to v2
     * <li/>either v1 or v2 are not present in this graph
     * </ul>
     * 
     * <p><b>Note</b>: for purposes of this method, v1 is only considered to be
     * connected to
     * v2 via a given <i>directed</i> edge e if
     * v1 == e.getSource() && v2 == e.getDest() evaluates to true.
     * (v1 and v2 are connected by an undirected edge u if
     * u is incident to both v1 and v2.)
     * 
     * @param v1 vertex 1
     * @param v2 vertex 2
     * @return an edge that connects v1 to v2,
     *         or null if no such edge exists (or either vertex is not present)
     * @see Hypergraph#findEdgeSet(Object, Object)
     */
    public GraphEdge findEdge(GraphNode v1, GraphNode v2) {

        // check if both vertices exist in the graph
        if (!this.nodeList.contains(v1) || !this.nodeList.contains(v2)|| (!this.nodeList.contains(v1) && !this.nodeList.contains(v2))) {
            return null;
        }

        ListIterator<Destination> itr = this.adjList[v1.id].listIterator(); // list iterator for destinations to grab edges
        Destination temp = null;
        GraphEdge e = null;

        // for each destination
        while (itr.hasNext()) {
            // set temp
            temp = itr.next();

            // find v2
            if (temp.node.equals(v2)) {
                // found edge
                e = temp.edge;

                // break out of loop
                break;
            }
        } // end while
        return e;
    }

    /**
     * Returns true if vertex and edge
     * are incident to each other.
     * Equivalent to getIncidentEdges(vertex).contains(edge) and to
     * getIncidentVertices(edge).contains(vertex).
     * 
     * @param vertex v
     * @param edge e
     * @return true if vertex and edge
     *         are incident to each other
     */
    public boolean isIncident(GraphNode vertex, GraphEdge edge) {

        // using previous methods I created
        if (this.getIncidentEdges(vertex).contains(edge) && this.getIncidentVertices(edge).contains(vertex)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds edge e to this graph such that it connects
     * vertex v1 to v2.
     * Equivalent to addEdge(e, new Pair(v1, v2)).
     * If this graph does not contain v1, v2,
     * or both, implementations may choose to either silently add
     * the vertices to the graph or throw an IllegalArgumentException.
     * If this graph assigns edge types to its edges, the edge type of
     * e will be the default for this graph.
     * See Hypergraph.addEdge() for a listing of possible reasons
     * for failure.
     * 
     * @param e  the edge to be added
     * @param v1 the first vertex to be connected
     * @param v2 the second vertex to be connected
     * @return true if the add is successful, false otherwise
     * @see Hypergraph#addEdge(Object, Collection)
     * @see #addEdge(Object, Object, Object, EdgeType)
     */
    public boolean addEdge(GraphEdge e, GraphNode v1, GraphNode v2) {

        // check if both vertices exist in the graph
        if (!this.nodeList.contains(v1) || !this.nodeList.contains(v2)|| (!this.nodeList.contains(v1) && !this.nodeList.contains(v2))) {
            throw new IllegalArgumentException();
        }

        // check if the edge already exists in this graph
        if (this.getEdges().contains(e)) {
            return false;
        }

        // check if the vertices are already connected by an edge
        if (this.isIncident(v1, e) && this.isIncident(v2, e)) {
            return false;
        }

        // add destination for v1
        this.adjList[v1.id].add(new Destination(v2, e));

        // add destination for v2
        this.adjList[v2.id].add(new Destination(v1, e));

        return true;
    }

    /**
     * Adds vertex to this graph.
     * Fails if vertex is null or already in the graph.
     * 
     * @param vertex the vertex to add
     * @return true if the add is successful, and false otherwise
     * @throws IllegalArgumentException if vertex is null
     */
    public boolean addVertex(GraphNode vertex) {
        // check if vertex is null or already in the graph
        if (vertex == null) {
            throw new IllegalArgumentException();
        }
        if (this.nodeList.contains(vertex)) {
            return false;
        }

        // check if node number does not exceed the limit
        if ((this.getVertexCount() + 1) >= MAX_NUMBER_OF_NODES) {
            return false;
        }

        // add vertex in nodeList
        this.nodeList.add(vertex);

        // add vertex space in adjList
        this.adjList[vertex.id] = new LinkedList<Destination>();

        return true;
    }

    /**
     * Removes edge from this graph.
     * Fails if edge is null, or is otherwise not an element of this graph.
     * 
     * @param edge the edge to remove
     * @return true if the removal is successful, false otherwise
     */
    public boolean removeEdge(GraphEdge edge) {

        GraphNode v = null;
        Destination d = null;
        ListIterator<Destination> itr2 = null;
        ListIterator<GraphNode> itr = null;
        LinkedList<GraphNode> vertices = new LinkedList<GraphNode>();

        // check if edge is null
        if (edge == null) {
            return false;
        }

        // check if the edge is not in the graph
        if (!this.getEdges().contains(edge)) {
            return false;
        }

        // get the vertices that are incident to the edge
        vertices.addAll(this.getIncidentVertices(edge));

        // get the list iterator for the vertices
        itr = vertices.listIterator();

        // for each vertex
        while (itr.hasNext()) {

            // set the temp vertex
            v = itr.next();

            // set second iterator for adjList
            itr2 = this.adjList[v.id].listIterator();

            while (itr2.hasNext()) {
                // set temp destination
                d = itr2.next();

                // if the destination edge equals passed edge, then remove the node and break
                // the loop
                if (d.edge.equals(edge)) {
                    this.adjList[v.id].remove(d);
                    break;
                }
            } // end while 2
        } // end while 1
        return true;
    } // end removeEdge

    /**
     * Removes vertex from this graph.
     * As a side effect, removes any edges e incident to vertex if the
     * removal of vertex would cause e to be incident to an illegal
     * number of vertices. (Thus, for example, incident hyperedges are not removed,
     * but
     * incident edges--which must be connected to a vertex at both endpoints--are
     * removed.)
     * 
     * 
     * <p>Fails under the following circumstances:
     * <ul>
     * <li/>vertex is not an element of this graph
     * <li/>vertex is null
     * </ul>
     * 
     * @param vertex the vertex to remove
     * @return true if the removal is successful, false otherwise
     */
    public boolean removeVertex(GraphNode vertex) {
        // check if vertex is null and if it is an element in the graph
        if (vertex == null) {
            return false;
        }
        if (!this.getVertices().contains(vertex)) {
            return false;
        }

        // in order to properly remove a vertex,
        // the edge connected to it must also be removed

        // check if there is an edge connected to the vertex
        if (this.getIncidentEdges(vertex).size() != 0) {
            ListIterator<GraphNode> itr = this.nodeList.listIterator();
            ListIterator<Destination> itr2 = null;
            GraphNode v = null;
            Destination d = null;

            // for every node in nodelist
            while (itr.hasNext()) {

                // set temp node
                v = itr.next();

                // create second iterator
                itr2 = this.adjList[v.id].listIterator();

                // for every destination node in specific index
                while (itr2.hasNext()) {

                    // set temp dest node
                    d = itr2.next();

                    // check if the vertex id matches the one in dest node
                    if (d.node.equals(vertex)) {
                        break;
                    }
                } // end while

                // remove the node and set boolean flag to true
                if (d.node.equals(vertex)) {
                    this.adjList[v.id].remove(d);
                }

            } // end while
        } // end if

        // set index in adjList to null
        this.adjList[vertex.id] = null;

        // remove in nodeList
        this.nodeList.remove(vertex);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return super.toString();
    }

    /**
     * Returns true if v1 and v2 share an incident edge.
     * Equivalent to getNeighbors(v1).contains(v2).
     * 
     * @param v1 the first vertex to test
     * @param v2 the second vertex to test
     * @return true if v1 and v2 share an incident edge
     */
    public boolean isNeighbor(GraphNode v1, GraphNode v2) {
        return (findEdge(v1, v2) != null);
    }

    /**
     * Returns true if this graph's edge collection contains edge.
     * Equivalent to getEdges().contains(edge).
     * 
     * @param edge the edge whose presence is being queried
     * @return true iff this graph contains an edge edge
     */
    public boolean containsEdge(GraphEdge edge) {
        return (getEndpoints(edge) != null);
    }

    /**
     * Returns the collection of edges in this graph which are of type edge_type.
     * 
     * @param edgeType the type of edges to be returned
     * @return the collection of edges which are of type edge_type, or
     *         null if the graph does not accept edges of this type
     * @see EdgeType
     */
    public Collection<GraphEdge> getEdges(EdgeType edgeType) {
        if (edgeType == EdgeType.UNDIRECTED) {
            return getEdges();
        }
        return null;
    }

    /**
     * Returns the number of edges of type edge_type in this graph.
     * 
     * @param edgeType the type of edge for which the count is to be returned
     * @return the number of edges of type edge_type in this graph
     */
    public int getEdgeCount(EdgeType edgeType) {
        if (edgeType == EdgeType.UNDIRECTED) {
            return getEdgeCount();
        }
        return 0;
    }

    /**
     * Returns the number of edges incident to vertex.
     * Special cases of interest:
     * <ul>
     * <li/>Incident self-loops are counted once.
     * <li>If there is only one edge that connects this vertex to
     * each of its neighbors (and vice versa), then the value returned
     * will also be equal to the number of neighbors that this vertex has
     * (that is, the output of getNeighborCount).
     * <li>If the graph is directed, then the value returned will be
     * the sum of this vertex's indegree (the number of edges whose
     * destination is this vertex) and its outdegree (the number
     * of edges whose source is this vertex), minus the number of
     * incident self-loops (to avoid double-counting).
     * </ul>
     * 
     * <p>Equivalent to getIncidentEdges(vertex).size().
     * 
     * @param vertex the vertex whose degree is to be returned
     * @return the degree of this node
     * @see Hypergraph#getNeighborCount(Object)
     */
    public int degree(GraphNode vertex) {
        return getNeighborCount(vertex);
    }

    /**
     * Returns a Collection view of the predecessors of vertex
     * in this graph. A predecessor of vertex is defined as a vertex v
     * which is connected to
     * vertex by an edge e, where e is an outgoing edge of
     * v and an incoming edge of vertex.
     * 
     * @param vertex the vertex whose predecessors are to be returned
     * @return a Collection view of the predecessors of
     *         vertex in this graph
     */
    public Collection<GraphNode> getPredecessors(GraphNode vertex) {
        return getNeighbors(vertex);
    }

    /**
     * Returns a Collection view of the successors of vertex
     * in this graph. A successor of vertex is defined as a vertex v
     * which is connected to
     * vertex by an edge e, where e is an incoming edge of
     * v and an outgoing edge of vertex.
     * 
     * @param vertex the vertex whose predecessors are to be returned
     * @return a Collection view of the successors of
     *         vertex in this graph
     */
    public Collection<GraphNode> getSuccessors(GraphNode vertex) {
        return getNeighbors(vertex);
    }

    /**
     * Returns true if v1 is a predecessor of v2 in this graph.
     * Equivalent to v1.getPredecessors().contains(v2).
     * 
     * @param v1 the first vertex to be queried
     * @param v2 the second vertex to be queried
     * @return true if v1 is a predecessor of v2, and false otherwise.
     */
    public boolean isPredecessor(GraphNode v1, GraphNode v2) {
        return isNeighbor(v1, v2);
    }

    /**
     * Returns true if v1 is a successor of v2 in this graph.
     * Equivalent to v1.getSuccessors().contains(v2).
     * 
     * @param v1 the first vertex to be queried
     * @param v2 the second vertex to be queried
     * @return true if v1 is a successor of v2, and false otherwise.
     */
    public boolean isSuccessor(GraphNode v1, GraphNode v2) {
        return isNeighbor(v1, v2);
    }

    /**
     * If directed_edge is a directed edge in this graph, returns the source;
     * otherwise returns null.
     * The source of a directed edge d is defined to be the vertex for which
     * d is an outgoing edge.
     * directed_edge is guaranteed to be a directed edge if
     * its EdgeType is DIRECTED.
     * 
     * @param directedEdge the directed edge
     * @return the source of directed_edge if it is a directed edge in this graph,
     *         or null otherwise
     */
    public GraphNode getSource(GraphEdge directedEdge) {
        return null;
    }

    /**
     * If directed_edge is a directed edge in this graph, returns the destination;
     * otherwise returns null.
     * The destination of a directed edge d is defined to be the vertex
     * incident to d for which
     * d is an incoming edge.
     * directed_edge is guaranteed to be a directed edge if
     * its EdgeType is DIRECTED.
     * 
     * @param directedEdge the directed edge
     * @return the destination of directed_edge if it is a directed edge in this
     *         graph, or null otherwise
     */
    public GraphNode getDest(GraphEdge directedEdge) {
        return null;
    }

    /**
     * Returns a Collection view of the incoming edges incident to vertex
     * in this graph.
     * 
     * @param vertex the vertex whose incoming edges are to be returned
     * @return a Collection view of the incoming edges incident
     *         to vertex in this graph
     */
    public Collection<GraphEdge> getInEdges(GraphNode vertex) {
        return getIncidentEdges(vertex);
    }

    /**
     * Returns the collection of vertices in this graph which are connected to edge.
     * Note that for some graph types there are guarantees about the size of this
     * collection
     * (i.e., some graphs contain edges that have exactly two endpoints, which may
     * or may
     * not be distinct). Implementations for those graph types may provide alternate
     * methods
     * that provide more convenient access to the vertices.
     * 
     * @param edge the edge whose incident vertices are to be returned
     * @return the collection of vertices which are connected to edge,
     *         or null if edge is not present
     */
    public Collection<GraphNode> getIncidentVertices(GraphEdge edge) {

        Pair<GraphNode> p = getEndpoints(edge);
        if (p == null)
            return null;

        LinkedList<GraphNode> ret = new LinkedList<>();
        ret.add(p.getFirst());
        ret.add(p.getSecond());
        return ret;
    }

    /**
     * Returns a Collection view of the outgoing edges incident to vertex
     * in this graph.
     * 
     * @param vertex the vertex whose outgoing edges are to be returned
     * @return a Collection view of the outgoing edges incident
     *         to vertex in this graph
     */
    public Collection<GraphEdge> getOutEdges(GraphNode vertex) {
        return getIncidentEdges(vertex);
    }

    /**
     * Returns the number of incoming edges incident to vertex.
     * Equivalent to getInEdges(vertex).size().
     * 
     * @param vertex the vertex whose indegree is to be calculated
     * @return the number of incoming edges incident to vertex
     */
    public int inDegree(GraphNode vertex) {
        return degree(vertex);
    }

    /**
     * Returns the number of outgoing edges incident to vertex.
     * Equivalent to getOutEdges(vertex).size().
     * 
     * @param vertex the vertex whose outdegree is to be calculated
     * @return the number of outgoing edges incident to vertex
     */
    public int outDegree(GraphNode vertex) {
        return degree(vertex);
    }

    /**
     * Returns the number of predecessors that vertex has in this graph.
     * Equivalent to vertex.getPredecessors().size().
     * 
     * @param vertex the vertex whose predecessor count is to be returned
     * @return the number of predecessors that vertex has in this graph
     */
    public int getPredecessorCount(GraphNode vertex) {
        return degree(vertex);
    }

    /**
     * Returns the number of successors that vertex has in this graph.
     * Equivalent to vertex.getSuccessors().size().
     * 
     * @param vertex the vertex whose successor count is to be returned
     * @return the number of successors that vertex has in this graph
     */
    public int getSuccessorCount(GraphNode vertex) {
        return degree(vertex);
    }

    /**
     * Returns the vertex at the other end of edge from vertex.
     * (That is, returns the vertex incident to edge which is not vertex.)
     * 
     * @param vertex the vertex to be queried
     * @param edge   the edge to be queried
     * @return the vertex at the other end of edge from vertex
     */
    public GraphNode getOpposite(GraphNode vertex, GraphEdge edge) {
        Pair<GraphNode> p = getEndpoints(edge);
        if (p.getFirst().equals(vertex)) {
            return p.getSecond();
        } else {
            return p.getFirst();
        }
    }

    /**
     * Returns all edges that connects v1 to v2.
     * If this edge is not uniquely
     * defined (that is, if the graph contains more than one edge connecting
     * v1 to v2), any of these edges
     * may be returned. findEdgeSet(v1, v2) may be
     * used to return all such edges.
     * Returns null if v1 is not connected to v2.
     * <br/>
     * Returns an empty collection if either v1 or v2 are not present in this graph.
     * 
     * 
     * <p><b>Note</b>: for purposes of this method, v1 is only considered to be
     * connected to
     * v2 via a given <i>directed</i> edge d if
     * v1 == d.getSource() && v2 == d.getDest() evaluates to true.
     * (v1 and v2 are connected by an undirected edge u if
     * u is incident to both v1 and v2.)
     * @param v1 vertex 1
     * @param v2 vertex 2
     * @return a collection containing all edges that connect v1 to v2,
     *         or null if either vertex is not present
     * @see Hypergraph#findEdge(Object, Object)
     */
    public Collection<GraphEdge> findEdgeSet(GraphNode v1, GraphNode v2) {
        GraphEdge edge = findEdge(v1, v2);
        if (edge == null) {
            return null;
        }

        LinkedList<GraphEdge> ret = new LinkedList<>();
        ret.add(edge);
        return ret;

    }

    /**
     * Returns true if vertex is the source of edge.
     * Equivalent to getSource(edge).equals(vertex).
     * 
     * @param vertex the vertex to be queried
     * @param edge   the edge to be queried
     * @return true iff vertex is the source of edge
     */
    public boolean isSource(GraphNode vertex, GraphEdge edge) {
        return getSource(edge).equals(vertex);
    }

    /**
     * Returns true if vertex is the destination of edge.
     * Equivalent to getDest(edge).equals(vertex).
     * 
     * @param vertex the vertex to be queried
     * @param edge   the edge to be queried
     * @return true iff vertex is the destination of edge
     */
    public boolean isDest(GraphNode vertex, GraphEdge edge) {
        return getDest(edge).equals(vertex);
    }

    /**
     * Adds edge e to this graph such that it connects
     * vertex v1 to v2.
     * Equivalent to addEdge(e, new Pair(v1, v2)).
     * If this graph does not contain v1, v2,
     * or both, implementations may choose to either silently add
     * the vertices to the graph or throw an IllegalArgumentException.
     * If edgeType is not legal for this graph, this method will
     * throw IllegalArgumentException.
     * See Hypergraph.addEdge() for a listing of possible reasons
     * for failure.
     * 
     * @param e        the edge to be added
     * @param v1       the first vertex to be connected
     * @param v2       the second vertex to be connected
     * @param edgeType the type to be assigned to the edge
     * @return true if the add is successful, false otherwise
     * @see Hypergraph#addEdge(Object, Collection)
     * @see #addEdge(Object, Object, Object)
     */
    public boolean addEdge(GraphEdge e, GraphNode v1, GraphNode v2, EdgeType edgeType) {
        // NOTE: Only directed edges allowed

        if (edgeType == EdgeType.DIRECTED) {
            throw new IllegalArgumentException();
        }

        return addEdge(e, v1, v2);
    }

    /**
     * Adds edge to this graph.
     * Fails under the following circumstances:
     * <ul>
     * <li/>edge is already an element of the graph
     * <li/>either edge or vertices is null
     * <li/>vertices has the wrong number of vertices for the graph type
     * <li/>vertices are already connected by another edge in this graph,
     * and this graph does not accept parallel edges
     * </ul>
     * 
     * @param edge given edge
     * @param vertices endpoints
     * @return true if the add is successful, and false otherwise
     * @throws IllegalArgumentException if edge or vertices is null,
     *                                  or if a different vertex set in this graph
     *                                  is already connected by edge,
     *                                  or if vertices are not a legal vertex set
     *                                  for edge
     */
    @SuppressWarnings("unchecked")
    public boolean addEdge(GraphEdge edge, Collection<? extends GraphNode> vertices) {
        if (edge == null || vertices == null || vertices.size() != 2) {
            return false;
        }

        GraphNode[] vs = (GraphNode[]) vertices.toArray();
        return addEdge(edge, vs[0], vs[1]);
    }

    /**
     * Adds edge to this graph with type edge_type.
     * Fails under the following circumstances:
     * <ul>
     * <li/>edge is already an element of the graph
     * <li/>either edge or vertices is null
     * <li/>vertices has the wrong number of vertices for the graph type
     * <li/>vertices are already connected by another edge in this graph,
     * and this graph does not accept parallel edges
     * <li/>edge_type is not legal for this graph
     * </ul>
     * 
     * @param edge given edge
     * @param vertices  endpoints
     * @param edgeType edge type
     * @return true if the add is successful, and false otherwise
     * @throws IllegalArgumentException if edge or vertices is null,
     *                                  or if a different vertex set in this graph
     *                                  is already connected by edge,
     *                                  or if vertices are not a legal vertex set
     *                                  for edge
     */
    @SuppressWarnings("unchecked")
    public boolean addEdge(GraphEdge edge, Collection<? extends GraphNode> vertices, EdgeType edgeType) {
        if (edge == null || vertices == null || vertices.size() != 2) {
            return false;
        }

        GraphNode[] vs = (GraphNode[]) vertices.toArray();
        return addEdge(edge, vs[0], vs[1], edgeType);
    }

    /**
     * Returns a {@code Factory} that creates an instance of this graph type.
     * 
     * @param <GraphNode> the vertex type for the graph factory
     * @param <GraphEdge> the edge type for the graph factory
     * @return factory
     */
    public static <GraphNode, GraphEdge> Factory<UndirectedGraph<GraphNode, GraphEdge>> getFactory() {
        return new Factory<UndirectedGraph<GraphNode, GraphEdge>>() {
            @SuppressWarnings("unchecked")
            public UndirectedGraph<GraphNode, GraphEdge> create() {
                return (UndirectedGraph<GraphNode, GraphEdge>) new ThreeTenGraph();
            }
        };
    }

    /**
     * Returns the edge type of edge in this graph.
     * 
     * @param edge given edge
     * @return the EdgeType of edge, or null if edge has no defined type
     */
    public EdgeType getEdgeType(GraphEdge edge) {
        return EdgeType.UNDIRECTED;
    }

    /**
     * Returns the default edge type for this graph.
     * 
     * @return the default edge type for this graph
     */
    public EdgeType getDefaultEdgeType() {
        return EdgeType.UNDIRECTED;
    }

    /**
     * Returns the number of vertices that are incident to edge.
     * For hyperedges, this can be any nonnegative integer; for edges this
     * must be 2 (or 1 if self-loops are permitted).
     * 
     * 
     * <p>Equivalent to getIncidentVertices(edge).size().
     * 
     * @param edge the edge whose incident vertex count is to be returned
     * @return the number of vertices that are incident to edge.
     */
    public int getIncidentCount(GraphEdge edge) {
        return 2;
    }
}
