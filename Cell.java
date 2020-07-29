import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class Cell extends JPanel implements MouseInputListener {

    // possible cell States
    static final int EMPTY = 0;
    static final int PLAYER1 = 1;
    static final int PLAYER2 = 2;
    
    int x; // x coordinates
    int y; // y coordinates
    int score; // score based on the relative position in the parent board
    int state; // what token is in the cell

    Board parent; // parent Board

    // Constructor
    public Cell(int x, int y, Board parent) {

        // init 
        setBackground(Color.BLUE);
        state = EMPTY;
        this.x = x;
        this.y = y;
        this.parent = parent;

        // mouse click listener
        addMouseListener(this);

        // score is reset by Board class
        score = 0;
    }

    // Mouse Click Event Listener
    public void mouseClicked(MouseEvent arg0) {

        if (!parent.gameOver) // eliminate the possibilty someone already won
        {
            // which coloumn the token is dropped in 
            parent.CurrCol = y; 

            parent.insertToken(parent.CurrCol, parent.board,false);
            
            // the insert operation caused by a mouse click
            // not caused by AI operation
            parent.click = true; 

            Game g = parent.parent;

            // if singlePlayer mode is played,AI response required
            if (g.mode == Game.MULTIPLAYER && !parent.gameOver) {

                parent.insertToken(g.rival.roboMax(g.rival.moves,
                g.gameBoard.board, true), parent.board, true);
                
                // after AI response,switch turns
                parent.Turn = !parent.Turn;
            }
        }
    }

    public void mouseEntered(MouseEvent arg0) {

    }

    public void mouseExited(MouseEvent arg0) {

    }

    public void mousePressed(MouseEvent arg0) {

    }

    public void mouseReleased(MouseEvent arg0) {

    }

    public void mouseDragged(MouseEvent arg0) {
    }

    public void mouseMoved(MouseEvent arg0) {

    }

    // main paintComponent
    public synchronized void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        drawToken(g, state,10,10);

        // String source = "CellTexture.png";
        // Image img = new ImageIcon(source).getImage();
        // g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
	}

    // draws token
    public void drawToken(Graphics g, int state,int x,int y)
    {
        Color c = Color.black; 
        if(state == PLAYER1) c = Color.YELLOW;
        if(state == PLAYER2) c = Color.RED;
        g.setColor(c);
        g.fillOval(x ,y, Board.RADIUS,Board.RADIUS);  
    }
}