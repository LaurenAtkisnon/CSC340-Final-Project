/***************
 * Team Members: Lauren Atkinson, Timothy Carta, Ryan Hayes, Griffin King, Charles Rescsanski
 * Spring 21 | CSC340
 *
 * Created By: Lauren
 * Modified by: Charles, Timothy, Ryan
 *
 * connects client to the server -- processes the commands between the client and the server
 * also creates threads
 ***************/
import javax.swing.*;

import java.awt.Color;
import java.io.*;
import java.net.*;



public class NetworkConnector {


    //port
    private int port = GameServer.DEFAULT_PORT;
    private String hostname;
    private String username;
    private Debug debug;
    private Boolean status; //represent whether or not a connection has been established with the server
    private Socket socket;
    private ObjectOutputStream out; //output stream for sending objects to server
    private ObjectInputStream in; //intput stream for receiving objects from server
    private Grid grid;
    private GameState gameState;

    //creates instance that connects host to the server
    public NetworkConnector(String hostname, String username, int port, Grid grid){
    	this.debug = Debug.getInstance();
    	this.port = port;
        this.username = username;
        this.hostname = hostname;
        this.grid = grid;

        //establish connection with the server
        if(establishConnection())
        {
            //startup a thread that received and processes incoming communication from the server
            Connection connection = new Connection();
            connection.start();
        }


    }

    public boolean establishConnection() {
        // Establish connection with the Server
    try{
	    socket = new Socket(hostname, port);
	    out = new ObjectOutputStream(socket.getOutputStream());
	    in = new ObjectInputStream(socket.getInputStream());
	    this.status = true; //a connection has been established
	    return true;
    }
        catch (UnknownHostException e)
        {
        this.status = false;
	    System.out.println("Unknown host: " + hostname);
	    System.out.println("             " + e.getMessage());
        }
        catch (IOException e) {
        	this.status = false;
        	System.out.println("IO Error: Error establishing communication with server.");
        	System.out.println("          " + e.getMessage());
        }

    	return false;
    }

    public void retry(String hostname, int port)
    {
    	this.hostname = hostname;
    	this.port = port;
    	//establish connection with the server
        if(establishConnection())
        {
            //startup a thread that received and processes incoming communication from the server
            Connection connection = new Connection();
            connection.start();
        }
    }

    public Boolean getStatus()
    {
    	return this.status;
    }
    public void resetStatus()
    {
    	this.status = null;
    }

    void registerPlayer(Color color, Boolean playMode)
    {
    	//System.out.println("Let's ask the server if we can join the game.");
        JoinMessage message = new JoinMessage(this.username, color, playMode);
        transmitMessage(message);

    }

    public void updateDirection(int newDirection)
    {
    	transmitMessage(new ChangeDirectionMessage(newDirection));
    }

    public String getUserName()
    {
    	return this.username;
    }

    private void transmitMessage(Object message)
    {
        try{
            synchronized(out)
        {
            debug.println(3, "Transmitting message to server. " + message);
            out.writeObject(message);
            out.flush(); //ensures that info gets sent out immediately
        }
        }
        catch (IOException e)
        {
            debug.println(3, "Error transmitting message: " + message);
        }

        }


    public GameState getGameState()
    {
    	return this.gameState;
    }


    /*
    runnable class that opens the sits and waits for data to be
    sent back and processes
     */
    public class Connection extends Thread {
        boolean done = false;
        public void run() {
        	//System.out.println("The thread to process data received from the server has started!");
            while (!done)
            {
            	//System.out.println("The thread to process data received from the server has started!");
                try {
                	//System.out.println("Let's try reading in a message");
                    Object message = in.readObject();
                   // System.out.println("A message has been received by the server");
                    processMessage(message);
                }
                catch (ClassNotFoundException e)
                {
                    System.out.println("Coding error: Server transmitted an unrecognized Object.");
                }
                catch (IOException e) {
                    System.out.println("I/O Error while communicating with Server.  Terminating connection.");
                    System.out.println("    Message: " + e.getMessage());
                }
            }

        }
        /*
        Process one message that has been sent through this connection.
        */
        private void processMessage(Object message) {
            if (message instanceof JoinResponseMessage) {
                processJoinResponseMessage((JoinResponseMessage) message);

            } else if (message instanceof GameState) {
            	//debug.println(3, "The GameState has been received!");
                processGameStateMessage((GameState) message);
            } else {
                debug.println(3, "[ Connection ] Unrecognized message: " + message);
            }
        }

        private void processJoinResponseMessage(JoinResponseMessage message) {
            username = message.name;
            if (message.playerID >= 0)
            {
                grid.setPlayerID(message.playerID);
            }
        }

        private void processGameStateMessage(GameState gs)
        {
            gameState = gs;
            if (!gs.getGameActivity())
            {
            	grid.displayWinLose(gs);
            }
            else {
            	grid.clearMessage();
            }
        }

        /**
         * Print out the message (with a little name id in front)
         * @param string
         */
        private void printMessage(String string) {
        	System.out.println(string);
        }

    }

}
