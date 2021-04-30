/***************
 * GameEngine
 * Author: Christian Duncan
 * Spring 21: CSC340
 * 
 * Modified By: Charles Rescsanski
 * 
 * This is the Engine for the TRON game.  It is NOT a standalone application.  It relays commands
 * to the GameState.
 * It is very similar to SLING.IO, except that players have to avoid line trails
 * emitted by other players as they move.
 * And is designed to be a simple game to convert to a Networking game.
 ***************/
import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
import java.io.*;
import java.io.IOException;

public class GameEngine implements Runnable {
    GameState gameState;
    Debug debug;
    boolean activeGame;
    boolean done;

    public GameEngine()
    {
    	this.activeGame = true;
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
    
    public void turnWest(int p){
        gameState.turnWest(p);
    }

    public void turnEast(int p){
       gameState.turnEast(p);
    }

    public void turnSouth(int p){
    	gameState.turnSouth(p);
    }

    public void turnNorth(int p){
        gameState.turnNorth(p);
     }

    public synchronized void changePlayerDirection(int p, int newDirection)
    {
    	switch (newDirection)
    	{
    		case 0: 
    			this.turnNorth(p);
    			break;
    		case 1:
    			this.turnEast(p);
    			break;
    		case 2:
    			this.turnSouth(p);
    			break;
    		case 3:
    			this.turnWest(p);
    			break;
    			
    	}
    }
    

    public synchronized int addPlayer(String name, Color color) {
    	Random rand = new Random();
    	int initialDirection = rand.nextInt(4);
        return gameState.addPlayer(name, color, initialDirection);
    }

    /**
     * Set a player p's direction to dx and dy.
     * This moves all cells in that direction
     * @param p The player (index) to move
     * @param dx The amount to move in the x direction
     * @param dy The amount to move in the y direction
     **/
    public synchronized void setPlayerLocation(int p, int dx, int dy) {
        gameState.setPlayerLocation(p, dx, dy);
    }

    public void run() {
        while (!gameState.isDone()) {
            debug.println(10, "(GameEngine.run) Executing...");
            
            if (this.activeGame)
            {
            	//Move Players
                movePlayers();
                
                //Check if game is over
                if(checkIfGameOver())
            	{
                	this.activeGame = false;
                	//gameState.endCurrentGame();
            	}
            }       

            try {
                Thread.sleep(GameServer.GAME_STATE_REFRESH);
            } catch (Exception e) { }
        } 
    }

    private synchronized void detectCollisions() {
        ArrayList<GameState.Player> player = gameState.getPlayers();
        
        // Each player cannot cross any line (including its own).
        // For each player, we need to check whether their current location intersects
        // with a previously visited location or the wall of the arena.
        // If so, the player must be removed from the game
        int size = player.size();
        for (int i = 0; i < size; i++) {
            GameState.Player p = player.get(i);
            p.collisions();  // Compute collisions for this player

        }
    }
    
    private synchronized void movePlayers() {
    	ArrayList<GameState.Player> player = gameState.getPlayers();
    	int size = player.size();
        for (int i = 0; i < size; i++) {
            GameState.Player p = player.get(i);
            p.move();

        }        
        
        //next, we need to update the grid to reflect the new position of each player
        gameState.updateGrid();
    	
    }
    
    private synchronized boolean checkIfGameOver() {
    	ArrayList<GameState.Player> player = gameState.getPlayers();
    	int size = player.size();
    	if (size > 0)
    	{
    		int alive = 0;
            for (int i = 0; i < size; i++) {
                GameState.Player p = player.get(i);
                if (!p.dead)
                {
                	alive++;
                }
            }   
            //a multiplayer game ends when there is only one player left standing
            if (size > 1)
            {
            	return alive == 1;	
            }
            else
            {
            	//in single player mode, the game simply ends when the only player dies
            	return alive == 0;
            }
    	}
    	
    	return false;
    	
        
       	
    }
}

