/***************
 * GameState
 * Team Members: Lauren Atkinson, Timothy Carta, Ryan Hayes, Griffin King, Charles Rescsanski
 * Spring 21 | CSC340
 ***************/
import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
import java.awt.Point;
import java.io.PrintStream;
import java.io.Serializable;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

public class GameState implements Cloneable, Serializable {
    public static final long serialVersionUID = 340L;
    public static final int GRID_HEIGHT = 125; // height
    public static final int GRID_WIDTH = 175; // width

    // Inner class: Just a player, with name, color, location, direction, and
    class Player implements Cloneable, Serializable {

        public static final int DIRECTION_NORTH = 0; // north
        public static final int DIRECTION_EAST = 1; // EAST
        public static final int DIRECTION_SOUTH = 2; // SOUTH
        public static final int DIRECTION_WEST = 3; // WEST

        boolean dead = false;
        String name; // Name to display
        Color appearance; // The appearance of this player and its corresponding lines
        int playerID;
        int gridID; // the value of grid coordinates pertaining to this player
        int direction; // direction that player is moving
        int locx; // current x coordinate of player
        int locy; // current y coordinate of player

        public Player(int playerID, String n, int _gridID, int initX, int initY, Color _appearance, int _initialDirection) {
        	this.name = n;
        	this.playerID = playerID;
            this.locx = initX;
            this.locy = initY;
            this.gridID = _gridID;
            this.appearance = _appearance;
            this.direction = _initialDirection;
        }

        public Object clone() throws CloneNotSupportedException {
            Player p = (Player) super.clone();

            return p;
        }

        public String getName() {
            return this.name;
        }

        public int getGridID() {
            return this.gridID;
        }

        public Color getAppearance() {
            return this.appearance;
        }

        /**
         * Moves player by corresponding distance
         **/
        public void setLocation(int x, int y) {
            this.locx = x;
            this.locy = y;
        }

        /**
         * Moves players by the given direction
         */
        public void move() {
            switch (direction) {

                case DIRECTION_NORTH:
                    if (boundCheck(this.locx, this.locy - 1)) {
                        this.locy--;
                    }
                    break;

                case DIRECTION_EAST:
                    if (boundCheck(this.locx + 1, this.locy)) {
                        this.locx++;
                    }
                    break;

                case DIRECTION_SOUTH:
                    if (boundCheck(this.locx, this.locy + 1)) {
                        this.locy++;
                    }
                    break;

                case DIRECTION_WEST:
                    if (boundCheck(this.locx - 1, this.locy)) {
                        this.locx--;
                    }
                    break;
            }

            if (!lineCollisionCheck(this.locx, this.locy)) {
                this.dead = true;
            }
        }

        public void turnWest() {
            if (direction != DIRECTION_EAST) {
                direction = DIRECTION_WEST;
            }
        }

        public void turnEast() {
            if (direction != DIRECTION_WEST) {
                direction = DIRECTION_EAST;
            }
        }

        public void turnSouth() {
            if (direction != DIRECTION_NORTH) {
                direction = DIRECTION_SOUTH;
            }
        }

        public void turnNorth() {
            if (direction != DIRECTION_SOUTH) {
                direction = DIRECTION_NORTH;
            }
        }

        /**
         * Determine any collisions between two groups of Players
         **/
        public void collisions() {

        }

        /**
         * Generate a string representation of the given player For DEBUGGING purposes
         * mainly
         **/
        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            res.append(this.name);
            return res.toString();
        }
    }

    // The list of Players
    private ArrayList<Player> players;

    private int winnerID = -1;
    private String winnerName = null;
    private int numPlayers = 0;
    private boolean activeGame = false;

    private int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT]; // keeps track of where each player has been

    public GameState() {
        players = new ArrayList<Player>();
        
        resetGrid();
    }

    public Object clone() throws CloneNotSupportedException {
        GameState gs = (GameState) super.clone();
        gs.players = new ArrayList<>(players.size());
        for (int i = 0; i < players.size(); i++) {
            gs.players.add((Player) players.get(i).clone());
        }
        return gs;
    }

    /**
     * Returns if the game is done or not!
     **/
    public boolean isDone() {
        return false; // For now, runs forever!
    }

    /**
     * Add a player to the Game State. All future references to this player should
     * use the index returned.
     *
     * @param name  The name of the player
     * @param color The color of the player
     * @returns The index of this player (in the ArrayList)
     **/
    public int addPlayer(String name, Color color, int initialDirection, boolean activeGame) {
        // Pick an initial location that has not yet been visited
    	this.numPlayers ++;
        Point p = null;
        if (activeGame) {
            p = new Point(GRID_WIDTH + 10, GRID_HEIGHT + 10);
        } else {
            p = starterPos();
        }
        int gridID = players.size() + 1;
        int playerID = players.size();
        players.add(new Player(playerID, name, gridID, (int) p.getX(), (int) p.getY(), color, initialDirection));
        return players.size() - 1;
    }
    
    public int getNumPlayers()
    {
    	return this.numPlayers;
    }

    public void removePlayer(int iD)
    {
    	//players.removeIf(p -> (p.playerID == iD));
    	players.set(iD, null);
    	this.numPlayers --;
    }

    /** Determines a location on the grid that has yet to be occupied */
    public Point starterPos() {
        boolean validStart = false;
        int x = 0;
        int y = 0;
        while (!validStart) {
            x = (int) (Math.random() * GRID_WIDTH);
            y = (int) (Math.random() * GRID_HEIGHT);
            if (x < GRID_WIDTH - 10 && x > 10 && y < GRID_HEIGHT - 10 && y > 10) {
                validStart = true;
            }
        }
        return new Point(x, y);
    }

    /*
     * Go through each player and reset their death, position
     */
    public void resetPlayers() {
        Point startPos = null;
        for (GameState.Player p : this.getPlayers()) {
        	if (p != null)
        	{
    		  p.dead = false;
              startPos = starterPos();
              p.setLocation((int) startPos.getX(), (int) startPos.getY());
        	}
          
        }
    }

    public void setGameActivity(boolean status) {
    	this.activeGame = status;
    }

    public boolean getGameActivity()
    {
    	return this.activeGame;
    }

    /**
     * Set a player p's direction to dx and dy. This moves all cells in that
     * direction
     *
     * @param p  The player (index) to move
     * @param dx The amount to move in the x direction
     * @param dy The amount to move in the y direction
     **/
    public void setPlayerLocation(int p, int x, int y) {
        Player pl = players.get(p); // Get the Player object
        pl.setLocation(x, y);
        // marks new location on grid
        this.grid[x][y] = pl.getGridID();
    }

    public void turnWest(int p) {
        Player pl = players.get(p); // Get the Player object
        pl.turnWest();
    }

    public void turnEast(int p) {
        Player pl = players.get(p); // Get the Player object
        pl.turnEast();
    }

    public void turnSouth(int p) {
        Player pl = players.get(p); // Get the Player object
        pl.turnSouth();
    }

    public void turnNorth(int p) {
        Player pl = players.get(p); // Get the Player object
        pl.turnNorth();
    }

    public void updateGrid() {
        for (GameState.Player p : this.getPlayers()) {
        	if (p != null)
        	{
        		  if (!p.dead) {
                      this.grid[p.locx][p.locy] = p.gridID;
                  }
        	}  
        }
    }

    public void resetGrid() {
        // set everything to 0
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                this.grid[x][y] = 0;
            }
        }
    }

    // verify if location is valid
    public boolean boundCheck(int x, int y) {
        return x > 0 && x < this.grid.length && y > 0 && y < this.grid[0].length;
    }

    public boolean lineCollisionCheck(int x, int y) {
        if (x > GRID_WIDTH && y > GRID_HEIGHT) {
            return false;
        }
        return this.grid[x][y] == 0;
    }

    // Returns the list of players. Probably safer to have some way to iterate
    // through them and the cells
    // So we can control access. But this will be a network so this actually will be
    // a local copy of the GameState anyway!
    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setWinner(int id, String name)
    {
    	this.winnerID = id;
    	this.winnerName = name;
    }

    public int getWinnerID()
    {
    	return this.winnerID;
    }

    public String getWinnerName()
    {
    	return this.winnerName;
    }

    // Returns the grid containing the history of where each player has been
    public int[][] getGrid() {
        return grid;
    }

    /**
     * Display the "Game State"
     **/
    public void display(PrintStream out) {
        out.println("============ State =================");
        for (Player p : players) {
            out.println("  " + p);
        }
        out.println("====================================");
    }
}
