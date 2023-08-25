package byow.Core;


import byow.TileEngine.TETile;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static com.google.common.truth.Truth.assertThat;


public class TestClass {
    @Test
    public void sameTile() {
        Engine engine = new Engine();
        TETile t = new TETile('✹', Color.WHITE, Color.BLACK,
                "light");



        assertThat(t.toString(engine.interactWithInputString("n7313251667695476404sasdw"))
                .equals(t.toString(engine.interactWithInputString("n7313251667695476404sasdw"))));

        assertThat(t.toString(engine.interactWithInputString("n7313257695476404sasdw"))
                .equals(t.toString(engine.interactWithInputString("n7313257695476404sasdw"))));

        String prev = t.toString(engine.interactWithInputString("n7313257695476404sasdw"));
        engine.interactWithInputString("n7313257695476404s:q");
        engine.interactWithInputString("las:q");
        assertThat(t.toString(engine.interactWithInputString("ldw")).equals(prev));

        String prev2 = t.toString(engine.interactWithInputString("n2swwwaa"));
        engine.interactWithInputString("la:q");
        assertThat(t.toString(engine.interactWithInputString("lawww")).equals(prev2));

        String prev3 = t.toString(engine.interactWithInputString("n98765ssdww"));
        engine.interactWithInputString("n98765s:q");
        engine.interactWithInputString("ls:q");
        engine.interactWithInputString("l:q");
        engine.interactWithInputString("l:q");
        assertThat(t.toString(engine.interactWithInputString("lsdww")).equals(prev3));



        String prev4 = t.toString(engine.interactWithInputString("n1392967723524655428sddsaawwsaddw"));
        engine.interactWithInputString("n1392967723524655428sddsaawws:q");
        //engine.interactWithInputString("ladww");
        assertThat(t.toString(engine.interactWithInputString("ladww")).equals(prev4));

    }
    @Test
    public void printTile() {
        Engine engine = new Engine();
        TETile t = new TETile('✹', Color.WHITE, Color.BLACK,
                "light");

        /*System.out.println(t.toString(engine.interactWithInputString("n7313251667695476404sasdw")));
        System.out.println(t.toString(engine.interactWithInputString("n7313251667695476404sasdw")));*/
//
        //System.out.println(t.toString(engine.interactWithInputString("n139sddsaawwswaaa:q")));
        //System.out.println(t.toString(engine.interactWithInputString("n139sddsaawwswaaa:q")));
        //System.out.println(t.toString(engine.interactWithInputString("ladww")));
        //System.out.println(t.toString(engine.interactWithInputString("n7313251667695476404sasdw")));
        //System.out.println(t.toString(engine.interactWithInputString("n7313251667695476404sasdw")));
        System.out.println(t.toString(engine.interactWithInputString("n1swwwddwaaw")));
        engine.interactWithInputString("n1swww:q");
        engine.interactWithInputString("lddw:q");
        engine.interactWithInputString("l:q");
        /*System.out.println(t.toString(engine.interactWithInputString("n1swww:q")));
        System.out.println(t.toString(engine.interactWithInputString("lddw")));
        System.out.println(t.toString(engine.interactWithInputString("l:q")));*/
        System.out.println(t.toString(engine.interactWithInputString("laaw")));
    }
    @Test
    public void test3() {
        Engine engine = new Engine();
        TETile t = new TETile('✹', Color.WHITE, Color.BLACK,
                "light");

        System.out.println(t.toString(engine.interactWithInputString("n6ssssswwwwdd")));
        engine.interactWithInputString("n6sssss:q");
        engine.interactWithInputString("lww:q");
        System.out.println(t.toString(engine.interactWithInputString("lwwdd")));

    }
    @Test
    public void test4() {
        Engine engine = new Engine();
        TETile t = new TETile('✹', Color.WHITE, Color.BLACK,
                "light");

        System.out.println(t.toString(engine.interactWithInputString("n1914164012418174419saaadd")));
        System.out.println(t.toString(engine.interactWithInputString("n1914164012418174419saaadd")));

    }
}

