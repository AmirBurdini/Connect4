import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class Cell extends JPanel implements MouseInputListener {

    static final int EMPTY = 0;
    static final int PLAYER1 = 1;
    static final int PLAYER2 = 2;
    
    int x;
    int y;
    int score;
    int state;

    Board parent;

    // Constructor
    public Cell(int x, int y, Board parent) {

        setBackground(Color.BLUE);
        state = EMPTY;
        this.x = x;
        this.y = y;
        this.parent = parent;
        addMouseListener(this);
        score = 0;
        repaint();
    }

    // Mouse Click Event Listener
    public void mouseClicked(MouseEvent arg0) {

        if (!parent.gameOver)
        {
            parent.CurrCol = y;
            parent.insertToken(parent.CurrCol, parent.board,false);
            parent.click = true;

            Game g = parent.parent;

            if (g.mode == Game.MULTIPLAYER && !parent.gameOver) {

                parent.insertToken(g.rival.roboMax(g.rival.moves,
                g.gameBoard.board, true), parent.board, true);
                // parent.print(parent.board);
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