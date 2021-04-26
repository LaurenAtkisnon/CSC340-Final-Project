import java.io.Serializable;

/***************
 * Message
 * Author: Christian Duncan
 * Spring 21: CSC340
 * 
 * 
 * Server sends to the client their playerID and their name.
 ***************/

public class MovePlayerMessage extends Message implements Serializable {
    double playerDX;
    double playerDY;

    public MovePlayerMessage(double playerDX, double playerDY)
    {
        this.playerDX = playerDX;
        this.playerDY = playerDY;
    }
    
}
