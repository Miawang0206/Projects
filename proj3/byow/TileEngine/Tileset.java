package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.WHITE, Color.black, "you");
    public static final TETile WALL = new TETile('█', new Color(250, 175, 199),
            new Color(255, 122, 165),
            "wall");

    public static final TETile FLOOR = new TETile('·', new Color(247, 218, 227), Color.black,
            "floor");
    public static final TETile NOTHING = new TETile(' ', Color.BLACK, Color.BLACK,"nothing");
    public static final TETile HIDDEN_ROOM_WALL = new TETile('█', new Color(255, 147, 16)
            , new Color(255, 119, 46), "wall");
    public static final TETile HIDDEN_ROOM_FLOOR = new TETile('·', new Color(255, 236, 199),
            Color.black,
            "floor");
    public static final TETile DUNGEON = new TETile('▴', Color.gray, Color.black,
            "dungeon: AVOID IT!");
    public static final TETile DUNGEON_FLOOR = new TETile('·', new Color(84, 78, 68),
            new Color (133, 127, 118),
            "floor");
    public static final TETile DUNGEON_WALL = new TETile('█', new Color(48, 45, 39),
            new Color (33, 31, 27),
            "wall");
    public static final TETile MONSTER = new TETile('☠', Color.ORANGE.darker(),
            Color.BLACK, "monster");
    public static final TETile LIGHT = new TETile('✹', Color.WHITE, Color.BLACK,
            "light");
    public static final TETile LIGHT_ON = new TETile('✹', Color.WHITE, new Color(14, 240, 225),
            "light");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile COIN = new TETile('✪', new Color(255, 188, 33), Color.black,
            "coin");
    public static final TETile PORTAL = new TETile('⤄', Color.orange, Color.black,
            "portal");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
}


