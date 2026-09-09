//A RareCollect is a rare kind of Collect that spawns more infrequently than the regular Collect
//When collided with, RareCollects increase the Player's score by the same amount as a standard Collect,
//but also increase the Player's HP.  Otherwise, behaves the same as a regular Collect.
public class RareCollect extends Collect{
    
    //Location of image file to be drawn for a RareCollect
    public static final String RCOLLECT_IMAGE_FILE = "assets/rare_collect.gif";

    //Dimensions of the Collect  
    public static final int RCOLLECT_WIDTH = 50;
    public static final int RCOLLECT_HEIGHT = 50;   

    //The amount of HP restored when the player collides with a RareAvoid
    protected static final int RCOLLECT_HP_RESTORED = 1;    


    public RareCollect(){
        this(0, 0);        
    }
    
    public RareCollect(int x, int y){
        this(x, y, RCOLLECT_IMAGE_FILE);  
    }

    public RareCollect(int x, int y, String imageName){
        this(x, y, RCOLLECT_WIDTH, RCOLLECT_HEIGHT, imageName);  
    }  
    
    public RareCollect(int x, int y, int width, int height, String imageName){
        super(x, y, width, height, imageName);
    }
    
    public int getHPModifier(){
        return 1;
    }
   
}
