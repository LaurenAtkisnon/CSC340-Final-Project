import java.io.Serializable;
import java.awt.Color;

/***************
 * Message
 * Author: Christian Duncan
 * Spring 21: CSC340
 * 
 * 
 * Represents a join message to transmit.
 *    Client sends this message to the server.
 *    Contains the name and the color
 ***************/

public class JoinMessage extends Message implements Serializable {
    String name; // the name the client wishes to use
    Color color; // the color that the client would like to use
    Boolean playMode; //true if the user would like to be a player

    public JoinMessage(String name, Color color, Boolean playMode)
    {
        this.name = name;
        this.color = color;
        this.playMode = playMode;
    }
    
}
