# Build Your Own World

**Mia Wang**
## CS 61B: Data Structures
This is my implementation of a tile-based world exploration game based on a 2D renderer that has a variety of features. The basic random world tile generation was separately implemented in _WorldGenerator_ and all other auxiliary features were included in _AllUtils_. To run the game, initiate _Main_ class with no input string and visualize the game.

**Feature list:**
1. During the game, click (R) anytime and (B) if you try to get back!
2. (0) Turn on a random light
3. (1) Turn off a random light
4. (▢) Encounter hidden room, where you can collect coins
5. (⤄) Encounter portal, where you will be opened to another area of the world and take a peek
6. (☠) Encounter monster, life will decrease when encounter. If life drops under 0, the game will exit.
7. (M) - (K) Set avatar appearance in the menu (you can only select your avatar at BEGINNING of a game!).
8. (M) - (F) / (F) Turn on/off developer mode before/during the game.
9. (M) - (G) Set life limit up to 20.
10. (P) Return to main menu and save the game state without exiting the game.
