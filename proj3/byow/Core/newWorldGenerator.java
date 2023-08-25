package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.TileEngine.TERenderer;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

import static java.lang.Math.abs;

public class newWorldGenerator {
    public TETile[][] tiles;
    public static int WIDTH;
    public static int HEIGHT;
    public static long SEED;
    private WeightedQuickUnionUF floor;
    private ArrayList<Node> isFloor;
    private static final int MIN_ROOM_WIDTH = 2;
    private static final int MIN_ROOM_HEIGHT = 2;
    private static final int MAX_ROOM_WIDTH = 3;
    private static final int MAX_ROOM_HEIGHT = 3;
    private static final int MIN_ROOM_NUM = 20;
    private static final int MAX_ROOM_NUM = 40;
    private TreeSet<Node> rooms;

    public newWorldGenerator(int width, int height, long seed) {
        WIDTH = width;
        HEIGHT = height;
        SEED = seed;
        isFloor = new ArrayList<>();
        rooms = new TreeSet<>();
        tiles = new TETile[WIDTH][HEIGHT];
        floor = new WeightedQuickUnionUF(WIDTH * HEIGHT);

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }
    public TETile[][] getTiles() {
        return tiles;
    }
    public void drawAWorld() {
        Random rand = new Random(SEED);
        int maxNumRooms = rand.nextInt(MAX_ROOM_NUM - MIN_ROOM_NUM + 1) + MIN_ROOM_NUM;

        for (int i = 0; i < maxNumRooms; i++) {
            int roomWidth = rand.nextInt(MAX_ROOM_WIDTH - MIN_ROOM_WIDTH + 1) + MIN_ROOM_WIDTH;
            int roomHeight = rand.nextInt(MAX_ROOM_HEIGHT - MIN_ROOM_HEIGHT + 1) + MIN_ROOM_HEIGHT;
            int x = rand.nextInt(WIDTH - roomWidth - 3) + 1;
            int y = rand.nextInt(HEIGHT - roomHeight - 3) + 1;
            drawARoom(x, y, roomWidth, roomHeight);
        }
        connect();
        //buildWalls();
    }
    public ArrayList<newWorldGenerator.Node> drawARoom(int x, int y, int w, int h) {
        ArrayList<newWorldGenerator.Node> roomEdges = new ArrayList<>();
        Node origin = new Node(x, y);

        for (int i = x; i <= x + w; i++) {
            for (int j = y; j <= y + h; j++) {
                try {
                    if (i == x || i == x + w || j == y || j == y + h || i == 0
                            || i == WIDTH - 1 || j == 0 || j == HEIGHT - 1) {
                        tiles[i][j] = Tileset.FLOOR;
                    } else {
                        tiles[i][j] = Tileset.FLOOR;
                        Node n = new Node(i, j);
                        floor.union(origin.index, n.index);
                    }
                    Node m = new Node(i, j);
                    isFloor.add(m);
                }
                catch(Exception IndexOutOfBoundsException){
                    break;
                }
            }
        }
        Random rand = new Random(SEED);
        int randX = rand.nextInt(w + 1) + x;
        int randY = rand.nextInt(h + 1) + y;
        rooms.add(new Node(randX, randY));

        return roomEdges;
    }
    public ArrayList<newWorldGenerator.Node> drawAHallway(int x, int y, int h, int m) {
        ArrayList<newWorldGenerator.Node> hallFloors = new ArrayList<>();
        Node origin = new Node(x, y);

        if (m == 1) { //horizontal
            for (int i = x; i < x + h; i++) {
                for (int j = y; j < y + 1; j++) {
                    try {
                        tiles[i][j] = Tileset.FLOOR;
                        Node n = new Node(i, j);
                        isFloor.add(n);
                        floor.union(origin.index, n.index);

                    } catch (Exception IndexOutOfBoundException) {
                        break;
                    }
                }
            }
        }
        if (m == 0) { //vertical
            for (int i = x; i < x + 1; i++) {
                for (int j = y; j < y + h; j++) {
                    try {
                        tiles[i][j] = Tileset.FLOOR;
                        Node n = new Node(i, j);
                        isFloor.add(n);
                        floor.union(origin.index, n.index);

                    } catch (Exception IndexOutOfBoundException) {
                        break;
                    }
                }
            }
        }
        return hallFloors;
    }
    public TreeSet<Node> getRooms() {
        return rooms;
    }
    public void connect() {
        ArrayList<Node> connectR = new ArrayList<>();
        for (Node n : rooms) {
            connectR.add(n);
        }
        Node start = connectR.remove(0);

        while (connectR.size() > 0) {
            Node end = connectR.remove(0);

            if (floor.connected(start.index, end.index) == false) {
                int startX = start.X;
                int startY = start.Y;
                int endX = end.X;
                int endY = end.Y;

                int hX = abs(endX - startX);
                int hY = abs(endY - startY);

                if (endX > startX && endY > startY) {
                    drawAHallway(startX, startY + hY, hX + 1, 1);
                    drawAHallway(startX, startY, hY, 0);
                }
                if (endX > startX && endY < startY) {
                    drawAHallway(startX, endY, hX + 1, 1);
                    drawAHallway(startX, endY, hY , 0);
                }
                if (endX < startX && endY > startY) {
                    drawAHallway(endX - 1, startY, hX + 1, 1);
                    drawAHallway(endX, startY, hY, 0);
                }
                if (endX < startX && endY < startY) {
                    drawAHallway(endX - 1, endY, hX + 1, 1);
                    drawAHallway(endX, endY + hY, hY, 0);
                }
                if (endX == startX) {
                    if (startY < endY) {
                        drawAHallway(startX, startY, hY, 0);
                    }
                    if (startY > endY) {
                        drawAHallway(startX, endY, hY, 0);
                    }
                }
                if (endY == startY) {
                    if (startY < endY) {
                        drawAHallway(startX, startY, hX, 1);
                    }
                    if (startY > endY) {
                        drawAHallway(endX, startY, hX, 1);
                    }
                }
            }
            start = end;
        }
    }
    private void buildWalls() {
        for (Node n : isFloor) {
            if (tiles[n.X + 1][n.Y] == Tileset.NOTHING) {
                tiles[n.X + 1][n.Y] = Tileset.WALL;
            }
            if (tiles[n.X + 1][n.Y + 1] == Tileset.NOTHING) {
                tiles[n.X + 1][n.Y + 1] = Tileset.WALL;
            }
            if (tiles[n.X + 1][n.Y - 1] == Tileset.NOTHING) {
                tiles[n.X + 1][n.Y - 1] = Tileset.WALL;
            }
            if (tiles[n.X - 1][n.Y + 1] == Tileset.NOTHING) {
                tiles[n.X - 1][n.Y + 1] = Tileset.WALL;
            }
            if (tiles[n.X - 1][n.Y - 1] == Tileset.NOTHING) {
                tiles[n.X - 1][n.Y - 1] = Tileset.WALL;
            }
            if (tiles[n.X - 1][n.Y] == Tileset.NOTHING) {
                tiles[n.X - 1][n.Y] = Tileset.WALL;
            }
            if (tiles[n.X][n.Y + 1] == Tileset.NOTHING) {
                tiles[n.X][n.Y + 1] = Tileset.WALL;
            }
            if (tiles[n.X][n.Y - 1] == Tileset.NOTHING) {
                tiles[n.X][n.Y - 1] = Tileset.WALL;
            }
        }
    }
    public class Node implements Comparable<Node> {
        int X;
        int Y;
        int index;


        public Node(int x, int y) {
            X = x;
            Y = y;
            index = (y % WIDTH) * WIDTH + x - 1;
        }

        @Override
        public int compareTo(Node o) {
            if (this.X < o.X && this.X < o.X) {
                return -1;
            }
            if (this.X> o.X) {
                return 1;
            }
            if (this.X == o.X) {
                if (this.Y < o.Y) {
                    return 1;
                } else {
                    return -1;
                }
            }
            return 0;
        }
    }
    public static void main(String[] args) {
        newWorldGenerator w = new newWorldGenerator(80, 40, 1212337);
        w.drawAWorld();
        System.out.print(w.getRooms().size());

        TERenderer ter = new TERenderer();
        ter.initialize(80, 40);
        ter.renderFrame(w.getTiles());

    }

}