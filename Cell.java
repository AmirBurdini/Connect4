import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class Cell extends JPanel implements MouseInputListener {

    // possible cell States
    enum state {
        EMPTY,
        PLAYER1,
        PLAYER2
    }
    static final int EMPTY = 0;
    static final int PLAYER1 = 1;
    static final int PLAYER2 = 2;
    
    int x; // x coordinates
    int y; // y coordinates
    int score; // score based on the relative position in the parent board
    //int state; // what token is in the cell
    state cellState;

    Board parent; // parent Board

    // Constructor
    public Cell(int x, int y, Board parent) {

        // init 
        setBackground(Color.BLUE);
        // state = EMPTY;
        cellState = state.EMPTY;
        this.x = x;
        this.y = y;
        this.parent = parent;

        // mouse click listener
        addMouseListener(this);

        // score is reset by Board class
        score = 0;
    }

    // paint component methods :

    // main paintComponent
    public synchronized void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        drawToken(g,cellState, 10,10);

        // String source = "CellTexture.png";
        // Image img = new ImageIcon(source).getImage();
        // g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
	}

    // draws token
    public void drawToken(Graphics g, state cellState, int x, int y)
    {
        Color c = Color.black;

        switch (cellState) {

            case EMPTY : {
                break;
            }

            case PLAYER1 : {
                c = Color.YELLOW;
                break;
            }
            
            case PLAYER2 : {
                c = Color.RED;
                break;
            }
        }

        g.setColor(c);
        g.fillOval(x ,y, Board.RADIUS,Board.RADIUS);  
    }


    // MouseInputListener methods implementation :

    // Mouse Click Event Listener
    public void mouseClicked(MouseEvent event) {

        if (!parent.gameOver) // eliminate the possibilty someone already won
        {
            // which coloumn the token is dropped in 
            parent.CurrCol = y; 

            parent.insertToken(parent.CurrCol, parent.board, false);
            
            // the insert operation caused by a mouse click
            // not caused by AI operation
            parent.click = true; 

            Game g = parent.parent;

            // if singlePlayer mode is played,AI response required
            if (g.mode == Game.MULTIPLAYER && !parent.gameOver) {

                parent.insertToken(g.rival.nextMove(g.rival.moves,
                g.gameBoard.board, true), parent.board, true);
                
                // after AI response,switch turns
                parent.Turn = !parent.Turn;
            }
        }
    }

    // not used
    public void mouseEntered(MouseEvent e) {
    }

    // not used
    public void mouseExited(MouseEvent e) {
    }

    // not used
    public void mousePressed(MouseEvent e) {

    }

    // not used
    public void mouseReleased(MouseEvent e) {

    }

    // not used
    public void mouseDragged(MouseEvent e) {
    }

    // not used
    public void mouseMoved(MouseEvent e) {
    }
}