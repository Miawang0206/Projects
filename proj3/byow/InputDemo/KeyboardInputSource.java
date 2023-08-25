package byow.InputDemo;

/**
 * Created by hug.
 */
import edu.princeton.cs.algs4.StdDraw;

public class KeyboardInputSource implements InputSource {
    private static final boolean PRINT_TYPED_KEYS = false;
    public KeyboardInputSource() {
        StdDraw.text(0.5, 0.5, "NEW GAME (N)");
        StdDraw.text(0.5, 0.4, "LOAD GAME (L)");
        StdDraw.text(0.5, 0.3, "QUITE (Q)");
    }

    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (PRINT_TYPED_KEYS) {
                    System.out.print(c);
                }
                return c;
            }
        }
    }

    public boolean possibleNextInput() {
        return true;
    }
}
