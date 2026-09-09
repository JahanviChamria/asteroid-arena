public class SmallAvoid extends Avoid {

    private int velocityX;
    private int velocityY;

    public static final String SMAVOID_IMAGE_FILE = "assets/small_avoid.gif";

    public SmallAvoid(int x, int y, int velocityX, int velocityY, int size) {
        super(x, y, size, size, AVOID_IMAGE_FILE);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void scroll() {
        //update the position based on the velocity
        setX(getX() + velocityX);
        setY(getY() + velocityY);
    }
}
