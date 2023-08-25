package byow.Core;

import byow.TileEngine.Tileset;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

import static java.lang.Math.abs;

public class dGraph extends WorldGenerator{
    private PriorityQueue<Node> pq;
    private HashSet<Node> visited;
    private Node START;
    private Node TARGET;

    public dGraph() {
        super(WIDTH, HEIGHT, SEED);
        pq = new PriorityQueue<>();
        visited = new HashSet<>();
    }
}
