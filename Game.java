import javax.swing.*;

public class Game extends JFrame
{
    final static int MULTIPLAYER = 1,SINGLEPLAYER = 2;

    Board gameBoard;
    AI rival;
    int mode;

    public Game(int mode)
    {
        gameBoard = new Board(this);
        this.mode = mode;

        if(mode == MULTIPLAYER)
        {
            rival = new AI(3, gameBoard,2);
        }
        
        add(gameBoard);
        setVisible(true);
        setResizable(false);
        setLocation(400, 50);
        setSize((Board.RADIUS + 15) * 7 + 30 ,(Board.RADIUS + 15) * 6 + 40);
    }
}