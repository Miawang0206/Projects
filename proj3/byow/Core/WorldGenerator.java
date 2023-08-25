package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.TileEngine.TERenderer;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

import static java.lang.Math.abs;

public class WorldGenerator {
    public TETile[][] tiles;
    public static int WIDTH = 40;
    public static int HEIGHT = 60;
    public static long SEED;
    private WeightedQuickUnionUF floor;
    public ArrayList<Node> isFloor;
    public ArrayList<Node> isWall;
    public ArrayList<Node> isDungeon;
    public Random rand;
    public ArrayList<Node> isLight;
    public ArrayList<Node> isPortal;
    public static final int MIN_ROOM_WIDTH = 2;
    public static final int MIN_ROOM_HEIGHT = 2;
    public static final int MAX_ROOM_WIDTH = 3;
    public static final int MAX_ROOM_HEIGHT = 3;
    public static final int MIN_ROOM_NUM = 20;
    public static final int MAX_ROOM_NUM = 40;
    public  Node avatarLocation;
    private TreeSet<Node> rooms;
    private static final int MAX_HIDDEN_GOLD_ROOMS = 4;
    private static final int MIN_HIDDEN_GOLD_ROOMS = 2;

    private static final int MAX_DUNGEONS = 5;
    private static final int MIN_DUNGEONS = 2;
    private static final int MAX_LIGHT = 5;
    private static final int MIN_LIGHT = 2;
    private static final int MAX_PORTAL_NUM = 10;
    private static final int MIN_PORTAL_NUM = 2;
    private static final int MAX_MONSTER_NUM = 15;
    private static final int MIN_MONSTER_NUM = 10;

    public WorldGenerator(int width, int height, long seed) {
        WIDTH = width;
        HEIGHT = height;
        SEED = seed;
        isFloor = new ArrayList<>();
        isWall = new ArrayList<>();
        isLight = new ArrayList<>();
        isPortal = new ArrayList<>();
        isDungeon = new ArrayList<>();
        rooms = new TreeSet<>();
        tiles = new TETile[WIDTH][HEIGHT];
        floor = new WeightedQuickUnionUF(WIDTH * HEIGHT);
        rand = new Random(seed);

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
        int maxNumRooms = rand.nextInt(MAX_ROOM_NUM - MIN_ROOM_NUM + 1) + MIN_ROOM_NUM;

        for (int i = 0; i < maxNumRooms; i++) {
            int roomWidth = rand.nextInt(MAX_ROOM_WIDTH - MIN_ROOM_WIDTH + 1) + MIN_ROOM_WIDTH;
            int roomHeight = rand.nextInt(MAX_ROOM_HEIGHT - MIN_ROOM_HEIGHT + 1) + MIN_ROOM_HEIGHT;
            int x = rand.nextInt(WIDTH - roomWidth - 3) + 1;
            int y = rand.nextInt(HEIGHT - roomHeight - 5) + 1;
            drawARoom(x, y, roomWidth, roomHeight);
        }
        connect();
        buildWalls();
        placeFeatures();
        /*Features f = new Features(this, rand);
        f.placeFeatures();
        isLight = f.lightSources;
        isPortal = f.portal;
        isDungeon = f.dungeon;
        avatarLocation = f.getAvatar();*/
    }
    public void drawAHiddenRoom(int x, int y, int w, int h) {
        avatarLocation = new Node(x, y);
        for (int i = x - 1; i <= x + w + 1; i++) {
            for (int j = y - 1; j <= y + h + 1; j++) {
                try {
                    if (i == x - 1 || i == x + w + 1 || j == y - 1 || j == y + h + 1|| i == 0
                            || i == WIDTH - 1 || j == 0 || j == HEIGHT - 1) {
                        tiles[i][j] = Tileset.HIDDEN_ROOM_WALL;
                    } else {
                        tiles[i][j] = Tileset.HIDDEN_ROOM_FLOOR;
                    }
                    tiles[x][y] = Tileset.AVATAR;
                } catch (Exception IndexOutOfBoundsException) {
                    break;
                }
            }
        }
        for (int i = 0; i < rand.nextInt(w * h); i++) {
            int goldX = rand.nextInt(w) + x ;
            int goldY = rand.nextInt(h) + y;
            try {
                if (tiles[goldX][goldY] == Tileset.HIDDEN_ROOM_FLOOR) {
                    tiles[goldX][goldY] = Tileset.COIN;
                }
            } catch (Exception ArrayIndexOutOfBoundsException) {
                break;
            }
        }
    }
    public void drawADungeon(int x, int y, int w, int h) {
        avatarLocation = new Node(x, y);
        for (int i = x - 1; i <= x + w + 1; i++) {
            for (int j = y - 1; j <= y + h + 1; j++) {
                try {
                    if (i == x - 1 || i == x + w + 1 || j == y - 1 || j == y + h + 1|| i == 0
                            || i == WIDTH - 1 || j == 0 || j == HEIGHT - 1) {
                        tiles[i][j] = Tileset.DUNGEON_WALL;
                    } else {
                        tiles[i][j] = Tileset.DUNGEON_FLOOR;
                    }
                    tiles[x][y] = Tileset.AVATAR;
                } catch (Exception IndexOutOfBoundsException) {
                    break;
                }
            }
        }

        int mX = rand.nextInt(w) + x;
        int mY = rand.nextInt(h) + y;
        placeMonster(mX, mY);
    }
    public void placeMonster(int x, int y) {
        tiles[x][y] = Tileset.MONSTER;
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
        int randX = rand.nextInt(w + 1) + x;
        int randY = rand.nextInt(h + 1) + y;
        rooms.add(new Node(randX, randY));

        return roomEdges;
    }
    public ArrayList<Node> drawAHallway(int x, int y, int h, int m) {
        ArrayList<Node> hallFloors = new ArrayList<>();
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
    public ArrayList<Node> getLight() {return isLight;}
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
                    if (startX < endX) {
                        drawAHallway(startX, startY, hX, 1);
                    }
                    if (startX > endX) {
                        drawAHallway(endX, startY, hX, 1);
                    }
                }
            }
            start = end;
        }
    }
    public void buildWalls() {
        for (Node n : isFloor) {
            if (n.X == 0 || n.X == WIDTH || n.Y == 0 || n.Y == HEIGHT) {
                tiles[n.X][n.Y] = Tileset.WALL;
                isWall.add(new Node(n.X, n.Y));
            }
            if (tiles[n.X + 1][n.Y] == Tileset.NOTHING) {
                tiles[n.X + 1][n.Y] = Tileset.WALL;
                isWall.add(new Node(n.X + 1, n.Y));
            }
            if (tiles[n.X + 1][n.Y + 1] == Tileset.NOTHING) {
                tiles[n.X + 1][n.Y + 1] = Tileset.WALL;
                isWall.add(new Node(n.X + 1, n.Y + 1));
            }
            if (tiles[n.X + 1][n.Y - 1] == Tileset.NOTHING) {
                tiles[n.X + 1][n.Y - 1] = Tileset.WALL;
                isWall.add(new Node(n.X + 1, n.Y - 1));
            }
            if (tiles[n.X - 1][n.Y + 1] == Tileset.NOTHING) {
                tiles[n.X - 1][n.Y + 1] = Tileset.WALL;
                isWall.add(new Node(n.X - 1, n.Y + 1));
            }
            if (tiles[n.X - 1][n.Y - 1] == Tileset.NOTHING) {
                tiles[n.X - 1][n.Y - 1] = Tileset.WALL;
                isWall.add(new Node(n.X - 1, n.Y - 1));
            }
            if (tiles[n.X - 1][n.Y] == Tileset.NOTHING) {
                tiles[n.X - 1][n.Y] = Tileset.WALL;
                isWall.add(new Node(n.X - 1, n.Y));
            }
            if (tiles[n.X][n.Y + 1] == Tileset.NOTHING) {
                tiles[n.X][n.Y + 1] = Tileset.WALL;
                isWall.add(new Node(n.X, n.Y + 1));
            }
            if (tiles[n.X][n.Y - 1] == Tileset.NOTHING) {
                tiles[n.X][n.Y - 1] = Tileset.WALL;
                isWall.add(new Node(n.X, n.Y - 1));
            }
        }
    }
    public void placeFeatures() {
        placeAvatar();
        placeHiddenRooms();
        placeLightSource();
        placePortal();
        placeMonster();
        //placeDungeon();

        for (int i = 0; i < isLight.size(); i++) {
            Node light = isLight.get(i);
            turnOnLightSource(light.X, light.Y);
        }
        //turnOffLightSource(tiles, 0);
    }

    public void placeAvatar() {
        int i = rand.nextInt(isFloor.size());
        Node n = isFloor.get(i);
        tiles[n.X][n.Y] = Tileset.AVATAR;
        avatarLocation = new Node(n.X, n.Y);
    }

    public void placeMonster() {
        int num = rand.nextInt(MAX_MONSTER_NUM - MIN_MONSTER_NUM + 1) + MIN_MONSTER_NUM;
        int count = 0;

        while (count < num) {
            int a = rand.nextInt(isFloor.size());
            Node n = isFloor.get(a);
            if (tiles[n.X][n.Y].description().equals("floor")) {
                if (checkMonster(n.X, n.Y) == true) {
                    tiles[n.X][n.Y] = Tileset.MONSTER;
                    count++;
                }
            }
        }
    }

    public void placeLightSource() {
        int numLightSource = rand.nextInt(MAX_LIGHT - MIN_LIGHT + 1)
                + MIN_LIGHT;
        int validCount = 0;
        long start = System.currentTimeMillis();
        long end = start + 500;

        while (validCount < numLightSource) {
            if (System.currentTimeMillis() > end) {
                break;}
            int a = rand.nextInt(isFloor.size());
            Node n = isFloor.get(a);
            if (tiles[n.X][n.Y].description().equals("floor") && tiles[n.X][n.Y].backgroundColor() ==
                    Color.BLACK) {
                if (checkPosition(n.X, n.Y) == true) {
                    tiles[n.X][n.Y] = Tileset.LIGHT;
                    isLight.add(new Node(n.X, n.Y));
                    validCount++;
                }
            }
        }
    }

    private boolean checkMonster(int x, int y) {
        if (tiles[x + 1][y + 1] == Tileset.WALL ||
                tiles[x + 1][y] == Tileset.WALL ||
                tiles[x + 1][y - 1] == Tileset.WALL ||
                tiles[x - 1][y + 1] == Tileset.WALL ||
                tiles[x - 1][y] == Tileset.WALL ||
                tiles[x + 1][y + 1] == Tileset.WALL ||
                tiles[x][y - 1] == Tileset.WALL ||
                tiles[x][y + 1] == Tileset.WALL) {
            return false;
        }
        return true;
    }

    private boolean checkPosition(int x, int y) {
        if (tiles[x + 1][y + 1] == Tileset.WALL ||
                tiles[x + 1][y] == Tileset.WALL ||
                tiles[x + 1][y - 1] == Tileset.WALL ||
                tiles[x - 1][y + 1] == Tileset.WALL ||
                tiles[x - 1][y] == Tileset.WALL ||
                tiles[x + 1][y + 1] == Tileset.WALL ||
                tiles[x][y - 1] == Tileset.WALL ||
                tiles[x][y + 1] == Tileset.WALL) {
            return false;
        }

        for (int i = x - 10; i < x + 10; i++) {
            for (int j = y - 10; j < y + 10; j++) {
                try {
                    if (tiles[i][j] == Tileset.LIGHT) {
                        return false;
                    }
                } catch (Exception ArrayIndexOutOfBoundsException) {
                    return false;
                }
            }
        }
        return true;
    }

    public void turnOnLightSource(int x, int y) {
        tiles[x][y] = Tileset.LIGHT_ON;
        int R = tiles[x][y].backgroundColor().getRed();
        int G = tiles[x][y].backgroundColor().getGreen();
        int B = tiles[x][y].backgroundColor().getBlue();

        int layer = 1;
        while (layer < 6) {
            G = G - 40;
            B = B - 40;
            for (int i = x - layer; i <= x + layer; i++) {
                for (int j = y - layer; j <= y + layer; j++) {
                    try {
                        if (tiles[i][j] == Tileset.LIGHT_ON ||
                                tiles[i][j].backgroundColor() != Color.BLACK) {
                            continue;
                        }
                        Color c = new Color(R, G, B);

                        if (tiles[i][j] == Tileset.FLOOR) {
                            tiles[i][j] = new TETile('·', new Color(192, 192, 192), c,
                                    "lighted floor");
                        }

                        if (tiles[i][j].character() != '·' && tiles[i][j].character() != ' ') {
                            char text = tiles[i][j].character();
                            String description = tiles[i][j].description();
                            Color color = tiles[i][j].stringColor();
                            tiles[i][j] = new TETile(text, color, c, description);
                        }
                        if (tiles[i][j] == Tileset.WALL) {
                            break;
                        }
                    } catch (Exception ArrayIndexOutOfBoundException) {
                        break;
                    }
                }
            }
            layer++;
        }
    }

    public void placeHiddenRooms() {
        int numHiddenRooms = rand.nextInt(MAX_HIDDEN_GOLD_ROOMS - MIN_HIDDEN_GOLD_ROOMS + 1)
                + MIN_HIDDEN_GOLD_ROOMS;
        for (int i = 0; i < numHiddenRooms; i++) {
            int j = rand.nextInt(isFloor.size());
            Node n = isFloor.get(j);
            if (tiles[n.X][n.Y] == Tileset.FLOOR) {
                tiles[n.X][n.Y] = Tileset.UNLOCKED_DOOR;
            }
        }
    }

    public void placePortal() {
        int numPortals = rand.nextInt(MAX_PORTAL_NUM - MIN_PORTAL_NUM + 1) + MIN_PORTAL_NUM;
        for (int i = 0; i < numPortals; i++) {
            int j = rand.nextInt(isFloor.size());
            Node n = isFloor.get(j);
            if (tiles[n.X][n.Y] == Tileset.FLOOR) {
                tiles[n.X][n.Y] = Tileset.PORTAL;
                isPortal.add(new Node(n.X, n.Y));
            }
        }
    }

    public void placeDungeon() {
        int numDungeons = rand.nextInt(MAX_DUNGEONS - MIN_DUNGEONS + 1) + MIN_DUNGEONS;
        for (int i = 0; i < numDungeons; i++) {
            int j = rand.nextInt(isFloor.size());
            Node n = isFloor.get(j);
            if (tiles[n.X][n.Y] == Tileset.FLOOR) {
                tiles[n.X][n.Y] = Tileset.DUNGEON;
                isDungeon.add(new Node(n.X, n.Y));
            }
        }
    }
    public static class Node implements Comparable<Node> {
        public int X;
        public int Y;
        public int index;


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
        long a = 7313251667695476404L;
        long d = 1403711858794727810L;
        long b = 7313251667695474L;
        long e = 5584210714828612328L;
        long c = 2;
        WorldGenerator w = new WorldGenerator(60, 40, e);
        w.drawAWorld();

        System.out.print(w.getRooms().size());
        TERenderer ter = new TERenderer();
        ter.initialize(60, 40);
        ter.renderFrame(w.getTiles());

    }

}