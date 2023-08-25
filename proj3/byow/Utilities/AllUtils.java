package byow.Utilities;

import byow.Core.Engine;
import byow.Core.WorldGenerator;
import byow.Core.WorldGenerator.Node;
import byow.InputDemo.InputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class AllUtils {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private String worldPath = "prev.txt";
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;
    private long SEED;
    private static final int REFACTOR = 10;
    private boolean inSpecialRoom = false;
    private WorldGenerator world;
    private ArrayList<Node> light;
    private ArrayList<Node> portals;
    private ArrayList<Node> closedLight;

    public TETile[][] tiles;
    private boolean lightVisibility = false;
    private int lifeLimit = 20;
    private Node avatarLoc;
    private boolean developerMode = false;
    private static final int TIME_INTERVAL = 1000;
    private boolean gameOver = false;
    private int coinValue;
    private TETile[][] visibleArea;
    private static final ArrayList<String> ENCOUNTER_TYPES
            = new ArrayList<>(List.of("portal",
            "lighted floor",
            "floor",
            "unlocked door",
            "coin",
            "dungeon: AVOID IT!",
            "monster"));


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        light = new ArrayList<>();
        portals = new ArrayList<>();
        closedLight = new ArrayList<>();
        menu();
    }

    private WorldGenerator initializeGame() {
        drawFrame("Please enter a seed:");
        long seed = getInputSeed();

        WorldGenerator w = new WorldGenerator(WIDTH, HEIGHT, seed);
        w.drawAWorld();
        world = w;
        drawFrame("World generating...");
        StdDraw.pause(TIME_INTERVAL);

        ter.renderFrame(w.getTiles());
        light = world.isLight;
        closedLight = new ArrayList<>();
        tiles = w.getTiles();
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Coin Value: " + coinValue);

        avatarLoc = w.avatarLocation;
        portals = world.isPortal;
        return w;
    }

    private void playGame() {
        while (!gameOver) {
            if (developerMode) {
                ter.renderFrame(tiles);
            } else {
                ter.renderFrame(visibleArea());
            }


            trackCursor();


            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char c = Character.toUpperCase(StdDraw.nextKeyTyped());

            if ("WSAD".contains(String.valueOf(c))) {
                avatarLoc = moveAvatar(tiles, avatarLoc, c);
            }

            if (c == ':') {
                char output = getQuitCommand();
                if (output == 'Q') {
                    gameOver = true;
                    drawFrame("Good Game!");
                    StdDraw.pause(TIME_INTERVAL);
                    save();
                    System.exit(0);
                }
            }
            if (c == '0') {
                try {
                    turnOffLight(0);
                } catch (Exception IndexOutOfBoundsException) {
                    continue;
                }
            }

            if (c == '1') {
                try {
                    turnOnLight(0);
                } catch (Exception IndexOutOfBoundsException) {
                    continue;
                }
            }

            if (c == 'F') {
                developerMode = !developerMode;
            }
            if (c == 'P') {
                save();
                coinValue = 0;
                lifeLimit = 20;
                portals = new ArrayList<>();
                light = new ArrayList<>();
                closedLight = new ArrayList<>();
                menu();
                break;
            }
            if (c == 'R') {
                readMe();
            }

            if (!developerMode) {
                ter.renderFrame(visibleArea());
            } else {
                ter.renderFrame(tiles);
            }

        }

    }

    /**
     * Given a prearranged tiles, render the frame after the player's move.
     * Return the new avatar location of the generated world tiles.
     */
    public Node moveAvatar(TETile[][] t, Node a, char c) {
        c = Character.toUpperCase(c);
        if (c == 'A') {
            if (ENCOUNTER_TYPES.contains(t[a.X - 1][a.Y].description())) {
                if (!encounter(t, a.X - 1, a.Y)) {
                    if (!inSpecialRoom) {
                        turnOffAllLight();
                    }
                    t[a.X - 1][a.Y] = Tileset.AVATAR;
                    t[a.X][a.Y] = Tileset.FLOOR;
                    a = new WorldGenerator.Node(a.X - 1, a.Y);
                    if (!inSpecialRoom) {
                        turnOnAllLight();
                    }
                }
            }
        }
        if (c == 'D') {
            if (ENCOUNTER_TYPES.contains(t[a.X + 1][a.Y].description())) {
                if (!encounter(t, a.X + 1, a.Y)) {
                    if (!inSpecialRoom) {
                        turnOffAllLight();
                    }
                    t[a.X + 1][a.Y] = Tileset.AVATAR;
                    t[a.X][a.Y] = Tileset.FLOOR;
                    a = new WorldGenerator.Node(a.X + 1, a.Y);
                    if (!inSpecialRoom) {
                        turnOnAllLight();
                    }
                }
            }
        }
        if (c == 'S') {
            if (ENCOUNTER_TYPES.contains(t[a.X][a.Y - 1].description())) {
                if (!encounter(t, a.X, a.Y - 1)) {
                    if (!inSpecialRoom) {
                        turnOffAllLight();
                    }
                    t[a.X][a.Y - 1] = Tileset.AVATAR;
                    t[a.X][a.Y] = Tileset.FLOOR;
                    a = new WorldGenerator.Node(a.X, a.Y - 1);
                    if (!inSpecialRoom) {
                        turnOnAllLight();
                    }
                }
            }
        }

        if (c == 'W') {
            if (ENCOUNTER_TYPES.contains(t[a.X][a.Y + 1].description())) {
                if (!encounter(t, a.X, a.Y + 1)) {
                    if (!inSpecialRoom) {
                        turnOffAllLight();
                    }
                    t[a.X][a.Y + 1] = Tileset.AVATAR;
                    t[a.X][a.Y] = Tileset.FLOOR;
                    a = new WorldGenerator.Node(a.X, a.Y + 1);
                    if (!inSpecialRoom) {
                        turnOnAllLight();
                    }
                }
            }
        }
        return a;
    }

    /**
     * Check if the next avatar move will have special encounters;
     * Types of encounters:
     * 1. Encounter hidden room: enter a new realm where a hidden room is generated and
     * randomized coins are scattered in the room for collection;
     * 2. Encounter water:
     * 3. Encounter special agents:
     * 4. Encounter monsters:
     */
    private boolean encounter(TETile[][] t, int x, int y) {
        if (t[x][y].description().equals("unlocked door")) {
            encounterHiddenRoom(x, y);
            //t[x][y] = Tileset.AVATAR;
            //ter.renderFrame(t);
            return true;
        }
        if (t[x][y] == Tileset.COIN) {
            coinValue++;
        }
        if (t[x][y].description().equals("portal")) {
            encounterPortal(x, y);
        }
        if (t[x][y].description().equals("dungeon: AVOID IT!")) {
            encounterDungeon(x, y);
            t[x][y] = Tileset.FLOOR;
            ter.renderFrame(t);
            return true;
        }
        if (t[x][y].description().equals("monster")) {
            lifeLimit--;
            drawFrame("Monster! Life -1");
            StdDraw.pause(1000);
        }
        if (lifeLimit <= 0) {
            drawFrame("You died! Game Over! You can reload your game in the next play.");
            StdDraw.pause(1000);
            gameOver = true;
            lifeLimit++;
            save();
            System.exit(0);
            return true;
        }
        return false;
    }

    private void encounterDungeon(int x, int y) {
        drawFrame("Escape the monster with minimum sand intake!");
        StdDraw.pause(TIME_INTERVAL);
        drawFrame("WARNING: YOUR LIFE WILL DECREASE IF YOU TAKE IN MORE SAND THAN THE MONSTER!");
        StdDraw.pause(TIME_INTERVAL);

        WorldGenerator roomWorld = new WorldGenerator(WIDTH, HEIGHT, SEED);
        int w = roomWorld.rand.nextInt(roomWorld.MAX_ROOM_WIDTH - roomWorld.MIN_ROOM_WIDTH)
                + roomWorld.MIN_ROOM_WIDTH + REFACTOR;
        int h = roomWorld.rand.nextInt(roomWorld.MAX_ROOM_HEIGHT - roomWorld.MIN_ROOM_HEIGHT)
                + roomWorld.MIN_ROOM_WIDTH + REFACTOR;

        roomWorld.drawADungeon(x, y, w, h);
        visibleArea = roomWorld.getTiles();
        StdDraw.pause(TIME_INTERVAL / 2);
        ter.renderFrame(roomWorld.getTiles());
        tiles[x][y] = Tileset.FLOOR;


        long startTime = System.currentTimeMillis();
        long waitTime = TIME_INTERVAL * 8;
        long endTime = startTime + waitTime;

        while (System.currentTimeMillis() < endTime) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            char c = StdDraw.nextKeyTyped();
            roomWorld.avatarLocation = moveAvatar(roomWorld.getTiles(), roomWorld.avatarLocation, c);
            ter.renderFrame(roomWorld.getTiles());

        }
        drawFrame("YOU SURVIVE!");
        StdDraw.pause(TIME_INTERVAL);
    }

    private Node encounterPortal(int x, int y) {
        Node v = new Node(x, y);
        portals.remove(v);

        Random rand = new Random(SEED);

        int i = rand.nextInt(portals.size());
        Node n = portals.get(i);
        drawFrame("Take a peek!");
        StdDraw.pause(TIME_INTERVAL);

        System.out.print(portals.size());
        avatarLoc = new Node(n.X, n.Y);
        ter.renderFrame(visibleArea());
        StdDraw.pause(TIME_INTERVAL);

        return new Node(n.X, n.Y);
    }

    /**
     * If encounter hidden room is executed, a new hidden room will be generated
     * on the avatar's location. The player will have 8 seconds to collect all
     * the coins in the hidden room. After 8 seconds, the player's avatar will
     * be returned to the original world.
     */
    private void encounterHiddenRoom(int x, int y) {
        WorldGenerator roomWorld = new WorldGenerator(WIDTH, HEIGHT, SEED);
        int w = roomWorld.rand.nextInt(roomWorld.MAX_ROOM_WIDTH - roomWorld.MIN_ROOM_WIDTH)
                + roomWorld.MIN_ROOM_WIDTH + REFACTOR;
        int h = roomWorld.rand.nextInt(roomWorld.MAX_ROOM_HEIGHT - roomWorld.MIN_ROOM_HEIGHT)
                + roomWorld.MIN_ROOM_WIDTH + REFACTOR;
        roomWorld.drawAHiddenRoom(x, y, w, h);
        drawFrame("Start collecting coins!");
        visibleArea = roomWorld.getTiles();
        StdDraw.pause(1000);
        ter.renderFrame(roomWorld.getTiles());
        tiles[x][y] = Tileset.FLOOR;


        long startTime = System.currentTimeMillis();
        long waitTime = TIME_INTERVAL * 5;
        long endTime = startTime + waitTime;

        while (System.currentTimeMillis() < endTime) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            char c = StdDraw.nextKeyTyped();
            roomWorld.avatarLocation = moveAvatar(roomWorld.getTiles(), roomWorld.avatarLocation, c);
            ter.renderFrame(roomWorld.getTiles());
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH / 2, HEIGHT - 1, "Coins collected:" + (coinValue));
            StdDraw.show();

        }

        drawFrame("Time's Up!");
        StdDraw.pause(TIME_INTERVAL);
    }

    /**
     * Turn on all light at once. Helper methods for rendering.
     */
    private void turnOnAllLight() {
        for (int a = 0; a < light.size(); a++) {
            int x = light.get(a).X;
            int y = light.get(a).Y;
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
                            if (tiles[i][j] == Tileset.LIGHT_ON || tiles[i][j] == Tileset.WALL ||
                                    tiles[i][j].backgroundColor() != Color.BLACK) {
                                continue;
                            }
                            Color c = new Color(R, G, B);

                            if (tiles[i][j] == Tileset.FLOOR) {
                                tiles[i][j] = new TETile('·', new Color(192, 192, 192), c,
                                        "lighted floor");
                            }

                            if (!tiles[i][j].description().equals("floor") && tiles[i][j].character() != ' ') {
                                char text = tiles[i][j].character();
                                String description = tiles[i][j].description();
                                Color color = tiles[i][j].stringColor();
                                tiles[i][j] = new TETile(text, color, c, description);
                            }
                        } catch (Exception ArrayIndexOutOfBoundException) {
                            break;
                        }
                    }
                }
                layer++;
            }
        }
    }

    /**
     * Turn on light given an index a.
     */
    private void turnOnLight(int a) {
        int x = closedLight.get(a).X;
        int y = closedLight.get(a).Y;

        int R = Tileset.LIGHT_ON.backgroundColor().getRed();
        int G = Tileset.LIGHT_ON.backgroundColor().getGreen();
        int B = Tileset.LIGHT_ON.backgroundColor().getBlue();

        int layer = 1;
        while (layer < 6) {
            G = G - 40;
            B = B - 40;
            for (int i = x - layer; i <= x + layer; i++) {
                for (int j = y - layer; j <= y + layer; j++) {
                    try {
                        if (tiles[i][j] == Tileset.LIGHT || tiles[i][j] == Tileset.WALL ||
                                tiles[i][j].backgroundColor() != Color.BLACK) {
                            continue;
                        }
                        Color c = new Color(R, G, B);

                        if (tiles[i][j].description().equals("floor")) {
                            tiles[i][j] = new TETile('·', new Color(192, 192, 192), c,
                                    "lighted floor");
                        }

                        if (tiles[i][j].character() != '·' && tiles[i][j].character() != ' ') {
                            char text = tiles[i][j].character();
                            String description = tiles[i][j].description();
                            Color color = tiles[i][j].stringColor();
                            tiles[i][j] = new TETile(text, color, c, description);
                        }
                    } catch (Exception ArrayIndexOutOfBoundException) {
                        break;
                    }
                }
            }
            layer++;
        }
        tiles[x][y] = Tileset.LIGHT_ON;
        light.add(closedLight.remove(a));
        //ter.renderFrame(tiles);
        //ter.renderFrame(visibleArea());
    }

    /**
     * Turn off light given an index a.
     */

    private void turnOffLight(int a) {
        int x = light.get(a).X;
        int y = light.get(a).Y;

        int layer = 1;

        while (layer < 6) {
            for (int i = x - layer; i <= x + layer; i++) {
                for (int j = y - layer; j <= y + layer; j++) {
                    try {
                        if (tiles[i][j].description() == "lighted floor") {
                            tiles[i][j] = Tileset.FLOOR;
                        } else if (!(tiles[i][j] == Tileset.WALL)) {
                            char text = tiles[i][j].character();
                            String description = tiles[i][j].description();
                            Color color = tiles[i][j].stringColor();
                            tiles[i][j] = new TETile(text, color, Color.BLACK, description);
                        }
                    } catch (Exception ArrayIndexOutOfBoundsException) {
                        break;
                    }
                }
            }
            layer++;
        }
        tiles[x][y] = Tileset.LIGHT;
        closedLight.add(light.remove(a));
    }

    /**
     * Turn off all light at once.
     */
    private void turnOffAllLight() {
        for (int a = 0; a < light.size(); a++) {
            int x = light.get(a).X;
            int y = light.get(a).Y;
            tiles[x][y] = Tileset.LIGHT;

            int layer = 1;

            while (layer < 6) {
                for (int i = x - layer; i <= x + layer; i++) {
                    for (int j = y - layer; j <= y + layer; j++) {
                        try {
                            if (tiles[i][j] == Tileset.LIGHT || tiles[i][j] == Tileset.WALL ||
                                    tiles[i][j].backgroundColor() == Color.BLACK) {
                                continue;
                            }
                            if (tiles[i][j].description().equals("lighted floor")) {
                                tiles[i][j] = Tileset.FLOOR;
                            }

                            if (tiles[i][j].character() != '·' && tiles[i][j].character() != ' ') {
                                char text = tiles[i][j].character();
                                String description = tiles[i][j].description();
                                Color color = tiles[i][j].stringColor();
                                tiles[i][j] = new TETile(text, color, Color.BLACK, description);
                            }
                        } catch (Exception ArrayIndexOutOfBoundsException) {
                            break;
                        }
                    }
                }
                layer++;
            }
        }
    }

    /**
     * Generate the visible area based on the avatar's location
     */
    //subjected to change based on light source location,
    private TETile[][] visibleArea() {
        WorldGenerator displayWorld = new WorldGenerator(WIDTH, HEIGHT, SEED);
        visibleArea = displayWorld.getTiles();
        int xVisibility = 5;
        int yVisibility = 5;
        int x = avatarLoc.X;
        int y = avatarLoc.Y;

        for (int i = x - xVisibility; i < x + xVisibility; i++) {
            try {
                if (tiles[i][y].description().equals("lighted floor")) {
                    xVisibility++;
                    break;
                }
            } catch (Exception IndexOutOfBoundsException) {
                break;
            }
        }
        for (int j = y - yVisibility; j < y + yVisibility; j++) {
            try {
                if (tiles[x][j].description().equals("lighted floor")) {
                    yVisibility++;
                    break;
                }
            } catch (Exception IndexOutOfBoundsException) {
                break;
            }
        }

        if (!onEdge(x, y, xVisibility, yVisibility)) {


            for (int i = x - xVisibility; i < x + xVisibility; i++) {
                for (int j = y - yVisibility; j < y + yVisibility; j++) {
                    try {
                        visibleArea[i][j] = tiles[i][j];
                    } catch (Exception ArrayIndexOutOfBoundsException) {
                        break;
                    }
                }
            }
        } else {
            fringeVisibleArea(x, y, xVisibility, yVisibility);
        }

        return visibleArea;
    }

    /**
     * Determine if the player's avatar is on edge.
     * if the avatar's visible area is on the edge of the frame, generate
     * new visible area using the fringeVisibleArea();
     */
    private boolean onEdge(int x, int y, int xv, int yv) {
        for (int i = x - xv; i <= x + xv; i++) {
            if (i < 0 || i > WIDTH) {
                return true;
            }
        }
        for (int j = y - yv; j <= y + yv; j++) {
            if (j < 0 || j > HEIGHT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate a visible area that is fringed based on the avatar's location.
     */
    private void fringeVisibleArea(int x, int y, int xv, int yv) {
        int dX = WIDTH - x;
        int dX2 = x - 0;
        int dY = HEIGHT - y;
        int dY2 = y - 0;

        if (dX <= xv && Math.min(dY, dY2) >= yv) {
            for (int i = x - 5; i < WIDTH; i++) {
                for (int j = y - 5; j < y + 5; j++) {
                    visibleArea[i][j] = tiles[i][j];
                }
            }
        }
        if (dX2 <= xv && Math.min(dY, dY2) >= yv) {
            for (int i = 0; i < x + 5; i++) {
                for (int j = y - 5; j < y + 5; j++) {
                    visibleArea[i][j] = tiles[i][j];
                }
            }
        }
        if (dY <= yv && Math.min(dX, dX2) >= xv) {
            for (int i = x - 5; i < x + 5; i++) {
                for (int j = y - 5; j < HEIGHT; j++)
                    visibleArea[i][j] = tiles[i][j];
            }
        }
        if (dY2 <= yv && Math.min(dX, dX2) >= xv) {
            for (int j = 0; j < y + 5; j++) {
                for (int i = x - 5; i < x + 5; i++) {
                    visibleArea[i][j] = tiles[i][j];
                }
            }
        }
        //Corner case
        if (dX <= xv && dY <= yv) {
            for (int i = x - 5; i < WIDTH; i++) {
                for (int j = y - 5; j < HEIGHT; j++) {
                    visibleArea[i][j] = tiles[i][j];
                }
            }
        }
        if (dX <= xv && dY2 <= yv) {
            for (int i = x - 5; i < WIDTH; i++) {
                for (int j = 0; j < y + 5; j++) {
                    visibleArea[i][j] = tiles[i][j];
                }
            }
        }
        if (dX2 <= xv && dY <= yv) {
            for (int i = 0; i < x + 5; i++) {
                for (int j = y - 5; j < HEIGHT; j++) {
                    visibleArea[i][j] = tiles[i][j];
                }
            }
        }

        if (dX2 <= xv && dY2 <= yv) {
            for (int i = 0; i < x + 5; i++) {
                for (int j = 0; j < y + 5; j++) {
                    visibleArea[i][j] = tiles[i][j];
                }
            }
        }
    }

    /**
     * Get the input seed from the player's keyboard.
     */
    private char getQuitCommand() {
        int count = 0;
        char c = 'b';
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                c = Character.toUpperCase(StdDraw.nextKeyTyped());
                count++;
            }
            if (count == 1) {
                break;
            }
        }
        return c;
    }

    private long getInputSeed() {
        String input = "";
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char c = Character.toUpperCase(StdDraw.nextKeyTyped());
            if ("1234567890".contains(String.valueOf(c))) {
                input = input + c;
                drawFrame(input);
            }
            if (c == 'S') {
                break;
            }
        }
        try {
            SEED = Long.valueOf(input);
        } catch (Exception NumberFormatException) {
            getInputSeed();
        }
        return SEED;
    }

    private void menu() {

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.PINK);
        Font font = new Font("Rockwell", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "GAME");

        Font menuFont = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(menuFont);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "SETTINGS (M)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "NEW GAME (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "LOAD GAME (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, "QUIT GAME (:Q)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 8, "READ ME!!! (R)");
        StdDraw.show();

        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char c = Character.toUpperCase(StdDraw.nextKeyTyped());

            if (c == 'N') {
                world = initializeGame();
                playGame();
            }

            if (c == 'L') {
                try {
                    read();
                    ter.renderFrame(tiles);
                    playGame();
                } catch (Exception NullPointerException) {
                    drawFrame("No game found!");
                    StdDraw.pause(1000);
                    menu();
                }
            }
            if (c == 'M') {
                settings();
            }
            if (c == 'R') {
                readMe();
                menu();
            }
            if (c == ':') {
                char output = getQuitCommand();
                if (output == 'Q') {
                    gameOver = true;
                    drawFrame("Good Game!");
                    StdDraw.pause(TIME_INTERVAL);
                    System.exit(0);
                }
            }
        }
    }

    private void settings() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Rockwell", Font.BOLD, 25);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "SETTINGS");
        Font font1 = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setFont(font1);


        if (developerMode == true) {
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Developer Mode (F) [on]");
        }
        if (developerMode == false) {
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Developer Mode (F) [off]");
        }
        if (lightVisibility == true) {
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "Light source visible (V) [on]");
        }
        if (lightVisibility == false) {
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "Light source visible (V) [off]");
        }
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Set avatar appearance (K)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, "Life Limit (G) [" + lifeLimit + "]");
        StdDraw.textLeft(3, 3, "BACK? (B)");
        StdDraw.show();


        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            String life = "Life Limit (G) [" + lifeLimit + "]";

            char c = Character.toUpperCase(StdDraw.nextKeyTyped());
            if (c == 'F') {
                developerMode = !developerMode;
            }
            if (c == 'V') {
                lightVisibility = !lightVisibility;
            }
            if (c == 'G') {
                drawFrame("Set any life limit up to 20:");
                StdDraw.textLeft(3, 3, "BACK? (B)");
                StdDraw.show();

                setLifeLimit();
            }

            if (c == 'B') {
                menu();
                break;
            }

            if (developerMode && lightVisibility == true) {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                Font title = new Font("Rockwell", Font.BOLD, 25);
                StdDraw.setFont(title);
                StdDraw.text(WIDTH / 2, HEIGHT - 10, "SETTINGS");
                Font text = new Font("Monaco", Font.BOLD, 14);
                StdDraw.setFont(text);
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Developer Mode (F) [on]");
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "Light source visible (V) [on]");
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Set avatar appearance (K)");
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, life);
            }
            if (developerMode == false && lightVisibility == true) {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                Font title = new Font("Rockwell", Font.BOLD, 25);
                StdDraw.setFont(title);
                StdDraw.text(WIDTH / 2, HEIGHT - 10, "SETTINGS");
                Font text = new Font("Monaco", Font.BOLD, 14);
                StdDraw.setFont(text);
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Developer Mode (F) [off]");
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "Light source visible (V) [on]");
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Set avatar appearance (K)");
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, life);

            }
            if (developerMode == false && lightVisibility == false) {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                Font title = new Font("Rockwell", Font.BOLD, 25);
                StdDraw.setFont(title);
                StdDraw.text(WIDTH / 2, HEIGHT - 10, "SETTINGS");
                Font text = new Font("Monaco", Font.BOLD, 14);
                StdDraw.setFont(text);
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Developer Mode (F) [off]");
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "Light source visible (V) [off]");
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Set avatar appearance (K)");
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, life);

            }
            if (developerMode == true && lightVisibility == false) {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                Font title = new Font("Rockwell", Font.BOLD, 25);
                StdDraw.setFont(title);
                StdDraw.text(WIDTH / 2, HEIGHT - 10, "SETTINGS");
                Font text = new Font("Monaco", Font.BOLD, 14);
                StdDraw.setFont(text);
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Developer Mode (F) [on]");
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "Light source visible (V) [off]");
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Set avatar appearance (K)");
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, life);
            }
            if (c == 'K') {
                setAvatar();
            }
            StdDraw.textLeft(3, 3, "BACK? (B)");
            StdDraw.show();
        }
    }

    private void readMe() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Rockwell", Font.BOLD, 25);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT - 4, "READ ME");

        Font menuFont = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(menuFont);
        StdDraw.textLeft(2, HEIGHT / 2 + 12, "Welcome to our project 3 game! Here is a reference list of" +
                " extra features that you can follow both in the beginning of ");
        StdDraw.textLeft(2, HEIGHT / 2 + 10, "the game and in the middle of a game. " +
                "During the game, click (R) anytime and (B) if you try to get back!");
        StdDraw.textLeft(2, HEIGHT / 2 + 8, "Primary Feature:");
        StdDraw.textLeft(2, HEIGHT / 2 + 6, "(0) Turn on a random light");
        StdDraw.textLeft(2, HEIGHT / 2 + 4, "(1) Turn off a random light");
        StdDraw.textLeft(2, HEIGHT / 2 + 2, "(▢) Encounter hidden room, where you can collect coins");
        StdDraw.textLeft(2, HEIGHT / 2, "(⤄) Encounter portal, where you will be opened to another " +
                "area of the world and take a peek");
        StdDraw.textLeft(2, HEIGHT / 2 - 2, "(☠) Encounter monster, life will decrease when encounter. If life" +
                " drops under 0, the game will exit.");

        StdDraw.textLeft(2, HEIGHT / 2 - 4, "Secondary Feature:");
        StdDraw.textLeft(2, HEIGHT / 2 - 6, "(M) - (K) Set avatar appearance in the menu (you can only " +
                "select your avatar at BEGINNING of a game!");
        StdDraw.textLeft(2, HEIGHT / 2 - 8, "(M) - (F) / (F) Turn on/off developer mode before/during the game");
        StdDraw.textLeft(2, HEIGHT / 2 - 10, "(M) - (G) Set life limit up to 20");

        StdDraw.textLeft(2, HEIGHT / 2 - 12, "(P) Return to main menu and save the game state without exiting " +
                "the game.");
        StdDraw.textLeft(2, HEIGHT / 2 - 14, "");

        StdDraw.textLeft(3, 3, "BACK? (B)");
        StdDraw.show();

        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char c = Character.toUpperCase(StdDraw.nextKeyTyped());
            if (c == 'B') {
                break;
            }
        }
    }


    private void setAvatar() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font font1 = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font1);
        StdDraw.text(WIDTH / 2 - 6, HEIGHT / 2, "@");
        font1 = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setFont(font1);

        StdDraw.setPenColor(Color.CYAN);
        StdDraw.text(WIDTH / 2 - 3, HEIGHT / 2, "@");
        StdDraw.setPenColor(Color.PINK);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "@");
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.text(WIDTH / 2 + 3, HEIGHT / 2, "@");
        StdDraw.setPenColor(Color.MAGENTA);
        StdDraw.text(WIDTH / 2 + 6, HEIGHT / 2, "@");

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 10, "Choose avatar appearances from below (hit " +
                "1-5 to see the options):");
        StdDraw.textLeft(3, 3, "BACK? (B)");
        StdDraw.show();

        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            char c = Character.toUpperCase(StdDraw.nextKeyTyped());
            if (c == '3') {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.PINK);
                Font font = new Font("Monaco", Font.BOLD, 30);
                StdDraw.setFont(font);
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "@");

                font = new Font("Monaco", Font.BOLD, 14);
                StdDraw.setFont(font);
                StdDraw.setPenColor(Color.CYAN);
                StdDraw.text(WIDTH / 2 - 3, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.BLUE);
                StdDraw.text(WIDTH / 2 + 3, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.MAGENTA);
                StdDraw.text(WIDTH / 2 + 6, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(WIDTH / 2 - 6, HEIGHT / 2, "@");
                Tileset.AVATAR.textColor = Color.PINK;
            }
            if (c == '2') {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.CYAN);
                Font font = new Font("Monaco", Font.BOLD, 30);
                StdDraw.setFont(font);
                StdDraw.text(WIDTH / 2 - 3, HEIGHT / 2, "@");

                font = new Font("Monaco", Font.BOLD, 14);
                StdDraw.setFont(font);
                StdDraw.setPenColor(Color.PINK);
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.BLUE);
                StdDraw.text(WIDTH / 2 + 3, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.MAGENTA);
                StdDraw.text(WIDTH / 2 + 6, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(WIDTH / 2 - 6, HEIGHT / 2, "@");
                Tileset.AVATAR.textColor = Color.CYAN;
            }
            if (c == '4') {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.BLUE);
                Font font = new Font("Monaco", Font.BOLD, 30);
                StdDraw.setFont(font);
                StdDraw.text(WIDTH / 2 + 3, HEIGHT / 2, "@");

                font = new Font("Monaco", Font.BOLD, 14);
                StdDraw.setFont(font);
                StdDraw.setPenColor(Color.CYAN);
                StdDraw.text(WIDTH / 2 - 3, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.PINK);
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.MAGENTA);
                StdDraw.text(WIDTH / 2 + 6, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(WIDTH / 2 - 6, HEIGHT / 2, "@");
                Tileset.AVATAR.textColor = Color.BLUE;
            }
            if (c == '5') {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.MAGENTA);
                Font font = new Font("Monaco", Font.BOLD, 30);
                StdDraw.setFont(font);
                StdDraw.text(WIDTH / 2 + 6, HEIGHT / 2, "@");

                font = new Font("Monaco", Font.BOLD, 14);
                StdDraw.setFont(font);
                StdDraw.setPenColor(Color.CYAN);
                StdDraw.text(WIDTH / 2 - 3, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.PINK);
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.BLUE);
                StdDraw.text(WIDTH / 2 + 3, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(WIDTH / 2 - 6, HEIGHT / 2, "@");
                Tileset.AVATAR.textColor = Color.MAGENTA;
            }
            if (c == '1') {
                StdDraw.clear(Color.BLACK);
                Font font = new Font("Monaco", Font.BOLD, 30);
                StdDraw.setFont(font);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(WIDTH / 2 - 6, HEIGHT / 2, "@");

                font = new Font("Monaco", Font.BOLD, 14);
                StdDraw.setFont(font);
                StdDraw.setPenColor(Color.CYAN);
                StdDraw.text(WIDTH / 2 - 3, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.PINK);
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.BLUE);
                StdDraw.text(WIDTH / 2 + 3, HEIGHT / 2, "@");
                StdDraw.setPenColor(Color.MAGENTA);
                StdDraw.text(WIDTH / 2 + 6, HEIGHT / 2, "@");

                Tileset.AVATAR.textColor = Color.WHITE;
            }
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 10, "Choose avatar appearances from below (hit " +
                    "1-5 to see the options):");
            StdDraw.textLeft(3, 3, "BACK? (B)");
            StdDraw.show();

            if (c == 'B') {
                settings();
                break;
            }
        }
    }

    /**
     * Set any life limit up to 20.
     */
    private void setLifeLimit() {
        String input = "";

        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char c = Character.toUpperCase(StdDraw.nextKeyTyped());
            if ("1234567890".contains(String.valueOf(c))) {
                input = input + c;
                drawFrame(input);
                StdDraw.textLeft(3, 3, "BACK? (B)");
                StdDraw.show();
            }
            if (c == 'B') {
                break;
            }
        }
        try {
            lifeLimit = Integer.valueOf(input);
        } catch (Exception NumberFormatException) {
            setLifeLimit();
        }
        if (lifeLimit > 20) {
            drawFrame("Please enter a valid life limit.");
            StdDraw.pause(1000);
            setLifeLimit();
        }
    }

    /**
     * Track the location of the player's cursor and display the according0
     * tile type on the left up corner of the game frame.
     */
    public void trackCursor() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();


        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT - 2, "Coins collected:" + coinValue);
        StdDraw.textRight(WIDTH - 18, HEIGHT - 2, "Life: " + lifeLimit);
        StdDraw.textLeft(10, HEIGHT - 2, "Return to main menu (P)");
        StdDraw.textRight(WIDTH - 2, HEIGHT - 2, "Feature List (R)");

        try {
            StdDraw.textLeft(2, HEIGHT - 2, tiles[x][y].description());
        } catch (Exception ArrayIndexOutOfBoundException) {
            StdDraw.textLeft(2, HEIGHT - 2, "nothing");
        }

        StdDraw.show();
    }

    /**
     * Save the world's 2D array into a text file before quitting the game
     */
    public void save() {
        try {
            FileWriter outFile = new FileWriter("prev.txt");

            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    if (tiles[i][j].character() != ' ') {
                        outFile.write(tiles[i][j].character());
                        outFile.flush();
                    } else {
                        outFile.write('-');
                        outFile.flush();
                    }
                }
                outFile.write('\n');
            }
            outFile.write("Color: " + Tileset.AVATAR.textColor.getRGB() + "\n");
            outFile.write("Coin Value: " + coinValue + "\n");
            outFile.write("Avatar Location: " + avatarLoc.X + ": " + avatarLoc.Y + "\n");
            outFile.write("Life: " + lifeLimit + "\n");
            outFile.flush();

            for (int i = 0; i < light.size(); i++) {
                outFile.write("Light Location: " + light.get(i).X + ": " + light.get(i).Y + "\n");
                outFile.flush();
            }

            for (int i = 0; i < closedLight.size(); i++) {
                outFile.write("Closed Light: " + closedLight.get(i).X + ": "
                        + closedLight.get(i).Y + "\n");
                outFile.flush();
            }
            for (int i = 0; i < portals.size(); i++) {
                outFile.write("Portal: " + portals.get(i).X + ": "
                        + portals.get(i).Y + "\n");
                outFile.flush();
            }
            outFile.flush();
            outFile.close();
        } catch (Exception IOException) {
            return;
        }
    }

    /**
     * Reload the last game.
     */
    public void read() {
        try {
            File file = new File(worldPath);
            tiles = new TETile[WIDTH][HEIGHT];
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    tiles[i][j] = Tileset.NOTHING;
                }
            }

            Scanner reader = new Scanner(file);
            int row = 0;

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.contains("Color")) {
                    int RGB = Integer.valueOf(line.split(": ")[1]);
                    Tileset.AVATAR.textColor = new Color(RGB);
                } else if (line.contains("Coin Value")) {
                    coinValue = Integer.valueOf(line.split(": ")[1]);
                } else if (line.contains("Avatar Location")) {
                    String[] s = line.split(": ");
                    avatarLoc = new Node(Integer.valueOf(s[1]), Integer.valueOf(s[2]));
                } else if (line.contains("Light Location")) {
                    String[] s = line.split(": ");
                    light.add(new Node(Integer.valueOf(s[1]), Integer.valueOf(s[2])));
                } else if (line.contains("Life")) {
                    lifeLimit = Integer.valueOf(line.split(": ")[1]);
                } else if (line.contains("Closed Light")) {
                    String[] s = line.split(": ");
                    closedLight.add(new Node(Integer.valueOf(s[1]), Integer.valueOf(s[2])));
                } else if (line.contains("Portal")) {
                    String[] s = line.split(": ");
                    portals.add(new Node(Integer.valueOf(s[1]), Integer.valueOf(s[2])));
                } else {
                    for (int col = 0; col < line.length(); col++) {
                        char c = line.charAt(col);

                        if (c == '@') {
                            tiles[row][col] = Tileset.AVATAR;
                        }
                        if (c == '·') {
                            tiles[row][col] = Tileset.FLOOR;
                        }
                        if (c == '█') {
                            tiles[row][col] = Tileset.WALL;
                        }
                        if (c == '▢') {
                            tiles[row][col] = Tileset.UNLOCKED_DOOR;
                        }
                        if (c == '-') {
                            tiles[row][col] = Tileset.NOTHING;
                        }
                        if (c == '✹') {
                            tiles[row][col] = Tileset.LIGHT;
                        }
                        if (c == '⤄') {
                            tiles[row][col] = Tileset.PORTAL;
                        }
                        if (c == '▴') {
                            tiles[row][col] = Tileset.DUNGEON;
                        }
                        if (c == '☠') {
                            tiles[row][col] = Tileset.MONSTER;
                        }
                    }
                }
                row++;
            }
            turnOnAllLight();
        } catch (Exception FileNotFoundException) {
            return;
        }
    }

    protected void drawFrame(String s) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    public TETile[][] interactWithInputString(String input) {

        tiles = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }

        for (int i = 0; i < input.length(); i++) {
            char c = Character.toUpperCase(input.charAt(i));

            if (c == 'N') {
                String seed = input.replaceAll("[^0-9]", "");
                seed = seed.replaceAll(" +", " ");
                WorldGenerator w = new WorldGenerator(WIDTH, HEIGHT, Long.valueOf(seed));
                w.drawAWorld();
                world = w;
                avatarLoc = world.avatarLocation;
                light = world.isLight;
                portals = world.isPortal;
                closedLight = new ArrayList<>();

                tiles = world.getTiles();
            }

            if (c == 'S' && Character.toUpperCase(input.charAt(i - 1)) == 'N') {
                continue;
            }

            if (c == 'L') {
                closedLight = new ArrayList<>();
                light = new ArrayList<>();
                portals = new ArrayList<>();
                read();
            }

            if ("WSAD".contains(String.valueOf(c))) {
                avatarLoc = moveAvatar(tiles, avatarLoc, c);
            }

            if (c == ':') {
                if (Character.toUpperCase(input.charAt(i + 1)) == 'Q') {
                    save();
                    break;
                }
            }
        }
        return tiles;
    }

    public String toString() {
        TETile t = new TETile('✹', Color.WHITE, Color.BLACK,
                "light");
        return t.toString(tiles);
    }
}
