public class Shot extends Avoid{

    //Location of image file to be drawn for a Shot
    public static final String SHOT_IMAGE_FILE = "assets/shot.gif";

    //Dimensions
    public static final int SHOT_WIDTH = 20;
    public static final int SHOT_HEIGHT = 20;

    public static final int DEFAULT_SCROLL_SPEED = 5;

    protected int scrollSpeed = DEFAULT_SCROLL_SPEED;


    public Shot(){
        this(0, 0);
    }

    public Shot(int x, int y){
        this(x, y, SHOT_IMAGE_FILE);
    }

    public Shot(int x, int y, String imageName){
        this(x, y, SHOT_WIDTH, SHOT_HEIGHT, imageName);
    }

    public Shot(int x, int y, int width, int height, String imageName){
        super(x, y, width, height, imageName);
    }

    public int getHPModifier(){
        return 0;
    }

    public int getScoreModifier(){
        return 15;
    }

    public void scroll(){
        setX(getX() + this.scrollSpeed);
    }
}
