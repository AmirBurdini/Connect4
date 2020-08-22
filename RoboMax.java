import java.util.ArrayList;
import java.util.HashMap;

public class RoboMax {

    ArrayList<ArrayList<Cell>> StreakList;
    
    Board gameBoard; // parent board of this AI
    int moves; // depth of tree research
    int[] res; // result arr containing

    

    // constructor
    public RoboMax(int moves, Board gameboard) {
        
        this.gameBoard = gameboard;
        this.moves = moves;
        StreakList = new ArrayList<ArrayList<Cell>>();
    }

    // recursive method to determine next move
    public int nextMove(int moves, Cell[][] board, boolean RoboTurn) {

        // stop condition
        if (moves == 0) {
            
            // printBoard(board);
            //return evaluation(board, RoboTurn);
            return alter(board);
        }
        
        // moves score map
        // key - column number, value - score of that move
        HashMap<Integer,Integer> pMoves = new HashMap<>();
        
        // create a new board
        Cell[][] boardCopy = new Cell[Board.HEIGHT][Board.WIDTH];

        // rate each possible next move
        for (int i = 0; i < Board.WIDTH; i++) {

            if (Board.lowestRow(i, board) == - 1) {
                pMoves.put(i, null);
            }
            else {
                CopyBoard(board, boardCopy, gameBoard);
                insertToken(boardCopy, i, !RoboTurn);

                if (winner(board, RoboTurn)) {
                    
                    if (RoboTurn) pMoves.put(i,Integer.MAX_VALUE);
                    else pMoves.put(i,Integer.MIN_VALUE);
                }
                else pMoves.put(i, nextMove(moves - 1, boardCopy, !RoboTurn));
            } 
        }

        if (moves == this.moves) {
            return choose(pMoves, RoboTurn, true);
        }
        return choose(pMoves, RoboTurn, false);

    } 

    // prints all streaks
    public void printStreak(ArrayList<ArrayList<Cell>> streakList) {

        for (ArrayList<Cell> streak : streakList) {

            System.out.println("---------------");
            for (Cell c : streak) {

                System.out.println("(" + c.x + "," + c.y + ")" + " == " + c.cellState);
            }
        }
    }

    // eval board
    public int evaluation(Cell[][] board, boolean turn) {

        Cell.state token;
        int score;

        if (turn) {

            token = Cell.state.PLAYER2;    
        }
        else token = Cell.state.PLAYER1;

        StreakList = streakList(board, token);

        // the final result
        int res = 0;
        
        HashMap <Integer, ArrayList<Integer>> scoreStreaks 
            = new HashMap <Integer, ArrayList<Integer>>();

        scoreStreaks = numOfStreaks(token, StreakList);

        // if there is a streak return max value
        if (scoreStreaks.get(Board.STREAK - 1).size() > 0) {
            return Integer.MAX_VALUE;
        }

        for (int i = 0; i < scoreStreaks.size(); i++) {
            
            score = 0;
            for (int j = 0; j < scoreStreaks.get(i).size(); j++) {

                score += scoreStreaks.get(i).get(j);
            }
            res += score * Math.pow(10, i);
        }

        return res;
    }

    // alternative method to assess board value
    public int alter(Cell[][] curr) {

        ArrayList<ArrayList<Cell>> pList = streakList(curr, Cell.state.PLAYER1);
        ArrayList<ArrayList<Cell>> rList = streakList(curr, Cell.state.PLAYER2);

        killStreak(pList, Cell.state.PLAYER2);
        killStreak(rList, Cell.state.PLAYER1);

        HashMap <Integer, ArrayList<Integer>> playerScore =
         numOfStreaks(Cell.state.PLAYER1, pList);

        HashMap <Integer, ArrayList<Integer>> roboScore =
         numOfStreaks(Cell.state.PLAYER2, rList);

        // roboMax win = max value
        if (!roboScore.get(Board.STREAK).isEmpty()) {
            return Integer.MAX_VALUE;
        }
        
        // player win = min value
        if (!playerScore.get(Board.STREAK).isEmpty()) {
            return Integer.MIN_VALUE;
        }

        res = new int[Board.STREAK + 1];
        int Score = 0;

        roboScore.forEach((k, v) -> {
            v.forEach((p)-> res[k] = p.intValue());
        });  

        for (int i = 1 ; i < res.length; i++) {

            Score += res[i] * Math.pow(2, roboScore.get(i).size()/i);
        }

        res = new int[Board.STREAK + 1];

        playerScore.forEach((k, v) -> {
            v.forEach((p)-> res[k] = p.intValue());
        });

        for (int i = 1 ; i < res.length; i++) {

            Score -= res[i] * Math.pow(2, playerScore.get(i).size()/i);
        }

        return Score;
    }

    // creates a list of all streaks possible in the board
    public ArrayList<ArrayList<Cell>> streakList(Cell[][] board,Cell.state fill) {

        ArrayList<ArrayList<Cell>> res = new ArrayList<>();

        // add all diagonal streaks
        ArrayList<ArrayList<Cell>> aux = new ArrayList<>();
        HashMap<Integer,ArrayList<Cell>> diagonal = new HashMap<>();
        
        diagonal = gameBoard.diagonalCells(board, 0);
        diagonal.forEach((k,v) -> aux.add(v));

        diagonal = gameBoard.diagonalCells(board, 1);
        diagonal.forEach((k,v) -> aux.add(v));
        
        res = diagonalStreaks(aux);
        
        // add all row streaks
        for (int i = 0; i < Board.HEIGHT; i++) {
            rowStreak(board, i, res);
        }

        // add all col streaks
        for (int i = 0; i < Board.WIDTH; i++) {
            colStreak(board, i, res);
        }

        return res;
    }

    // adds all row streaks
    public void rowStreak(Cell[][] board, int row,
     ArrayList<ArrayList<Cell>> streakList ) {

        ArrayList<Cell> streak;

        for (int i = 0; i < Board.WIDTH - Board.STREAK + 1; i++) {

            streak = new ArrayList<>();
            streak.clear();
            for (int j = 0; j < Board.STREAK; j++) {
                
                streak.add(board[row][i + j]);
            }
            streakList.add(streak);
        }
    }

    // adds all col streaks
    public void colStreak(Cell[][] board, int col, 
     ArrayList<ArrayList<Cell>> streakList) {

        ArrayList<Cell> streak;

        for (int i = 0; i < Board.HEIGHT - Board.STREAK + 1; i++) {

            streak = new ArrayList<>();
            streak.clear();
            for (int j = 0; j < Board.STREAK; j++) {
                
                streak.add(board[i + j][col]);
            }
            streakList.add(streak);
        }
    }

    // counts all streaks
    public HashMap <Integer, ArrayList<Integer>> numOfStreaks(Cell.state x,
     ArrayList<ArrayList<Cell>> streakList) {

        HashMap <Integer, ArrayList<Integer>> cnt = 
            new HashMap<Integer, ArrayList<Integer>>();

        for (int i = 1; i <= Board.STREAK; i++) {
            cnt.put(i, new ArrayList<Integer>());
        }

        int aux;
        int score;
        for (ArrayList<Cell> streak : streakList) {

            aux = 0;
            score = 0;
            for (int i = 0; i < streak.size(); i++) {

                if (streak.get(i).cellState == x) {
                    aux++;
                    score += streak.get(i).score;
                }
                else {

                    aux = 0;
                    score = 0;
                } 
            }

            if (aux > 0) {
                cnt.get(aux).add(score);
            }
            
        }

        return cnt;
    }

    // auxilary method, copies board to board
    public void CopyBoard(Cell[][] board,Cell[][] aux,Board gameboard) {
        
        for (int i = 0; i < Board.HEIGHT; i++){

            for (int j = 0; j < Board.WIDTH; j++){
            
                aux[i][j] = new Cell(i,j,gameboard);
                aux[i][j].cellState = board[i][j].cellState;
                aux[i][j].score = board[i][j].score;
            }
        }
    }

    // returns the Value/index of min/max value of arr
    public int choose(HashMap<Integer,Integer> pMoves, boolean turn
    , boolean isFinal) {

        int minimax , index = 0;

        if (turn) minimax = Integer.MIN_VALUE;
        else minimax = Integer.MAX_VALUE;

        for (int i = 0 ; i < pMoves.size(); i++) {

            // if its RoboMax's turn choose maximal score
            if (turn) {

                if (pMoves.get(i) != null) {
                
                    if (pMoves.get(i) >= minimax) {

                        index = i;
                        minimax = pMoves.get(i);
                    }
                    
                }
            }
            // if its Players's turn choose minimal score
            else if (pMoves.get(i) != null) {
                
                    if (pMoves.get(i) <= minimax) {

                        index = i;
                        minimax = pMoves.get(i);
                    }
            }
        }

        if (isFinal) {
            return index;
        }

        return minimax;
    }

    // turns all diagonals to diagonal streaks
    public ArrayList<ArrayList<Cell>> diagonalStreaks
    (ArrayList<ArrayList<Cell>> streakList) {

        ArrayList<Cell> aux;
        ArrayList<ArrayList<Cell>> res = new ArrayList<>();

        for (ArrayList<Cell> streak : streakList) {

            for (int i = 0; i < streak.size() - Board.STREAK + 1; i++) {

                if (streak.size() >= Board.STREAK) {
                    
                    aux = new ArrayList<>();
                    aux.clear();
                    for (int j = 0; j < Board.STREAK; j++) {
                    
                        aux.add(streak.get(i + j));
                    }
                    res.add(aux);
                }
            }
        }

        return res;
    }

    // dismiss all streaks blocked by rival
    public void killStreak(ArrayList<ArrayList<Cell>> streakList
    ,Cell.state fill) {

        boolean flag;
        for (int i = 0; i < streakList.size(); i++) {

            flag = false;
            for (Cell c : streakList.get(i)) {

                if (c.cellState.equals(fill)) {

                    flag = true;
                }
            }

            if (flag) {
                streakList.remove(streakList.get(i));
            }
        }
    }

    // inserts a token to the board 
    public void insertToken(Cell[][] board, int col, boolean turn) {

        int row = Board.lowestRow(col, board);

        if (row != -1) {

            if (turn) {
                board[row][col].cellState = Cell.state.PLAYER1;
            }
            else board[row][col].cellState = Cell.state.PLAYER2;
        }
    }

    // prints board
    public void printBoard(Cell[][] board) {

        for (int i = 0; i < Board.HEIGHT; i++) {
            
            System.out.println();
            for (int j = 0; j < Board.WIDTH; j++) {
            
                if (board[i][j].cellState == Cell.state.PLAYER1) {
                    System.out.print("[1]");
                }

                if (board[i][j].cellState == Cell.state.PLAYER2) {
                    System.out.print("[2]");
                }

                if (board[i][j].cellState == Cell.state.EMPTY) {
                    System.out.print("[0]");
                }
                
            }
        }

        System.out.println();
    }

    // checks if there is a winning sequence
    public boolean winner(Cell[][] board, boolean turn) {
        
        Cell.state fill;
        int cnt;

        if (turn) {
            fill = Cell.state.PLAYER2;
        } else fill = Cell.state.PLAYER1;

        ArrayList<ArrayList<Cell>> streakList = streakList(board, fill);

        for (ArrayList<Cell> streak : streakList) {

            cnt = 0;
            for (Cell c : streak) {

                if (c.cellState.equals(fill)) cnt++;
                else cnt = 0;
            }

            if (cnt == Board.STREAK) return true;
        }

        return false;
    }

}