package LLL.util.struct;

import arc.math.geom.*;
import arc.struct.*;

public class RelayGraph{
  public final Seq<Vertex> vertices = new Seq<>();
  public final Seq<Edge> edges = new Seq<>();

  public void addVertex(Vertex vertex){
    vertices.add(vertex);
  }

  public Edge addEdge(Vertex from, Vertex to){
    Edge edge = new Edge(from, to);
    edges.add(edge);
    return edge;
  }

  public Edge addEdge(Vertex from, Vertex to, float weight){
    Edge edge = new Edge(from, to, weight);
    edges.add(edge);
    return edge;
  }

  public Seq<Vertex> getVertices(){
    return vertices;
  }

  public Seq<Edge> getEdges(){
    return edges;
  }

  public static class Vertex{
    public final Position position;
    public final Seq<Edge> connectedEdges = new Seq<>();

    public Vertex(Position position){
      this.position = position;
    }

    public Seq<Edge> getConnectedEdges(){
      return connectedEdges;
    }
  }

  public static class Edge{
    public final Vertex from;
    public final Vertex to;
    public final float weight;

    public Edge(Vertex from, Vertex to){
      this(from, to, from.position.dst(to.position));
    }

    public Edge(Vertex from, Vertex to, float weight){
      this.from = from;
      this.to = to;
      this.weight = weight;

      from.connectedEdges.add(this);
      to.connectedEdges.add(this);
    }

    public Vertex getFrom(){
      return from;
    }

    public Vertex getTo(){
      return to;
    }

    public float getWeight(){
      return weight;
    }

    public Vertex getOtherVertex(Vertex vertex){
      if(vertex == from){
        return to;
      }else if(vertex == to){
        return from;
      }else{
        throw new IllegalArgumentException("Vertex is not part of this edge");
      }
    }
  }
}