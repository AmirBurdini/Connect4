

public class Fall implements Runnable {

    static final int velocity = 200;

    int col,height,radius;
    boolean isAlive;
    Board parent;
    
    public Fall(Board parent){

        this.parent = parent;
        col = parent.CurrCol;
        radius = Board.RADIUS;
        isAlive = true;
        
        //run();
    }

    public synchronized void run() {
        
        while(isAlive){

           
        }
    }
    
}