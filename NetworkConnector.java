/***************
 * Team Members: Lauren Atkinson, Timothy Carta, Ryan Hayes, Griffin King, Charles Rescanscki
 * Spring 21 | CSC340
 *
 * connects client to the server -- processes the commands between the client and the server
 * also creats threads
 ***************/

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class NetworkConnector {

    //port
    private static final int PORT = 8888;

    private String hostname;
    private String username;
    private int userId =-1;
    private Socket s;
    private PrintWriter out; //double check if necessary
    private String[] otherPLayers;
    private Grid grid;


    //creates instance that connects host to the server
    public NetworkConnector(String hostname, String username, Grid grid){
        this.username = username;
        this.hostname = hostname;
        this.grid = grid;
        connect();
    }
    /*
    feel like there should be a method here that connects the server w the port but
    it feels redundant
     */


    //send the current x&y position of the user bike (NOT COMP)
    public void sendLocation(int x, int y) {
        sendCommand("set-location", ("" + x + "," + y));
    }
    //tell the server that the user dies
    public void notifyDeath() {
        sendCommand("set-dead","true");
    }

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
private void startGame(String value){
    grid.startGame(userID);
}
//processes usernames from server and stores them
    private void setPsayers(String value){
    otherPlayers = value.split(", ");
    }

    //updates location of remote bike
    private void updateLocation(String value){
    String[] pair = value.split(", ");
    grid.getServerBike().setLocation(Integer.parseInt(pair[0]), Integer.parseInt(pair[1]))
    }

    //sets the user ID which determins the color and start location
    private void setUserID(String value){
    userId = Integer.parseInt(value);
    }

    //get userID
    public int getUserID(){
    return userID;
    }

    /*
    runnable class that opens the sicks and waits for data to be
    sent back and processes
     */

    /*
    method for listener class (if making one) waits for the data to be
    received from the server & itll just run
    doesnt get calles directly & is ran as a seperate thread
    so just parse commands
     */
}
