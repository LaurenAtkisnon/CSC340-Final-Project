import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.*;
import java.awt.event.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

public class Main extends JFrame implements KeyListener, MouseListener {
    // identifies the version of the game
    private final String GAME_VERSION = "0.1";
    private JMenuItem Exit; // exit button
    private JMenuItem About; // about button
    private JMenuItem serverPort; //for changing server Port
    private JMenuItem serverIP; //for changing server ip
    private JMenuItem Credit; // creidts
    private Grid gameGrid; // shows the trail on the main GUI
    private String username; // username for chat capabilites
    private String hostname = "127.0.0.1";
    private int port = GameServer.DEFAULT_PORT;
    private Color color = Color.BLUE;

    public static void main(String[] args) {
        new Main();
    }

    // GUI
    public Main() {
        JMenuBar menuBar = new JMenuBar();
        JMenu jmFile = new JMenu("File");
        Exit = new JMenuItem("Exit");
        serverPort = new JMenuItem("Change Server Port");
        serverIP = new JMenuItem("Change Server IP");
        jmFile.add(serverPort);
        jmFile.add(serverIP);
        jmFile.add(Exit);
        menuBar.add(jmFile);
        JMenu jmHelp = new JMenu("Help");
        About = new JMenuItem("About");
        jmHelp.add(About);
        
        Credit = new JMenuItem("Credit");
        jmHelp.add(Credit);
        menuBar.add(jmHelp);
        setJMenuBar(menuBar);
        

        /* chat
        JPanel chatFrame = new JPanel(new BorderLayout());
        JTextArea chat = new JTextArea(25, 60);
        chat.setLineWrap(true);
        chat.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chat);
        DefaultCaret caret1 = (DefaultCaret) chat.getCaret();
        caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        chatScroll.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

        JTextField msg = new JTextField(25);

        // ChatClient chatClient = new ChatClient(chat,msg);

        add(chatFrame, BorderLayout.EAST);
        chatFrame.add(chatScroll, BorderLayout.CENTER);
        chatFrame.add(msg, BorderLayout.SOUTH);
        */

        // grid for game
        gameGrid = new Grid();
        add(gameGrid, BorderLayout.CENTER);

        // actionlistener for menu
        ActionListener menuListener = ae -> {

            Object choice = ae.getSource();

            if (choice == Exit) {
                System.exit(0);
            } else if (choice == About) {
                JOptionPane.showMessageDialog(null,
                        "LightBikes is a multiplayer game between two people. In the " + "game, both players\n"
                                + "are controlling a \"bike\" across a grid-based area. As the player moves across\n"
                                + "the board, they leave a trail of \"light\" behind them. If a player runs into the\n"
                                + "light trail (either their's'or the opponent's), they will lose. Based\n"
                                + "same game from the TRON Legacy(Movvie).\n\n" + "CONTROLS\n"
                                + "Use the arrow keys to move. The game will start after opponent connects.\n"
                                + "Spectators can connect and chat while you are playing.");
            } else if (choice == Credit) {
                JOptionPane.showMessageDialog(null, "LightBikes v" + GAME_VERSION + "\n" + "Created in May 2021.\n\n"
                        + "DEVELOPERS:\n" + "L.Atkinson\n" + "T.Carta\n" + "R.Hayes\n" + "C.Rescsanki");
            } else if (choice == serverPort)
            {
            	String portName = JOptionPane.showInputDialog("Please enter a server PORT.\nThis only takes effect after the next connection attempt.\nCurrent port: " + port);
                if (portName != null && portName.length() > 0) {
                    try {
                        int p = Integer.parseInt(portName);
                        if (p < 0 || p > 65535) {
                            JOptionPane.showMessageDialog(null, "The port [" + portName + "] must be in the range 0 to 65535.", "Invalid Port Number", JOptionPane.ERROR_MESSAGE);
                        } else {
                            port = p;  // Valid.  Update the port
                        }
                    } catch (NumberFormatException ignore) {
                        JOptionPane.showMessageDialog(null, "The port [" + portName + "] must be an integer.", "Number Format Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (choice == serverIP)
            {
            	 String newHostName = JOptionPane.showInputDialog("Please enter a server IP/Hostname.\nThis only takes effect after the next connection attempt.\nCurrent server address: " + hostname);
                 if (newHostName != null && newHostName.length() > 0)
                     hostname = newHostName;
            }
        };

        serverIP.addActionListener(menuListener);
        serverPort.addActionListener(menuListener);
        Exit.addActionListener(menuListener);
        About.addActionListener(menuListener);
        Credit.addActionListener(menuListener);

        pack();
        setLocationRelativeTo(null);
        setTitle("Bikes");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

       
        username = JOptionPane.showInputDialog(null, "Enter your desired username:");
        gameGrid.connect(hostname, username, port);
        while(gameGrid.getConnectStatus() != true)
        {
        	if (gameGrid.getConnectStatus() == false)
        	{
        		JOptionPane.showMessageDialog(null, "Error: A connection could not be established with the specified server.");
        		hostname = JOptionPane.showInputDialog(null, "Please enter a different hostname.");
        	    try 
    	        {
    	        	port = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter the server port:"));
    	    	}
    	        catch (NumberFormatException e)
    	        {
    	        	port = GameServer.DEFAULT_PORT;
    	        }
        		gameGrid.resetConnectionStatus();
        		gameGrid.connect(hostname, username, port);
        	}
        }
        
        String[] values = {"Player", "Spectator"};
        Boolean playerMode = true;
        
        Object selected = JOptionPane.showInputDialog(null, "Choose the Mode:", "Selection", JOptionPane.DEFAULT_OPTION, null, values, "Player");
        if (selected != null)
        {
        	String selectedOption = selected.toString();
        	
        	if (selectedOption == "Spectator")
        	{
        		playerMode = false;
        	}
        }
        if (playerMode)
        {
        	 color = JColorChooser.showDialog(Main.this, "Select your color!", Color.BLUE);
        }

        // "Register" the player with the server
        gameGrid.registerPlayer(color, playerMode);

        gameGrid.setFocusable(true);
        gameGrid.addKeyListener(this);
        gameGrid.addMouseListener(this);
        gameGrid.requestFocus();
        // chatClient.connect(hostname, username);

        // Create animation
        Timer animationTimer; // A Timer that will emit events to force redrawing of game state
        animationTimer = new Timer(16, new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                gameGrid.repaint();
            }
        });
        animationTimer.start();
    }

    /**
     * Key listeners to listen for user input to control the bike.
     */
    @Override
    public void keyPressed(KeyEvent ke) {
    	if (gameGrid.getPlayerStatus())
    	{
    		switch (ke.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                gameGrid.turnEast();
                break;
            case KeyEvent.VK_LEFT:
                gameGrid.turnWest();
                break;
            case KeyEvent.VK_UP:
                gameGrid.turnNorth();
                break;
            case KeyEvent.VK_DOWN:
                gameGrid.turnSouth();
                break;
            case KeyEvent.VK_D:
                gameGrid.turnEast();
                break;
            case KeyEvent.VK_A:
                gameGrid.turnWest();
                break;
            case KeyEvent.VK_W:
                gameGrid.turnNorth();
                break;
            case KeyEvent.VK_S:
                gameGrid.turnSouth();
                break;
            /*    
            case KeyEvent.VK_SPACE:
                gameGrid.pauseMovement(); // for debugging purposes
                break;
             */
            default:
                break;
    		}
        
        }
    }

    /**
     *
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        gameGrid.requestFocus();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /*
     * A connection to handle incoming communincation from the server.
     */
}
