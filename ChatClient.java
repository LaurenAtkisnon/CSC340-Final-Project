import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
    private Bufferedreader br; //reads message from server
    private JTextArea chat; //displays the chat
    private JTextField newMsg; //text field for new messages
    private PrintWinter pw; //sends the message to the server
    private Socket sock; //socket connection
    private String username; //username for chat

    /*
    Creates client for chat
     */

    public ChatClient(JTextArea chat, JTextField newMsg){
        username = " ";
        this.chat = chat;
        this.newMsg = newMsg;

        newMsg.addActionListener(actionEvent -> {
            //user shouldnt send empty message
            if(!newMsg.getText().equals("")) {
                send(username + ": " + newMsg.getText());
                newMsg.setText("");
            }
        });
    }

    /*
    CHAT SERVER CODE HERE
     */
    public boolean connect(String host, String username){
        this.username = username;

    }
    //sends a message to the chat server
    public void send(String msg) {
        pw.println(msg);
        pw.flush();
    }

    public void closeSocket(){
        try {

        }

        /*
        Runnable inner class that listens for messages from the chat server
         */
        class ReceiveMessage extends Thread{
            /*
            runs the listener, runs indefinitely
             */
            public void run() {
                while (true) {
                    try {
                        chat.append(br.readLine() + "\n");
                    } catch (IOException e) {
                        break;
                    }
        }
    }
}
