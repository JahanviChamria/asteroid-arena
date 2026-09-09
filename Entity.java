//An Entity represents some indivdual thing that is drawn to the game window.
//Everything drawn and animated in the game window is an Entity, including the player.
//
//Each time the game window "refreshes" all entities in the game's display list
//are drawn according to their respective attributes.
public abstract class Entity {
    
    //Location and name of image file to be drawn for the Entity
    //Can be either a relative or absolute path
    private String imageName;
    
    //The height and width of the entity (in pixels) to be drawn to the game window
    //Note that all Entities are ultimately drawn as rectangles in the game window.
    //An entity's image will be stretched to fit its rectangle per its height/width
    //This rectangle is also its "hitbox", governing its boundaries when determining collisions.
    private int height, width;
    //The x and y coordinate of the Entity to be drawn in the game window.
    protected int x, y;
    //Determines if the Entity is visible in the game window or not
    //Invisible Entities in the display list are not drawn to the game window
    private boolean isVisible;
    //Determines if the Entitiy is flagged to be garbage collected (true) or not (false)
    private boolean flaggedForGC;
    
    
    public Entity(int x, int y, int width, int height, String imageName){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.imageName = imageName;
        this.isVisible = true; //by default, all newly instantiated Entities are visible
        this.flaggedForGC = false;
    }
    
    //Set and retrieve this entity's coordinates
    public int getX(){
        return x;
    }
    
    public void setX(int newX){
        this.x = newX;
    }
    
    public int getY(){
        return y;
    }
    
    public void setY(int newY){
        this.y = newY;
    }
    
    
    //Set and retrieve this entity's dimensions
    public int getHeight(){
        return height;
    }
    
    public void setHeight(int newHeight){
        this.height = newHeight;
    }
    
    public int getWidth(){
        return width;
    }
    
    public void setWidth(int newWidth){
        this.width = newWidth;
    }   
    
    
    //Set and retrieve this entity's image and visibility status
    public String getImage(){
        return imageName;
    }
    
    public void setImage(String newImageName){
        this.imageName = newImageName;
    }   
    
    public void setVisible(boolean isVisible){
        this.isVisible = isVisible;
    }
    
    public boolean isVisible(){
        return isVisible;
    }
    

    //sets if this entity is flagged to be garbage collected (true) or not (false)
    //if flagged for garbage collection, the Entity will be removed from the DisplayList
    //when the game window is next refreshed
    public void setGCFlag(boolean flag){
        this.flaggedForGC = flag;
    }

    //returns a boolean indicating if this Entity is flagged to be garbage collected (true) or not (false)
    public boolean isFlaggedForGC(){
        return this.flaggedForGC;
    }

    
    //Checks to see if this Entity is colliding with the argument Entity
    //Entities are colliding if any parts of the two Entities are overlapping
    public boolean isColliding(Entity other){
        return (checkCollision(this, other) || checkCollision(other,this));    //checks collisions for both combinations of entities
    }
    
    public boolean checkCollision(Entity A, Entity B){
        int x=A.getX();
        int y=A.getY();
        int entityWidth=A.getWidth();
        int entityHeight=A.getHeight();
        int[] xCoords={x,x+entityWidth,x+entityWidth,x};
        int[] yCoords={y,y,y+entityHeight,y+entityHeight};
        for(int i=0;i<xCoords.length;i++){
            if(B.containsCoord(xCoords[i],yCoords[i]))
                return true;
        }
        return false;
    }
    
    //Checks to see if the argument x,y coordinate is inside 
    //this Entity
    public boolean containsCoord(int x, int y){
        if (this.x <= x && this.x + this.width >= x){
            return (this.y <= y && this.y + this.height >= y);
        }
        return false;
    }
    
}
