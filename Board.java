import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

public class Board extends JPanel {
    
    static final int HEIGHT = 6;
    static final int WIDTH = 7;
    static final int STREAK = 4;
    static final int RADIUS = 80;

    Cell[][] board; 
    int CurrCol; 
    Game parent;
    Fall fallingToken;
    boolean Turn;
    boolean click;
    boolean gameOver;
    ArrayList<ArrayList<Cell>> WinningStreak;
    
    // Constructor
    public Board(Game parent){
        
        setLayout(new GridLayout(HEIGHT,WIDTH));
        board = new Cell[HEIGHT][WIDTH];
        initBoard(board);
        totalCellScore(board);
        printScore(board);
        
        Turn = true;
        click = false;
        gameOver = false;
        CurrCol = -1;
        fallingToken = new Fall(this);
        this.parent = parent;
    }

    // initiate Board
    public void initBoard(Cell[][] board){

        for(int i = 0 ; i < HEIGHT ; i++){

            for(int j = 0 ; j < WIDTH ; j++){

                board[i][j] = new Cell(i,j,this);
                add(board[i][j]);  
            } 
        }
    }
    
    // creates a clone
    public void copyBoard(Cell[][] aux){

        for (int i = 0; i < HEIGHT; i++){

            for (int j = 0; j < WIDTH; j++){
            
                aux[i][j] = new Cell(i,j,this);
                aux[i][j].state = board[i][j].state;
            }
        }
    }

    // prints the board
    public void print(Cell[][] board){

        System.out.println();

        for(int i = 0 ; i < HEIGHT ; i++){

            for(int j = 0 ; j < WIDTH ; j++){

                System.out.print("[" + board[i][j].state + "]") ;
            }

            System.out.println() ;
        }
    }
    
    // prints the board
    public void printScore(Cell[][] board){

        System.out.println();

        for(int i = 0 ; i < HEIGHT ; i++){

            for(int j = 0 ; j < WIDTH ; j++){

                System.out.print("[" + board[i][j].score + "]") ;
            }

            System.out.println() ;
        }
    }

    // calculates the total number of Streak for each Cell
    public void totalCellScore(Cell[][] board){

        for (int i = 0 ; i < HEIGHT; i++){

            colScore(board, i); // adds the coloumn streaks
        }
        
        for (int i = 0 ; i < WIDTH; i++){

            rowScore(board, i); // adds the row streaks
        }

        diagonalScore(board); // adds the diagonal streaks
        
    }

    // calculates how many row streaks contain a specific Cell
    public void rowScore(Cell[][] board, int row)
    {
        int score = 1;
        for (int i = 0, j = HEIGHT - 1; i <= j; i++, j--){

            board[i][row].score += score;
            if (i < j){ // avoid adding twice the score in case of an odd num of rows
                board[j][row].score += score; 
            } 
            // add the current score to the cell

            score++;
        }
    }

    // calculates how many coloumn streaks contain a specific Cell
    public void colScore(Cell[][] board, int col)
    {
        int score = 1;
        for (int i = 0, j = WIDTH - 1; i <= j; i++, j--){

            board[col][i].score += score;
            if (i < j){ // avoid adding twice the score in case of an odd num of cols
                board[col][j].score += score;
            } 
            // add the current score to the cell

            score++;
        }
    }

    // calculates how many diagonal streaks contain a specific Cell
    public void diagonalScore(Cell[][] board){

        HashMap<Integer,ArrayList<Cell>> mainMap,secMap;
        ArrayList<ArrayList<Cell>> streakList;

        mainMap = diagonalCells(board, 1);
        streakList = streakList(mainMap);
        
        streakList.forEach((s) -> 
        s.forEach((c) -> c.score++));
    
        secMap = diagonalCells(board, 0);   
        streakList = streakList(secMap);

        streakList.forEach((s) -> s.forEach((c) -> c.score++));
    }

    // turns hashmap into list of streaks
    public ArrayList<ArrayList<Cell>> streakList(HashMap<Integer, 
    ArrayList<Cell>> map){
        
        ArrayList<ArrayList<Cell>> streaklist = new ArrayList<>();
        map.forEach((k,v) -> { // iterate through the map
            for (int i = 0; i < v.size(); i++){ // for each cell in the ArrayList
                
                ArrayList<Cell> aux = new ArrayList<>();
                for (int j = 0; j < STREAK; j++){ // try to find Streak
                    
                    // only if sum of indexes is still pointing to a cell
                    if (i + j < v.size()){
                        aux.add(v.get(i + j));
                    } 
                }
                if (aux.size() == STREAK){
                    streaklist.add(aux);
                }
                
            }
        });

        return streaklist;
    }

    // constructs a hashmap of cells based on the diagonals whom they belong to
    public HashMap<Integer,ArrayList<Cell>> diagonalCells(Cell[][] board,
        int direction){

        HashMap<Integer,ArrayList<Cell>> diagonalMap = new HashMap<>();
        
        if (direction == 1){ // main diagonal - subtraction of the indexes

            for (int i = 0; i < HEIGHT; i++){

                for (int j = 0; j < WIDTH; j++){

                    if (diagonalMap.get((i - j + 1)) == null){
                        diagonalMap.put((i - j + 1), new ArrayList<>());
                    }
                    diagonalMap.get((i - j + 1)).add(board[i][j]);
                }
            }
        }
        else { // secondary diagonal - sum of the indexes

            for (int i = 0; i < HEIGHT; i++){

                for (int j = 0; j < WIDTH; j++){

                    if (diagonalMap.get((i + j)) == null){
                        diagonalMap.put((i + j), new ArrayList<>());
                    }
                    diagonalMap.get((i + j)).add(board[i][j]);
                }
            }
        }

        return diagonalMap;
    }

    // lowest available slot in the specified coloumn
    public int lowestRow(int Col,Cell[][] board){
        
        for(int i = HEIGHT - 1 ; i >= 0 ; i--){

            if(board[i][Col].state == Cell.EMPTY){

                return i;
            }
        }
        return -1 ; // all cells occupied
    }
    
    // inserts the right token in the lowest possible cell in the right column
    public boolean insertToken(int col,Cell[][] board,boolean test){

        int row = lowestRow(col, board); // what is the lowest row available on this specific coloumn
        if (row != -1){

            // determine the token's Color 
            if (Turn){
                board[row][col].state = Cell.PLAYER1;
            } 
            else board[row][col].state = Cell.PLAYER2;
                
            if ((winner(board, row, col)) && (click)){
                
                click = false;
                for (int i = 0; i < 4; i++){
                    
                    if (WinningStreak.get(i).size() >= STREAK){
    
                        for (Cell c : WinningStreak.get(i)) c.setBackground(Color.green);
                    }
                }

                gameOver = true;
            }
            
            board[row][col].repaint();
            
            //only if its not a part of simulation
            if (!test){
                Turn = !Turn;
            } 

            return true;
        }

        return false;
    }
    
    // checks if there is a winning sequence
    public boolean winner(Cell[][] board,int row,int col) {
        
        int LastToken = 0, cnt[] = new int[4];
        boolean simple = false,diagonal = false;
        if(Turn){LastToken = Cell.PLAYER1;}
        else {LastToken = Cell.PLAYER2;}

        WinningStreak = new ArrayList<>();
        
        for (int i = 0; i < 4; i ++){
            
            WinningStreak.add(new ArrayList<>());
        }

        diagonal = diagonalStreak(board, row, col, LastToken, cnt);
        simple = simpleStreak(board, row, col, LastToken, cnt);

        return (simple || diagonal);
    }
    
    // auxliary method. checks rows and columns for STREAKs
    public boolean simpleStreak(Cell[][] board,
        int row, int col,int LastToken,int [] cnt){

        boolean flag = false; 

        for (int i = 0 ; i < HEIGHT ; i++){

            if (board[i][col].state == LastToken){

                cnt[0]++;
                WinningStreak.get(0).add(board[i][col]);
            }
            else if (cnt[0] < STREAK){

                cnt[0] = 0;
                WinningStreak.get(0).clear();
            }

            if (cnt[0] == STREAK) flag = true;
        }

        for (int i = 0; i < WIDTH; i++){
            
            if (board[row][i].state == LastToken){

                cnt[1]++;
                WinningStreak.get(1).add(board[row][i]);
            }
            else if (cnt[1] < STREAK){

                cnt[1] = 0;
                WinningStreak.get(1).clear();
            }

            if (cnt[1] == STREAK) flag = true;
        }

        return flag;
    }

    // auxliary method. checks diagonals for STREAKs
    public boolean diagonalStreak(Cell[][] board,
        int row, int col,int LastToken,int [] cnt){

        boolean flag = false;
        for (int i = 0; i < HEIGHT; i++){
            
            for (int j = 0; j < WIDTH; j++){

                if (i + j == row + col){

                    if (board[i][j].state == LastToken){

                        cnt[2]++;
                        WinningStreak.get(2).add(board[i][j]);
                    }
                    else if (cnt[2] < STREAK){
        
                        cnt[2] = 0;
                        WinningStreak.get(2).clear();
                    }
                }
    
                if (i - j == row - col){

                    if (board[i][j].state == LastToken){

                        cnt[3]++;
                        WinningStreak.get(3).add(board[i][j]);
                    }
                    else if (cnt[3] < STREAK){
        
                        cnt[3] = 0;
                        WinningStreak.get(3).clear();
                    }
                }
    
                if (cnt[2] == STREAK || cnt[3] == STREAK) flag = true;
            }
        }
        return flag;
    }

    // main paintComponent
    public synchronized void paintComponent(Graphics g){
        
        super.paintComponent(g);
        drawBoard(g, board);
	}
    
    // draws the state of the board
    public void drawBoard(Graphics g,Cell[][] board){

        for(int i = 0 ; i < HEIGHT ; i++)
        {
            for(int j = 0 ; j < WIDTH ; j++)
            {
                board[i][j].drawToken(g, board[i][j].state,10,10);
            }
        }    
    }

}