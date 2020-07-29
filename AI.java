import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class AI implements Runnable
{
    static int value;
    static int rivalValue;

    Board gameBoard;
    int moves;
    int CurrMove;
    ArrayList<ArrayList<Cell>> streakList;

    public AI(int moves, Board gameboard, int value){
        
        this.gameBoard = gameboard;
        this.moves = moves;
        AI.value = value;
        if (value == Cell.PLAYER1) rivalValue = Cell.PLAYER2;
        else rivalValue = Cell.PLAYER1;
        CurrMove = value;
        streakList = streakList(gameboard.board, value);
    }

    // determines next move based on potential boards
    public int roboMax(int moves, Cell[][] board, boolean turn){

        if (moves == 0){
            
            return BoardeVal(board);
        } 

        // a score array,each cell contains the score of a potential move
        int[] score = new int[Board.WIDTH]; 
        Board temp = new Board(null);

        for (int i = 0; i < Board.WIDTH; i++){

            CopyBoard(board, temp.board, temp);
            temp.Turn = turn;

            if(!temp.insertToken(i, temp.board, true)){
                score[i] = Integer.MIN_VALUE;
            }

            else score[i] = roboMax(moves - 1, temp.board,!turn);
        }

        // HashMap<Integer,Integer> res = sortPlays(score);

        // return res.get(findMove());

        if (moves == this.moves){

            System.out.println();
            for (int i = 0; i < score.length; i++){
                System.out.print(i + " : " + score[i] + "||");
            }
        }

        return choose(score,turn);

    }  

    // auxilary method, copies board to board
    public void CopyBoard(Cell[][] board,Cell[][] aux,Board gameboard){
        
        for (int i = 0; i < Board.HEIGHT; i++){

            for (int j = 0; j < Board.WIDTH; j++){
            
                aux[i][j] = new Cell(i,j,gameboard);
                aux[i][j].state = board[i][j].state;
                aux[i][j].score = board[i][j].score;
            }
        }
    }

    // returns the max value of arr
    public int choose(int[] arr,boolean turn){

        int minimax = 0, index = 0;
        for (int i = 0 ; i < arr.length; i++){

            if(turn){
                if (arr[i] > minimax){
                
                    index = i;
                    minimax = arr[i];
                }
            }
            else if (arr[i] < minimax){
                
                index = i;
                minimax = arr[i];
            }
            
        }
        
        return index;
    }

    // choose randomly one of the three best moves
    public int findMove(){

        Random random = new Random();
        int x = random.nextInt(3);

        return x;
    }

    // returns a sorted map of the plays by potential score
    public HashMap<Integer,Integer> sortPlays(int[] arr){
        
        int max;
        HashMap<Integer, Integer> sortedPlays = new HashMap<>();
        
        // simple selection sort
        for (int i = 0 ; i < arr.length; i++){
            
            max = arr[i];
            for (int j = i + 1; j < arr.length -1; j++){

                if (arr[j] > max){
                    max = arr[j];
                }
            }
            sortedPlays.put(i, max);
        }

        return sortedPlays;
    }

    // creats an ArrayList of streaks
    public ArrayList<ArrayList<Cell>> streakList(Cell[][] board,int value){

        ArrayList<ArrayList<Cell>> streakList = new ArrayList<ArrayList<Cell>>();
        ArrayList<ArrayList<Cell>> mainDiagonal = gameBoard.
        streakList(gameBoard.diagonalCells(board, 1));

        ArrayList<ArrayList<Cell>> secDiagonal = gameBoard.
        streakList(gameBoard.diagonalCells(board, 0));

        ArrayList<Cell> rowStreak = new ArrayList<>();
        ArrayList<Cell> colStreak = new ArrayList<>();

        for (int i = 0; i < Board.HEIGHT; i++){

            for (int j = 0; j < Board.WIDTH; j++){

                rowStreak.clear();
                colStreak.clear();
                for (int k = 0; k < Board.STREAK; k++){

                    if(i + k < Board.HEIGHT){
                        if (board[i + k][j].state == value){
                            rowStreak.add(board[i + k][j]);
                        }
                        else rowStreak.clear();
                    }
                    
                    if(j +k < Board.WIDTH){
                        if (board[i][j + k].state == value){
                            colStreak.add(board[i][j + k]);
                        }
                        else colStreak.clear();
                    }
                }

                if (!rowStreak.isEmpty()){
                    streakList.add(rowStreak);
                }

                if (!colStreak.isEmpty()){
                    streakList.add(colStreak);
                }
            }
        }

        for (ArrayList<Cell> streak : mainDiagonal){
            streakList.add(streak);
        }

        for (ArrayList<Cell> streak : secDiagonal){
            streakList.add(streak);
        }

        return streakList;
    }

    // alternate scoring method
    public int alter(Cell[][] board,ArrayList<ArrayList<Cell>> streakList,int value){

        int score = 0;
        int aux;
        for (ArrayList<Cell> streak : streakList){

            aux = 0;
            for(Cell c : streak)
            {
                if (c.state == value){

                    aux++;
                }
            }
            score += Math.pow(10, aux);
        }

        return score;
    }

    // Board evalution function
    public int BoardeVal(Cell[][] board){
        
        int scoreSum = 0, rivalScore = 0;

        for (int i = 0; i < Board.HEIGHT; i++){

            for (int j = 0; j < Board.WIDTH; j++){

                if (board[i][j].state == value){
                    
                    scoreSum += Math.pow(board[i][j].score, Sequence(board,i,j));
                }

                if (board[i][j].state == rivalValue){
                    
                    rivalScore += Math.pow(board[i][j].score, Sequence(board,i,j));
                }

            }
        }

        return scoreSum - rivalScore;
    }
    
    // calculates how many sequences are in the board
    public int Sequence(Cell[][] board,int row,int col){

        int cnt[] = new int[Board.STREAK]; // streak counter
        int rival[] = new int[Board.STREAK]; // rival streak counter 

        int simple = simpleSequence(board, row, col, cnt, value);
        int diagonal = diagonalSequence(board, row, col, cnt, value);

        int rivalSimple = simpleSequence(board, row, col, rival, rivalValue);
        int rivalDiagonal = diagonalSequence(board, row, col, rival, rivalValue);
        
        return (simple + diagonal) - (rivalSimple + rivalDiagonal);
    }

    // calculates score based on rows and coloumn sequences
    public int simpleSequence(Cell[][] board, int row, int col,int[] cnt,
    int value){

        int score = 0; // score

        for (int i = 0; i < Board.WIDTH; i++){

            if (board[row][i].state == value){
                
                cnt[0]++;
            }
            else {
                if (cnt[0] < Board.STREAK) cnt[cnt[0]]++;
                else return 1000;
                cnt[0] = 0;
            }
        }

        cnt[0] = 0;

        for (int i = 0; i < Board.HEIGHT; i++){
            
            if (board[i][col].state == value){
                
                cnt[0]++;
            }
            else {
                if (cnt[0] < Board.STREAK) cnt[cnt[0]]++;
                else return 1000;
                cnt[0] = 0;
            }
        }

        for (int i = 1; i < Board.STREAK; i++){

            score += Math.pow(cnt[i], i + 1);
        }
        
        return score;

    }

    // calculates score based on diagonal sequences
    public int diagonalSequence(Cell[][] board, int row, int col, int[] cnt,
    int value){

        int score = 0, aux = 0;

        for (int i = 0; i < Board.HEIGHT; i++){
            
            for (int j = 0 ; j < Board.WIDTH ; j++){
                
                if (i + j == row + col){
                    
                    if (board[i][j].state == value) {

                        cnt[0]++;
                    }
                    else {
                        if (cnt[0] < Board.STREAK) cnt[cnt[0]]++;
                        else return 1000;
                        cnt[0] = 0;
                    }
                }

                if (i - j == row - col){
                    
                    if (board[i][j].state == value){
                        
                        aux++;
                    } 
                    else {
                        if (aux < Board.STREAK) cnt[aux]++;
                        else return 1000;
                        aux = 0;
                    }
                }
            }
        }
    
        for (int i = 1; i < Board.STREAK; i++){

            score += Math.pow(cnt[i], i + 1);
        }
        
        return score;
    }

    // run the AI
    public void run() {
       

    }

}