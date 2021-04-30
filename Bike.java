import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

public class Bike {
    public static final int DIRECTION_NORTH = 0; //north
    public static final int DIRECTION_EAST = 1; //EAST
    public static final int DIRECTION_SOUTH = 2; //SOUTH
    public static final int DIRECTION_WEST = 3; //WEST

    public int player; //player id
   
    private NetworkConnector connector; //network

    public Bike(int playerID, NetworkConnector _connector){
  
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
    
    
   
    
    public void setID(int id)
    {
    	this.player = id;
    }
    
    
	
}
