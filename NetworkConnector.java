/*
connects client to the server -- processes the commands between the client and the server
also creats threads
 */


import javax.swing.*;

import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.Scanner;


public class NetworkConnector {


    //port
    private int port = GameServer.DEFAULT_PORT;
    private String hostname;
    private String username;
    private Debug debug;
    private int userId =-1;
    private Socket socket;
    private Connection connection = null;
    private ObjectOutputStream out; //output stream for sending objects to server
    private ObjectInputStream in; //intput stream for receiving objects from server
    private String[] otherPLayers;
    private Grid grid;
    private GameState gameState;

    //creates instance that connects host to the server
    public NetworkConnector(String hostname, String username, Grid grid){
    	this.debug = debug.getInstance();
        this.username = username;
        this.hostname = hostname;
        this.grid = grid;

        //establish connection with the server
        establishConnection();

        //startup a thread that received and processes incoming communication from the server
        Connection connection = new Connection();
        connection.start();
    }
    /*
    feel like there should be a method here that connects the server w the port but
    it feels redundant
     */

    public void establishConnection() {
        // Establish connection with the Server
    try{
	    socket = new Socket(hostname, port);
	    out = new ObjectOutputStream(socket.getOutputStream());
	    in = new ObjectInputStream(socket.getInputStream());
    }   
        catch (UnknownHostException e) 
        {
	    System.out.println("Unknown host: " + hostname);
	    System.out.println("             " + e.getMessage());
        } 
        catch (IOException e) {
        	System.out.println("IO Error: Error establishing communication with server.");
        	System.out.println("          " + e.getMessage());
        } 
    }
    
    void registerPlayer(Color color)
    {
    	//System.out.println("Let's ask the server if we can join the game.");
        JoinMessage message = new JoinMessage(this.username, color);
        transmitMessage(message);
        
    }    
    //send the current x&y position of the user bike (NOT COMP)
    public void sendLocation(int x, int y) {
        //sendCommand("set-location", ("" + x + "," + y));
    	transmitMessage(new MovePlayerMessage(x, y));
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
    
    /*
    //tell the server that the user dies
    public void notifyDeath() {
        sendCommand("set-dead","true");
    }
    */
    
    public GameState getGameState()
    {
    	return this.gameState;
    }

    /*
    //comannd string to send code back to server -- may not be necessary for the requirements of project
    public void pressCommand(String cmdString){
        String[] temp = cmdString.split(":");
        String command = temp[0];
        String value = temp[1];


        switch(command) {
            case "rsp-user-id":
                setUserID(value);
                break;


            case "rsp-game-start":
                startGame(value);
                break;


            case "rsp-username-list":
                setPlayers(value);
                break;


            case "rsp-update-location":
                updateLocation(value);
                break;


            case "rsp-dead":
                grid.stop();
                grid.won();
                break;
        } }
    /*
    methods to process different commands from the server
     */


    //calls the method in Grid
	private void startGame(int userID){
	    grid.startGame(userID);
	}
	
	/*
	//processes usernames from server and stores them
    private void setPsayers(String value){
    otherPlayers = value.split(", ");
    }
	*/

	
	/*
    //updates location of remote bike
    private void updateLocation(String value){
    String[] pair = value.split(", ");
    grid.getServerBike().setLocation(Integer.parseInt(pair[0]), Integer.parseInt(pair[1]));
    }
	*/

    //sets the user ID which determins the color and start location
    private void setUserID(String value){
    userId = Integer.parseInt(value);
    }

    /*
    //get userID
    public int getUserID(){
    return userID;
    }
	*/

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
            grid.setPlayerID(message.playerID);
        }

        private void processGameStateMessage(GameState gs)
        {
            gameState = gs;
        }

        /**
         * Print out the message (with a little name id in front)
         * @param string
         */
        private void printMessage(String string) {
        	System.out.println(string);
        }

    }

  

    /*
    method for listener class (if making one) waits for the data to be
    received from the server & itll just run
    doesnt get calles directly & is ran as a seperate thread
    so just parse commands 
     */
}