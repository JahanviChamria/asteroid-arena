public class PowerUp extends Collect{

    //Location of image file to be drawn for a PowerUp
    public static final String POWERUP_IMAGE_FILE = "assets/powerup.gif";

    //Dimensions of the PowerUp
    public static final int POWERUP_WIDTH = 40;
    public static final int POWERUP_HEIGHT = 40;


    public PowerUp(){
        this(0, 0);
    }

    public PowerUp(int x, int y){
        this(x, y, POWERUP_IMAGE_FILE);
    }

    public PowerUp(int x, int y, String imageName){
        this(x, y, POWERUP_WIDTH, POWERUP_HEIGHT, imageName);
    }

    public PowerUp(int x, int y, int width, int height, String imageName){
        super(x, y, width, height, imageName);
    }

    public int getHPModifier(){
        return 0;
    }

    public int getScoreModifier(){
        return 10;
    }

}
