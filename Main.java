/***************
 * Team Members: Lauren Atkinson, Timothy Carta, Ryan Hayes, Griffin King, Charles Rescsanski
 * Spring 21 | CSC340
 * Created By: Lauren
 * Modified by: Charles Rescsanski, Timothy Carta, Ryan Hayes
 * Main game GUI
 ***************/

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
    private JMenuItem Credit; // creidts
    private Grid gameGrid; // shows the trail on the main GUI
    private String username; // username for chat capabilites
    private String hostname = "127.0.0.1";
    private int port = GameServer.DEFAULT_PORT;
    private boolean playerMode = true;
    private Color color = Color.BLUE;

    public static void main(String[] args) {
        new Main();
    }

    // GUI
    public Main() {
        JMenuBar menuBar = new JMenuBar();
        JMenu jmFile = new JMenu("File");
        Exit = new JMenuItem("Exit");
        jmFile.add(Exit);
        menuBar.add(jmFile);
        JMenu jmHelp = new JMenu("Help");
        About = new JMenuItem("About");
        jmHelp.add(About);

        Credit = new JMenuItem("Credit");
        jmHelp.add(Credit);
        menuBar.add(jmHelp);
        setJMenuBar(menuBar);


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
            } 
        };

        Exit.addActionListener(menuListener);
        About.addActionListener(menuListener);
        Credit.addActionListener(menuListener);

        pack();
        setLocationRelativeTo(null);
        setTitle("Bikes");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        
      
        this.startupDialog();
        gameGrid.connect(hostname, username, port);
        
        while(gameGrid.getConnectStatus() != true)
        {
        	if (gameGrid.getConnectStatus() == false)
        	{
        		JOptionPane.showMessageDialog(null, "Error: A connection could not be established with the specified server.");
        		
        		this.startupDialog();
        		gameGrid.resetConnectionStatus();
        		gameGrid.connect(hostname, username, port);
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
   
        // Create animation
        Timer animationTimer; // A Timer that will emit events to force redrawing of game state
        animationTimer = new Timer(16, new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                gameGrid.repaint();
            }
        });
        animationTimer.start();
    }
    
    public void startupDialog()
    {
	  JPanel pane = new JPanel();
      pane.setLayout(new GridLayout(0, 2, 2, 2));
      JTextField host = new JTextField(hostname, 15);
      JTextField portNum = new JTextField(Integer.toString(port), 5);
      JTextField playerName = new JTextField(username, 15);
      String[] values = {"Player", "Spectator"};
      JComboBox<String> box = new JComboBox<String>(values);
      box.setSelectedIndex(0);
      pane.add(new JLabel("Server IP/hostname:"));
      pane.add(host);
      pane.add(new JLabel("Server PORT:"));
      pane.add(portNum);
      pane.add(new JLabel("Player Name:"));
      pane.add(playerName);
      pane.add(new JLabel("Game Mode:"));
      pane.add(box);
    
      int option = JOptionPane.showConfirmDialog(this, pane, "Join a Game Server", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
      
      if (option == JOptionPane.OK_OPTION) {
      	String newHostName = host.getText();
      	if (newHostName != null && newHostName.length() > 0)
            hostname = newHostName;
      	else {
      		JOptionPane.showMessageDialog(null, "The hostname cannot be empty.", "Invalid Hostname", JOptionPane.ERROR_MESSAGE);
      		this.startupDialog();
      		return;
      	}
      	String newUserName = playerName.getText();
      	if (newUserName != null && newUserName.length() > 0)
            username = newUserName;
      	else {
      		JOptionPane.showMessageDialog(null, "The player name cannot be empty.", "Invalid Player Name", JOptionPane.ERROR_MESSAGE);
      		this.startupDialog();
      		return;
      	}
      	if (portNum.getText() != null && portNum.getText().length() > 0) {
              try {
                  int p = Integer.parseInt(portNum.getText());
                  if (p < 0 || p > 65535) {
                      JOptionPane.showMessageDialog(null, "The port [" + portNum.getText() + "] must be in the range 0 to 65535.", "Invalid Port Number", JOptionPane.ERROR_MESSAGE);
                      this.startupDialog();
                      return;
                  } else {
                      port = p;  // Valid.  Update the port
                  }
              } catch (NumberFormatException ignore) {
                  JOptionPane.showMessageDialog(null, "The port [" + portNum.getText() + "] must be an integer.", "Number Format Error", JOptionPane.ERROR_MESSAGE);
                  this.startupDialog();
                  return;
              }
          }
      	
      	if (box.getSelectedItem() == "Spectator")
      	{
      		playerMode = false;
      	}
      }
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
