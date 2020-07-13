import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class Menu 
{
    JButton PC,P2;
    JFrame mainFrame;

    // main function
    public static void main(String[] args) 
    {
        Menu m = new Menu();    
    }

    // Constructor
    public Menu()
    {
        mainFrame = new JFrame();

        JPanel options = new JPanel();
        PC = new JButton("single Player");
        P2 = new JButton("2 Player");
        
        PC.addActionListener(new GameMode());
        P2.addActionListener(new GameMode());

        options.setLayout(new FlowLayout());
        options.add(PC);
        options.add(P2);

        mainFrame.add(options);
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);
        mainFrame.setLocation(400, 50);
        mainFrame.setSize(Board.WIDTH * 80,Board.HEIGHT * 80);
    }

    // Action Listener
    private class GameMode implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
            JButton c = (JButton) e.getSource();
            int mode = 0;

            if(c == PC)
            {
                mode = 1;
            }

            if(c == P2)
            {
                mode = 2;
            }

            mainFrame = new Game(mode);
        }
    }
}