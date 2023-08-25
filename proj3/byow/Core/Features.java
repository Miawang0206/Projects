package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.Core.WorldGenerator.Node;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Features {
    private ArrayList<Node> floors;
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
    public ArrayList<Node> lightSources;
    public ArrayList<Node> portal;
    public ArrayList<Node> dungeon;
    private ArrayList<Node> walls;
    private TETile[][] tiles;
    private Random rand;
    private Node avatar;

    public Features(WorldGenerator world, Random r) {
        tiles = world.getTiles();
        floors = world.isFloor;
        walls = world.isWall;
        lightSources = world.isLight;
        portal = world.isPortal;
        dungeon = world.isDungeon;
        rand = r;
    }

    public void placeFeatures() {
        placeAvatar();
        placeHiddenRooms();
        placeLightSource();
        placePortal();
        placeMonster();
        //placeDungeon();

        for (int i = 0; i < lightSources.size(); i++) {
            Node light = lightSources.get(i);
            turnOnLightSource(light.X, light.Y);
        }
        //turnOffLightSource(tiles, 0);
    }

    public void placeAvatar() {
        int i = rand.nextInt(floors.size());
        Node n = floors.get(i);
        tiles[n.X][n.Y] = Tileset.AVATAR;
        avatar = new Node(n.X, n.Y);
    }

    public void placeMonster() {
        int num = rand.nextInt(MAX_MONSTER_NUM - MIN_MONSTER_NUM + 1) + MIN_MONSTER_NUM;
        int count = 0;

        while (count < num) {
            int a = rand.nextInt(floors.size());
            Node n = floors.get(a);
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
                int a = rand.nextInt(floors.size());
                Node n = floors.get(a);
                if (tiles[n.X][n.Y].description().equals("floor") && tiles[n.X][n.Y].backgroundColor() ==
                        Color.BLACK) {
                    if (checkPosition(n.X, n.Y) == true) {
                        tiles[n.X][n.Y] = Tileset.LIGHT;
                        lightSources.add(new Node(n.X, n.Y));
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
            int j = rand.nextInt(floors.size());
            Node n = floors.get(j);
            if (tiles[n.X][n.Y] == Tileset.FLOOR) {
                tiles[n.X][n.Y] = Tileset.UNLOCKED_DOOR;
            }
        }
    }

    public void placePortal() {
        int numPortals = rand.nextInt(MAX_PORTAL_NUM - MIN_PORTAL_NUM + 1) + MIN_PORTAL_NUM;
        for (int i = 0; i < numPortals; i++) {
            int j = rand.nextInt(floors.size());
            Node n = floors.get(j);
            if (tiles[n.X][n.Y] == Tileset.FLOOR) {
                tiles[n.X][n.Y] = Tileset.PORTAL;
                portal.add(new Node(n.X, n.Y));
            }
        }
    }

    public void placeDungeon() {
        int numDungeons = rand.nextInt(MAX_DUNGEONS - MIN_DUNGEONS + 1) + MIN_DUNGEONS;
        for (int i = 0; i < numDungeons; i++) {
            int j = rand.nextInt(floors.size());
            Node n = floors.get(j);
            if (tiles[n.X][n.Y] == Tileset.FLOOR) {
                tiles[n.X][n.Y] = Tileset.DUNGEON;
                dungeon.add(new Node(n.X, n.Y));
            }
        }
    }

    public ArrayList<Node> getLightSources() {
        return lightSources;
    }

    public TETile[][] getTiles() {
        return tiles;
    }

    public Node getAvatar() {
        return avatar;
    }

    public static void main(String[] args) {
        WorldGenerator w = new WorldGenerator(60, 40, 11123);
        w.drawAWorld();
        //Features f = new Features(w);
        //System.out.println(f.getLightSources().size());
        System.out.println(w.getLight().size());
        TERenderer ter = new TERenderer();
        ter.initialize(60, 40);
        ter.renderFrame(w.getTiles());
    }
}
