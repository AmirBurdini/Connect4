import java.util.HashMap;
import java.util.Random;

public class AI 
{
    static int value;
    static int rivalValue;

    Board gameBoard;
    int moves;
    int CurrMove;

    public AI(int moves, Board gameboard, int value){
        
        this.gameBoard = gameboard;
        this.moves = moves;
        AI.value = value;
        if (value == Cell.PLAYER1) rivalValue = Cell.PLAYER2;
        else rivalValue = Cell.PLAYER1;
        CurrMove = value;
    }

    // determines next move based on potential boards
    public int roboMax(int moves, Cell[][] board, boolean turn){

        if (moves == 0){
            
            return BoardeVal(board);
        } 

        int[] score = new int[Board.WIDTH];
        Board temp = new Board(null);

        for (int i = 0; i < Board.WIDTH; i++){

            CopyBoard(board, temp.board, temp);
            temp.Turn = turn;

            if(!temp.insertToken(i, temp.board, true)){
                score[i] = Integer.MIN_VALUE;
            }

            else score[i] = roboMax(moves - 1, temp.board,turn);
        }

        // HashMap<Integer,Integer> res = sortPlays(score);

        // return res.get(findMove());

        if (moves == this.moves){

            System.out.println();
            for (int i = 0; i < score.length; i++){
                System.out.print(i + " : " + score[i] + "||");
            }
        }

        return choose(score);

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
    public int choose(int[] arr){

        int max = 0, index = 0;
        for (int i = 0 ; i < arr.length; i++){

            if (arr[i] > max)
            {
                index = i;
                max = arr[i];
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

    // Board evalution function
    public int BoardeVal(Cell[][] board){
        
        int scoreSum = 0, rivalScore = 0;

        for (int i = 0; i < Board.HEIGHT; i++){

            for (int j = 0; j < Board.WIDTH; j++){

                if (board[i][j].state == value){
                    
                    scoreSum += Sequence(board,i,j);
                }

                if (board[i][j].state == rivalValue){
                    
                    rivalScore += board[i][j].score + Sequence(board,i,j);
                }

            }
        }

        return scoreSum - rivalScore;
    }
    
    // calculates how many sequences are in the board
    public int Sequence(Cell[][] board,int row,int col){

        int cnt[] = new int[Board.STREAK]; // streak counter
        int rival[] = new int[Board.STREAK]; // rival streak counter 

        int simple = simpleSequence(board, row, col, cnt, rival, rivalValue);
        int diagonal = diagonalSequence(board, row, col, cnt, rival, rivalValue);
        
        return (simple + diagonal);
    }

    // calculates score based on rows and coloumn sequences
    public int simpleSequence(Cell[][] board, int row, int col, int[] cnt,
     int[] rival, int value){

        int score = 0, rivalScore = 0; // score and rival score

        for (int i = 0; i < Board.WIDTH; i++){

            if (board[row][i].state == value){
                
                cnt[0]++;
            }
            else {
                if (cnt[0] < Board.STREAK) cnt[cnt[0]]++;
                else return 1000;
                cnt[0] = 0;
            }

            if (board[row][i].state == rivalValue){
                
                rival[0]++;
            }
            else {
                if (rival[0] < Board.STREAK) rival[rival[0]]++;
                else return -1000;
                rival[0] = 0;
            }
        }

        cnt[0] = 0; rival[0] = 0;

        for (int i = 0; i < Board.HEIGHT; i++){
            
            if (board[i][col].state == value){
                
                cnt[0]++;
            }
            else {
                if (cnt[0] < Board.STREAK) cnt[cnt[0]]++;
                else return 1000;
                cnt[0] = 0;
            }

            if (board[i][col].state == rivalValue){
                
                rival[0]++;
            }
            else {
                if (rival[0] < Board.STREAK) rival[rival[0]]++;
                else return -1000;
                rival[0] = 0;
            }
        }

        for (int i = 1; i < Board.STREAK; i++){

            score += i * cnt[i];
            rivalScore += i * rival[i];
        }
        
        return score - rivalScore;

    }

    // calculates score based on diagonal sequences
    public int diagonalSequence(Cell[][] board, int row, int col, int[] cnt,
     int[] rival, int value){

        int score = 0, aux = 0,rivalScore = 0;

        for (int i = 0; i < Board.HEIGHT; i++){
            
            for (int j = 0 ; j < Board.WIDTH ; j++){
                
                if (i + j == row + col){
                    
                    if (board[i][j].state == value) cnt[0]++;
                    else {
                        if (cnt[0] < Board.STREAK) cnt[cnt[0]]++;
                        else return Integer.MAX_VALUE;
                        cnt[0] = 0;
                    }

                    if (board[i][j].state == rivalValue) rival[0]++;
                    else {
                        if (rival[0] < Board.STREAK) rival[rival[0]]++;
                        else return Integer.MIN_VALUE;
                        rival[0] = 0;
                    }
                }

                if (i - j == row - col){
                    
                    if (board[i][j].state == value) aux++;
                    else {
                        if (aux < Board.STREAK) cnt[aux]++;
                        else return Integer.MAX_VALUE;
                        aux = 0;
                    }

                    if (board[i][j].state == rivalValue) rival[0]++;
                    else {
                        if (rival[0] < Board.STREAK) rival[rival[0]]++;
                        else return Integer.MIN_VALUE;
                        rival[0] = 0;
                    }
                }
            }
        }
    
        for (int i = 1; i < Board.STREAK; i++){

            score = i * cnt[i];
            rivalScore = i * rival[i];
        }
        
        return score - rivalScore;
    }

    // calculates the score by token positions
    public static int scorebyToken(Cell[][] board){

        int roboScore = 0, rivalScore = 0;

        for (int i = 0; i < Board.HEIGHT; i++){
            
            for (int j = 0; j < Board.WIDTH; j++){

                if (board[i][j].state == Cell.PLAYER1){
                    rivalScore += board[i][j].score;
                }

                
                if (board[i][j].state == Cell.PLAYER2){
                    roboScore += board[i][j].score;
                }
            }
        }

        return roboScore - rivalScore;
    }
}