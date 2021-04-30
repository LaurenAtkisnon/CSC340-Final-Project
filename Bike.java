import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

public class Bike {
    public static final int DIRECTION_NORTH = 0; //north
    public static final int DIRECTION_EAST = 1; //EAST
    public static final int DIRECTION_SOUTH = 2; //SOUTH
    public static final int DIRECTION_WEST = 3; //WEST

    private static final int DELAY_IN_MILLS = 80; //between each movement

    public int xPosition;
    public int yPosition;
    public int[][] gridArray; //keeps tracks of locations
    public int player; //player id
    private int direction;
    public boolean gameState;
    private boolean pausePlayer; //for debugging
    private Grid grid;
    private NetworkConnector connector; //network

    public Bike(int playerID, NetworkConnector _connector){
        //pausePlayer = false;
    	//direction = initialDirection;
    	/*
        xPosition = _xPosition;
        yPosition = _yPosition;
        gridArray = _gridArray;
        player = _player;
        gameState = true;
        grid = _grid;
        gridArray[xPosition][yPosition] = player;
        //grid.repaint();
         * */
    	this.player = playerID;
    	this.connector = _connector;
         
    }
    //east directions & cant go backwards
    public void turnWest(){
    	/*
        if(direction!= DIRECTION_EAST){
            direction = DIRECTION_WEST;
        } 
        */
        connector.updateDirection(DIRECTION_WEST);
       }
    	

    public void turnEast(){
    	/*
        if(direction != DIRECTION_WEST){
            direction = DIRECTION_EAST;
        }
        */ 
    	connector.updateDirection(DIRECTION_EAST);
        }

    public void turnSouth(){
    	/*
        if(direction != DIRECTION_NORTH){
            direction = DIRECTION_SOUTH;
        } 
        */
    		connector.updateDirection(DIRECTION_SOUTH);
       }

    public void turnNorth(){
    	/*
        if(direction != DIRECTION_SOUTH){
            direction = DIRECTION_NORTH;
        } }
        */
    	connector.updateDirection(DIRECTION_NORTH);
    }
    
    public void stop(){
        gameState = false;
    }

    //verify if location is valid
    public boolean checkLocation(int x, int y) {
        return x > 0 && x < gridArray.length && y > 0 && y < gridArray[0].length && gridArray[x][y] == 0;
    }

    //registers current location
    public void updateLocation() {
        gridArray[xPosition][yPosition] = player;
        grid.repaint();
    }
    
    public void setID(int id)
    {
    	this.player = id;
    }
    
    public void pauseOrResume()
    {
    	this.pausePlayer = !pausePlayer;
    }

    public void setLocation(int x, int y){
        this.xPosition = x;
        this.yPosition = y;
        updateLocation();
    }

    //gets current gameState
    public boolean getGameState(){
        return gameState;
    }
    //x coordinates
    public int getXpos() {
        return xPosition;
    }
    //y coordinate
    public int getYpos() {
        return yPosition;
    }
    
    //state the game
    public void startGame(){
        new Thread(new Movement()).start();
    }

    //handles the movement of the GUI based on time delay
    
    class Movement implements Runnable {
        /*
        begins movement of the object within the GUI
         */
        @Override
        public void run() {
            NetworkConnector connector = grid.getConnector();
            while (gameState) {
            	if (!pausePlayer)
            	{
                    switch(direction) {

                    case DIRECTION_NORTH:
                        gameState = checkLocation(xPosition, yPosition-1);
                        if (gameState) {
                            yPosition--;
                        } break;

                    case DIRECTION_EAST:
                        gameState = checkLocation(xPosition+1, yPosition);
                        if (gameState) {
                            xPosition++;
                        } break;

                    case DIRECTION_SOUTH:
                        gameState = checkLocation(xPosition, yPosition+1);
                        if (gameState) {
                            yPosition++;
                        } break;

                    case DIRECTION_WEST:
                        gameState = checkLocation(xPosition-1, yPosition);
                        if (gameState) {
                            xPosition--;
                        } break;
                    }
            	}
    
                /*
                if (!gameState) {
                    grid.stop();
                    connector.notifyDeath();
                    grid.lost();
                    break;
                }
                */

                // Send the server the bike's new location
                //connector.sendLocation(xPosition, yPosition);

                // Show the new location on the grid
                updateLocation();

                // Take a break
                try{
                    TimeUnit.MILLISECONDS.sleep(DELAY_IN_MILLS);
                }
                catch(InterruptedException ie){
                    ie.printStackTrace();
                } } } }
	
}
