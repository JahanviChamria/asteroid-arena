//A RareAvoid is a rare kind of Avoid that spawns more infrequently than the regular Avoid
//When collided with, RareAvoids lower the Player's HP by the same amount as a standard Avoid,
//but also decrease the Player's score.  Otherwise, behaves the same as a regular Avoid
public class RareAvoid extends Avoid{
    
    //Location of image file to be drawn for a RareAvoid
    public static final String RAVOID_IMAGE_FILE = "assets/rare_avoid.gif";

    //Dimensions of the Avoid  
    public static final int RAVOID_WIDTH = 120;
    public static final int RAVOID_HEIGHT = 90;
    
    //The amount of points lost when the player collides with a RareAvoid
    protected static final int RAVOID_POINTS_PENALTY = -40;

    public RareAvoid(){
        this(0, 0);        
    }
    
    public RareAvoid(int x, int y){
        this(x, y, RAVOID_IMAGE_FILE);  
    }

    public RareAvoid(int x, int y, String imageName){
        this(x, y, RAVOID_WIDTH, RAVOID_HEIGHT, imageName);  
    }  
    
    public RareAvoid(int x, int y, int width, int height, String imageName){
        super(x, y, width, height, imageName);
    }
    

    public int getScoreModifier(){
        return RAVOID_POINTS_PENALTY;
    }
   

    
}
