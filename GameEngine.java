/***************
 * GameEngine
 * Author: Christian Duncan
 * Spring 21: CSC340
 * 
 * Modified By: Charles Rescsanski
 * 
 * This is the Engine for the TRON game.
 * It is very similar to SLING.IO, except that players have to avoid line trails
 * emitted by other players as they move.
 * And is designed to be a simple game to convert to a Networking game.
 ***************/
import java.util.ArrayList;
import java.awt.Color;
import java.io.*;
import java.io.IOException;

public class GameEngine implements Runnable {
    GameState gameState;
    Debug debug;

    boolean done;

    public GameEngine()
    {
        gameState = new GameState();
        debug = Debug.getInstance();
    }

    /**

    /**
     * Return a (deep) clone of the game state.
     * Thus any changes to the game state must be made through the game server.
     **/
    public synchronized GameState getGameState() {
        try {
            byte[] byteArray = getGameStateBytes();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArray));
            GameState result = (GameState) in.readObject();
            return result;
        } catch (IOException|ClassNotFoundException e) {
            debug.println(1, "Error: gameEngine.gGSB - converting game state to byte array.");
        }
        return null;
    }

    public synchronized byte[] getGameStateBytes() {
        try{
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        synchronized (gameState) {
            out.writeObject(gameState);
        }
        out.close();
        bout.close();
        return bout.toByteArray();
        } catch (IOException e) {
            debug.println(1, "Error: gameEngine.gGSB - converting game state to byte array.");
            return null;
        }
        
    }

    public synchronized int addPlayer(String name, Color color) {
        return gameState.addPlayer(name, color);
    }

    /**
     * Set a player p's direction to dx and dy.
     * This moves all cells in that direction
     * @param p The player (index) to move
     * @param dx The amount to move in the x direction
     * @param dy The amount to move in the y direction
     **/
    public synchronized void setPlayerDirection(int p, double dx, double dy) {
        gameState.setPlayerDirection(p, dx, dy);
    }

    /**
     * Emit line mirroring path of player
     **/
    public synchronized void releaseLine(int p, double fraction) {
        //gameState.releaseLine(p, fraction);
    }

    public void run() {
        // First add a lot of random food cells
        for (int i = 0; i < 1000; i++)
            gameState.addRandomFood();

        long currentTime = System.currentTimeMillis();
        while (!gameState.isDone()) {
            debug.println(10, "(GameEngine.run) Executing...");
            // Compute elapsed time since last iteration
            long newTime = System.currentTimeMillis();
            long delta = newTime - currentTime;
            currentTime = newTime;
            
            // Move all of the players
            synchronized (this) {
                gameState.moveAllPlayers(delta/20);  // Speed to move in
            }
            
            // Add some more food.  (Could do this periodically instead but for now ALL the time)
            synchronized (this) {
                gameState.addRandomFood();
            }

            // Detect all collisions
            detectCollisions();

            try {
                Thread.sleep(GameServer.GAME_STATE_REFRESH);
            } catch (Exception e) { }
        } 
    }

    private synchronized void detectCollisions() {
        ArrayList<GameState.Player> player = gameState.getPlayers();
        GameState.Player food = gameState.getFood();

        // First check for collisions with food
        for (GameState.Player p: player) p.collisions(food);
        food.purge();
        
        // Now check for collisions with all the players (including themselves)
        int size = player.size();
        for (int i = 0; i < size; i++) {
            GameState.Player p = player.get(i);
            for (int j = i; j < size; j++) {
                GameState.Player q = player.get(j);
                p.collisions(q);  // Compute collisions between these two players
            }

            // And purge this player's dead cells at end
            p.purge();
        }
    }
}

