import java.awt.*;
import java.awt.event.*;
import java.util.*;

//A Starter version of the scrolling game, featuring Avoids, Collects, RareAvoids, and RareCollects
//Players must reach a score threshold to win.
//If player runs out of HP (via too many Avoid/RareAvoid collisions) they lose.
public class StarterGame extends GameEngine2D {
    
    
    //Starting Player coordinates
    protected static final int STARTING_PLAYER_X = 0;
    protected static final int STARTING_PLAYER_Y = 100;
    
    //Score needed to win the game
    protected static final int SCORE_TO_WIN = 300;
    
    //Maximum that the game speed can be increased to
    //(a percentage, ex: a value of 300 = 300% speed, or 3x regular speed)
    protected static final int MAX_GAME_SPEED = 300;
    //Interval that the speed changes when pressing speed up/down keys
    protected static final int SPEED_CHANGE_INTERVAL = 20;    
    
    public static final String INTRO_SPLASH_FILE = "assets/splash.gif";        
    //Key pressed to advance past the splash screen
    public static final int ADVANCE_SPLASH_KEY = KeyEvent.VK_ENTER;
    
    //Interval that Entities get spawned in the game window
    //ie: once every how many ticks does the game attempt to spawn new Entities
    protected static final int SPAWN_INTERVAL = 45;

    protected static final int COLLECT=1;
    protected static final int AVOID=2;
    protected static int MAX_ENTITIES_TO_SPAWN=3;
    protected static int RARE_ENTITY_ODDS=5;

    
    //A Random object for all your random number generation needs!
    public static final Random rand = new Random();
    
    //player's current score
    protected int score;
    
    
    //Stores a reference to game's Player object for quick reference (Though this Player presumably
    //is also in the DisplayList, but it will need to be referenced often)
    protected Player player;
    
    
    public StarterGame(){
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public StarterGame(int gameWidth, int gameHeight){
        super(gameWidth, gameHeight);
    }
    
    
    //Performs all of the initialization operations that need to be done before the game starts
    protected void pregame(){
        this.setBgColor(Color.BLACK);
        this.player = new Player(STARTING_PLAYER_X, STARTING_PLAYER_Y);
        this.entities.add(player); 
        this.score = 0;
        this.setSplashImage(INTRO_SPLASH_FILE);  //adding Splash image
    }
    
    //Called on each game tick
    protected void updateGame(){
        //scroll all scrollable Entities on the game board
        scrollEntities();   
        //Spawn new entities only at a certain interval
        if (super.getTicksElapsed() % SPAWN_INTERVAL == 0){
            spawnEntities();
        }
        checkForPlayerCollisions();
        updateTitleText();
        gcOffscreenEntities();
    }

    protected void checkForPlayerCollisions(){            //checking if the player collides with any Consumables for every tick
        for(int i=0;i<this.entities.size();i++){
            if(this.entities.get(i) instanceof Consumable){
                if(player.isColliding(this.entities.get(i))){
                    collidedWithPlayer((Consumable)this.entities.get(i));
                }
            }
        }
    }
    

    //Update the text at the top of the game window
    protected void updateTitleText(){
        setTitleText("HP: "+ player.getHP()+", Score: "+this.score);
    }
    

    //Scroll all scrollable entities per their respective scroll speeds
    protected void scrollEntities(){
        DisplayList currentEntities=this.entities;
        for(int i=0;i<currentEntities.size();i++){
            Entity e=currentEntities.get(i);
            if(e instanceof Scrollable){           //checking if an entity is scrollable
                ((Scrollable)e).scroll();
            }
        }

    }

    
    //Handles "garbage collection" of the entities
    //Flags entities in the displaylist that are no longer relevant
    //(i.e. will no longer need to be drawn in the game window).
    protected void gcOffscreenEntities(){
        DisplayList currentEntities=this.entities;
        for(int i=0;i<currentEntities.size();i++){
            Entity e=currentEntities.get(i);
            if(isOffScreen(e)){
                e.setGCFlag(true);
            }
        }
    }

    protected boolean isOffScreen(Entity E){            //returns true if an entity is offscreen
        if(E.getX()+E.getWidth()<0)
            return true;
        return false;
    }
    
    //Called whenever it has been determined that the Player collided with a consumable
    protected void collidedWithPlayer(Consumable collidedWith){
        ((Entity)collidedWith).setGCFlag(true);
        this.score+=collidedWith.getScoreModifier();
        player.modifyHP(collidedWith.getHPModifier());
    }
    
    //Spawn new Entities on the right edge of the game board
    protected void spawnEntities(){
        int randomNumEntitiesToBeSpawned=rand.nextInt(MAX_ENTITIES_TO_SPAWN)+1;     //max entities spawned for a particular tick
        int ct=0;
        int[] entityTypes={COLLECT, AVOID};
        while(ct<randomNumEntitiesToBeSpawned){
            Entity toBeSpawned=this.addEntity(chooseRandomEntity(entityTypes));
            if(this.getAllCollisions(toBeSpawned).size()==0)                         //prevents adding overlapping entities to the display list
                entities.add(toBeSpawned);
            ct++;
        }
    }

    protected Entity chooseRandomEntity(int[] entityTypes){                            //returns a random entity to be spawned
        int randomType=rand.nextInt(entityTypes.length)+1;
        int randomRareType=rand.nextInt(RARE_ENTITY_ODDS);                           //odds of a rare entity
        if(randomType==COLLECT){
            if(randomRareType==1){
                return new RareCollect();
            }
            else{
                return new Collect();
            }
        }
        else{
            if(randomRareType==1){
                return new RareAvoid();
            }
            else{
                return new Avoid();
            }
        }
    }

    protected Entity addEntity(Entity E){                    //returns a new entity on a random Y position
        int entitySize=E.getHeight();
        E.setX(this.getWindowWidth());
        E.setY(this.getRandomYPos(entitySize));
        return E;
    }

    protected int getRandomYPos(int height){                 //returns a random Y coordinate
        int yCoord=rand.nextInt(this.getWindowHeight()-height);
        return yCoord;
    }
    
    //Called once the game is over, performs any end-of-game operations
    protected void postgame(){
        if(this.score>=SCORE_TO_WIN){
            super.setTitleText("GAME OVER - You won!");
        }
        else if(player.getHP()<=0){
            super.setTitleText("GAME OVER! - You lose!");
        }
    }
    
    //Returns a boolean indicating if the game is over (true) or not (false)
    //Game can be over due to either a win or lose state
    protected boolean isGameOver(){
        if(this.score>=SCORE_TO_WIN || player.getHP()<=0){
            return true;
        }
        return false;
    }
    
    //Reacts to a single key press on the keyboard
    protected void keyReact(int key){
        //if a splash screen is up, only react to the advance splash key
        if (getSplashImage() != null){
            if (key == ADVANCE_SPLASH_KEY)
                super.setSplashImage(null);
            return;
        }
        else{
            if(search(key, MOVEMENT_KEYS)==true){
                if(!this.isPaused)
                arrowKeyPressed(key);
            }
            else if(key==SPEED_UP_KEY){
                if(this.getGameSpeed()<MAX_GAME_SPEED){
                    this.setGameSpeed(this.getGameSpeed()+SPEED_CHANGE_INTERVAL);
                }
            }
            else if(key==SPEED_DOWN_KEY){
                if(this.getGameSpeed()>0){
                    this.setGameSpeed(this.getGameSpeed()-SPEED_CHANGE_INTERVAL);
                }
            }
            else if(key==KEY_PAUSE_GAME){
                this.isPaused=!this.isPaused;
            }
        }
    }    
    
    protected void arrowKeyPressed(int key){        //to move the player according to the player's speed
        int playerXCoord=player.getX();
        int playerYCoord=player.getY();
        int windowWidth=this.getWindowWidth();
        int windowHeight=this.getWindowHeight();
        int pSpeed=player.getMoveSpeed();

        if(key==UP_KEY && playerYCoord>=0){          //to ensure that the player remains within the frame dimensions
            player.setY(playerYCoord-pSpeed);
        }
        else if(key==DOWN_KEY && playerYCoord<=(windowHeight-player.PLAYER_HEIGHT)){
            player.setY(playerYCoord+pSpeed);
        }
        else if(key==LEFT_KEY && playerXCoord>=0){
            player.setX(playerXCoord-pSpeed);
        }
        else if(key==RIGHT_KEY && playerXCoord<=(windowWidth-player.PLAYER_WIDTH)){
            player.setX(playerXCoord+pSpeed);
        }
    }
    
    protected static boolean search(int value, int[] arr){     //returns the index of a value in an array
        for(int i:arr){
            if(i==value){
                return true;
            }
        }
        return false;
    }
    
    //Handles reacting to a single mouse click in the game window
    protected MouseEvent reactToMouseClick(MouseEvent click){
       
        //Mouse functionality is not used at all in the Starter game...
        //you may want to override this function for a CreativeGame feature though!

        return click;//returns the mouse event for any child classes overriding this method
    }
    
    
    
}
