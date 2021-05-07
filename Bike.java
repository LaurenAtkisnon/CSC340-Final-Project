/***************
 * Team Members: Lauren Atkinson, Timothy Carta, Ryan Hayes, Griffin King, Charles Rescanscki
 * Spring 21 | CSC340
 * Created By: Lauren
 * Modified by: Charles Rescsanski, Timothy Carta, Ryan Hayes, Griffin King
 * Main game GUI
 ***************/

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
        connector.updateDirection(DIRECTION_WEST);
       }


    public void turnEast(){
    	connector.updateDirection(DIRECTION_EAST);
      }

    public void turnSouth(){
    
    	connector.updateDirection(DIRECTION_SOUTH);
     }

    public void turnNorth(){
    
    	connector.updateDirection(DIRECTION_NORTH);
    }

    public void setID(int id)
    {
    	this.player = id;
    }



}
