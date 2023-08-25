/*
package byow.Utilities;
import byow.Core.Engine;
import byow.Core.WorldGenerator;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.Core.WorldGenerator.Node;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.lang.Math.abs;

*
 * Generate dungeon with a monster always chasing the player along the shortest path
 * between them.


public class Dungeon extends Engine {
    TERenderer ter = new TERenderer();
    private Node avatarLoc;
    private Node monsterLoc;
    private WorldGenerator dungeonWorld;
    private TETile[][] tiles;
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;

    public Dungeon(Node a) {
        super();
        avatarLoc = a;
        //monsterLoc = m;
        tiles = dungeonWorld.getTiles();

    }
    public void encounterDungeon() {
        int x = avatarLoc.X;
        int y = avatarLoc.Y;
        drawFrame("Escape the monster with minimum sand intake!");
        StdDraw.pause(1000);
        drawFrame("WARNING: YOUR LIFE WILL DECREASE IF YOU TAKE IN MORE SAND THAN THE MONSTER!");
        StdDraw.pause(1000);
        int w = dungeonWorld.rand.nextInt(dungeonWorld.MAX_ROOM_WIDTH - dungeonWorld.MIN_ROOM_WIDTH)
                + dungeonWorld.MIN_ROOM_WIDTH + 10;
        int h = dungeonWorld.rand.nextInt(dungeonWorld.MAX_ROOM_HEIGHT - dungeonWorld.MIN_ROOM_HEIGHT)
                + dungeonWorld.MIN_ROOM_WIDTH + 10;

        dungeonWorld.drawADungeon(x, y, w, h);
        StdDraw.pause(500);
        ter.renderFrame(dungeonWorld.getTiles());
        tiles[x][y] = Tileset.FLOOR;

        long start_time = System.currentTimeMillis();
        long wait_time = 8000;
        long end_time = start_time + wait_time;

        while (System.currentTimeMillis() < end_time) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            char c = StdDraw.nextKeyTyped();
            dungeonWorld.avatarLocation = moveAvatar(dungeonWorld.getTiles(), dungeonWorld.avatarLocation, c);
            ter.renderFrame(dungeonWorld.getTiles());

        }
        drawFrame("YOU SURVIVE!");
        StdDraw.pause(1000);
    }

    public TETile[][] chase() {

        ArrayList<Node> path = new ArrayList<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(getComparator());
        ArrayList<Node> initialPath = new ArrayList<>();
        initialPath.add(monsterLoc);
        pq.add(monsterLoc);

        return null;
    }

    private ArrayList<Node> adj(Node n) {
        return null;
    }
    public Comparator<Node> getComparator() {
        return new getComparator();
    }
    private class getComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            int dist1 = abs(o1.X - avatarLoc.X) + abs(o1.Y - avatarLoc.Y);
            int dist2 = abs(o2.X - avatarLoc.X) + abs(o2.Y - avatarLoc.Y);
            if (dist1 < dist2) {
                return -11;
            }
            if (dist1 > dist2) {
                return 1;
            }
            return 0;
        }
    }
    public TETile[][] getTiles() {return tiles;}
}
*/
