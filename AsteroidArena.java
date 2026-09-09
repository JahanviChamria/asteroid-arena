import java.awt.event.*;

public class AsteroidArena extends StarterGame{

/**
 * A scrolling game featuring different types of entities such as Collects, Avoids, RareAvoids, and RareCollects.
 * Players must achieve a certain score to advance through levels and must avoid collisions that deplete HP.
 */

    protected static final int SCORE_FOR_NEXTLEVEL = 300;
    protected static final int LEVELS = 3;
    protected static final int MAX_FUEL = 100;
    protected static final int MAX_SPACESHIP_HP = 100;
    protected static final double FUEL_DEC = 0.05;
    protected static final int PLAYER_DEC = 25;
    protected static final int ENTITY_DEC = 25;
    protected static final int COLLECT_SPEED_INC = 2;
    protected static final int AVOID_SPEED_INC = 3;
    protected static final int LEV_3_SPEED = 150;
    protected static final int DEFAULT_SPEED = 100;
    protected static final int LEV_1 = 1;
    protected static final int LEV_2 = 2;
    protected static final int LEV_3 = 3;
    protected static final int FINAL_LEVEL = 4;
    protected static final int PLAYER_WIDTH = 75;
    protected static final int PLAYER_HEIGHT = 75;

    // Array of splash images for different game stages
    private static final String[] SPLASH_IMGS={INTRO_SPLASH_FILE, "assets/rulespg1.gif", "assets/rulespg2.gif", "assets/level1.gif", "assets/level2.gif","assets/level3.gif", "assets/finalLevel.gif", "assets/win.gif", "assets/lose.gif"};
    private static int CURRENT_SPLASH_IDX = 0;

    // Power-up identifier
    protected static final int POWERUP = 3;

    // Player's current number of collected coins
    protected int coinsCollected;

    // Flag indicating if the spaceship has been added to the game
    protected boolean spaceshipAdded;
    protected Entity spaceship;
    protected int spaceshipHP;

    // Timer for power-up duration (in ticks)
    private static final int POWER_UP_DURATION_TICKS = 4 * 60; // Assuming 60 ticks per second
    private int powerUpTimer = 0;

    // Player's current fuel level and current game level
    protected double fuel;
    protected int currentLevel;

    // Array defining types of entities that can be spawned
    protected int[] entityTypes;

    public AsteroidArena(){
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public AsteroidArena(int gameWidth, int gameHeight){
        super(gameWidth, gameHeight);
    }

    //Performs all of the initialization operations that need to be done before the game starts
    protected void pregame(){
        super.pregame();
        this.setBgImage("assets/bg.gif");
        this.CURRENT_SPLASH_IDX = 0;
        this.coinsCollected = 0;
        this.fuel = MAX_FUEL;
        this.currentLevel = 1;
        this.entityTypes = new int[]{COLLECT, AVOID};
        this.spaceshipAdded = false;
        this.spaceship = null;
        this.spaceshipHP = MAX_SPACESHIP_HP;
    }

    //Called on each game tick
    protected void updateGame(){
        if(this.currentLevel < FINAL_LEVEL){                                 //decrease fuel if the current level is less than 4
            this.fuel-=FUEL_DEC;
        }
        boolean isNewLevel = updateLevel();                        //check if the level has been updated
        if (isNewLevel && this.currentLevel <= FINAL_LEVEL) {
            this.fuel = MAX_FUEL;
            this.setSplashImage(SPLASH_IMGS[2 + this.currentLevel]);
            player.setX(STARTING_PLAYER_X);
            player.setY(STARTING_PLAYER_Y);
            resetEntities();
        }

        if (powerUpTimer > 0) {
            powerUpTimer--;
        }
        else if(powerUpTimer == 0 && player.getWidth()==PLAYER_DEC) {
                player.setWidth(PLAYER_WIDTH-PLAYER_DEC);
                player.setHeight(PLAYER_HEIGHT-PLAYER_DEC);
                this.setGameSpeed(LEV_3_SPEED);
        }


        //scroll all scrollable Entities on the game board
        scrollEntities();

        //Spawn new entities only at a certain interval
        if ((super.getTicksElapsed() % SPAWN_INTERVAL == 0) && this.currentLevel != FINAL_LEVEL) {     //excluding level 4
            spawnEntities();
        }

        checkForPlayerCollisions();
        updateTitleText();
        gcOffscreenEntities();

        if (this.currentLevel == LEV_2) {
            level2();
        }
        else if (this.currentLevel == LEV_3) {
            level3();
        }
        else if (this.currentLevel == FINAL_LEVEL) {
            finalLevel();
            checkShotCollisions();
        }
    }

    protected boolean updateLevel() {          //updates the current level based on the score.
        if (this.score >= (SCORE_FOR_NEXTLEVEL * this.currentLevel) && currentLevel != FINAL_LEVEL) {
            this.currentLevel += 1;
            player.setWidth(PLAYER_WIDTH-PLAYER_DEC);
            player.setHeight(PLAYER_HEIGHT-PLAYER_DEC);
            return true;
        }
        return false;
    }

    protected void resetEntities() {                //deletes all entities except for the player, for starting a new level
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e instanceof Collect || e instanceof Avoid) {
                e.setVisible(false);
                e.setGCFlag(true);
            }
        }
    }

    protected void spawnEntities() {
        int randomNumEntitiesToBeSpawned = rand.nextInt(MAX_ENTITIES_TO_SPAWN) + 1;   //max entities spawned for a particular tick
        int ct = 0;
        while (ct < randomNumEntitiesToBeSpawned) {
            Entity e = chooseRandomEntity();
            if (this.currentLevel == LEV_2) {                //changing entity speed and size-level 2
                if (e instanceof Collect) {
                    updateEntityProperties(e, (((Scrollable) e).getScrollSpeed()) + COLLECT_SPEED_INC, e.getWidth() - ENTITY_DEC, e.getHeight() - ENTITY_DEC);
                } else if (e instanceof Avoid) {
                    updateEntityProperties(e, (((Scrollable) e).getScrollSpeed()) + AVOID_SPEED_INC, e.getWidth() + ENTITY_DEC, e.getHeight() + ENTITY_DEC);
                }
            }
            Entity toBeSpawned = super.addEntity(e);
            if (this.getAllCollisions(toBeSpawned).size() == 0) {          //prevents adding overlapping entities to the display list
                entities.add(toBeSpawned);
            }
            ct++;
        }
    }

    protected Entity chooseRandomEntity(){                            //returns a random entity to be spawned
        int randomType = rand.nextInt(entityTypes.length) + 1;
        int randomRareType = rand.nextInt(RARE_ENTITY_ODDS);                           //odds of a rare entity
        if(randomType == COLLECT){
            if(randomRareType == 1) {
                return new RareCollect();
            }
            else{
                return new Collect();
            }
        }
        else if(randomType == AVOID) {
            if(randomRareType == 1) {
                return new RareAvoid();
            }
            else{
                return new Avoid();
            }
        }
        else{
            if(randomRareType == 1)
                return new PowerUp();
            else
                return new Avoid();
        }
    }

    //Called whenever it has been determined that the Player collided with a consumable
    protected void collidedWithPlayer(Consumable collidedWith) {
        if (collidedWith instanceof RareCollect) {
            this.fuel = MAX_FUEL;
        }
        else if (collidedWith instanceof PowerUp) {
            player.setWidth(PLAYER_DEC);
            player.setHeight(PLAYER_DEC);
            this.setGameSpeed(DEFAULT_SPEED);
            powerUpTimer = POWER_UP_DURATION_TICKS; // Start the power-up timer
        }
        else if (collidedWith instanceof Collect) {
            this.coinsCollected += 1;
        }
        else if (collidedWith instanceof RareAvoid) {
            this.fuel /= 2;
        }

        super.collidedWithPlayer(collidedWith);
    }

    //Update the text at the top of the game window
    protected void updateTitleText() {
        String fuelDec = Double.toString(this.fuel);
        if (this.currentLevel == FINAL_LEVEL) {
            setTitleText("LEVEL: " + this.currentLevel + ", HP: " + player.getHP() + ", Score: " + this.score + ", Shots: " + this.coinsCollected + ", Fuel: " + fuelDec.substring(0, 3) + "%, Spaceship HP: "+this.spaceshipHP);
        }
        else {
            setTitleText("LEVEL: " + this.currentLevel + ", HP: " + player.getHP() + ", Score: " + this.score + ", Coins: " + this.coinsCollected + ", Fuel: " + fuelDec.substring(0, 4) + "%");
        }
    }

    private void level2() {                          //LEVEL 2
        this.MAX_ENTITIES_TO_SPAWN = 4;
        player.setWidth(PLAYER_WIDTH-PLAYER_DEC);
        player.setHeight(PLAYER_HEIGHT-PLAYER_DEC);
        this.RARE_ENTITY_ODDS = 6;
    }

    private void level3() {                          //LEVEL 3
        this.entityTypes = new int[]{COLLECT, AVOID, POWERUP};
        this.setGameSpeed(LEV_3_SPEED);
        this.RARE_ENTITY_ODDS = 4;
    }

    private void finalLevel() {                     //FINAL LEVEL
        this.setGameSpeed(DEFAULT_SPEED);
        if (!this.spaceshipAdded) {
            this.entityTypes = new int[]{AVOID};
            Entity e = new Avoid(this.getWindowWidth() - 75, 50, "assets/spaceship.gif");
            this.spaceship = super.addEntity(e);
            spaceship.setY(150);
            updateEntityProperties(e, 1, e.getWidth() * 3, e.getHeight() * 3);
            entities.add(this.spaceship);
            this.spaceshipAdded = true;
        }
        if (spaceship.getX() == this.getWindowWidth() - spaceship.getWidth()) {
            updateEntityProperties(spaceship, 0, spaceship.getWidth(), spaceship.getHeight());
        }

        emitAvoids();                             //spaceship emits Avoid entities

        ((Entity) spaceship).setGCFlag(false);                //prevent the spaceship from being garbage collected
    }

    private void emitAvoids() {                                //for spaceship to emit Avoids
        if (super.getTicksElapsed() % (SPAWN_INTERVAL*2) == 0 && spaceshipHP>0) { //emit every spawn interval
            int centerX = spaceship.getX() + (spaceship.getWidth() / 2);
            int centerY = spaceship.getY() + (spaceship.getHeight() / 2);

            //define directions
            SmallAvoid left = new SmallAvoid(centerX, centerY, -5, 0, 20); //left
            SmallAvoid bottomLeft = new SmallAvoid(centerX, centerY, -4, 2, 20); //bottom-left
            SmallAvoid topLeft = new SmallAvoid(centerX, centerY, -4, -2, 20); //top-left
            SmallAvoid bottomLeftDiagonal = new SmallAvoid(centerX, centerY, -3, 3, 20); //45 degrees down
            SmallAvoid topLeftDiagonal = new SmallAvoid(centerX, centerY, -3, -3, 20); //45 degrees up

            entities.add(left);
            entities.add(bottomLeft);
            entities.add(topLeft);
            entities.add(bottomLeftDiagonal);
            entities.add(topLeftDiagonal);
        }
    }

    private void updateEntityProperties(Entity E, int newSpeed, int newWidth, int newHeight) {      //updates entity properties
        ((Scrollable) E).setScrollSpeed(newSpeed);
        E.setWidth(newWidth);
        E.setHeight(newHeight);
    }

    private void checkShotCollisions() {
        for (int i = entities.size() - 1; i >= 0; i--) {
            Entity entity = entities.get(i);
            if (entity instanceof Shot) {
                Shot shot = (Shot) entity;
                //check collision with the spaceship
                if (spaceship != null && shot.isColliding(spaceship)) {
                    spaceshipHP -= 10;
                    this.score+=20;
                    shot.setGCFlag(true);
                    shot.setVisible(false);
                    if (spaceshipHP <= 0) {
                        spaceship.setGCFlag(true);
                        resetEntities();
                    }
                    continue;
                }
                //check collision with smaller avoids
                for (int j = entities.size() - 1; j >= 0; j--) {
                    Entity otherEntity = entities.get(j);
                    if (otherEntity instanceof SmallAvoid && shot.isColliding(otherEntity)) {
                        shot.setGCFlag(true);
                        shot.setVisible(false);
                        otherEntity.setGCFlag(true);
                        otherEntity.setVisible(false);
                        break;
                    }
                }
            }
        }
    }

    //Returns a boolean indicating if the game is over (true) or not (false)
    //Game can be over due to either a win or lose state
    protected boolean isGameOver() {
        if(player.getHP() <= 0 || this.fuel <= 0 || (currentLevel==FINAL_LEVEL && spaceshipHP>0 && coinsCollected<=0) || (currentLevel==FINAL_LEVEL && spaceshipHP<=0))
            return true;
        return false;
    }

    //Called once the game is over, performs any end-of-game operations
    protected void postgame() {
        if (currentLevel==FINAL_LEVEL && spaceshipHP<=0) {
            super.setTitleText("GAME OVER - You won! SCORE: "+this.score);
            this.setSplashImage(SPLASH_IMGS[SPLASH_IMGS.length - 2]);
        }
        else if (player.getHP() <= 0 || this.fuel <= 0 || (currentLevel==FINAL_LEVEL && spaceshipHP>0 && coinsCollected<=0)) {
            super.setTitleText("GAME OVER! - You lose! SCORE: "+this.score);
            this.setSplashImage(SPLASH_IMGS[SPLASH_IMGS.length - 1]);
        }
    }

    protected void keyReact(int key){
        //if a splash screen is up, only react to the advance splash key
        if (getSplashImage() != null){
            if (key == ADVANCE_SPLASH_KEY)
                if(this.currentLevel==1 && (getSplashImage().equals(INTRO_SPLASH_FILE)||getSplashImage().equals(SPLASH_IMGS[1])||getSplashImage().equals(SPLASH_IMGS[2]))){
                    super.setSplashImage(SPLASH_IMGS[CURRENT_SPLASH_IDX+1]);
                    CURRENT_SPLASH_IDX+=1;
                }
                else{
                    super.setSplashImage(null);
                }
            return;
        }
        else{
            if(super.search(key, MOVEMENT_KEYS)==true){
                if(!this.isPaused)
                arrowKeyPressed(key);
            }
            else if(key==SPEED_UP_KEY && this.currentLevel==1){
                if(this.getGameSpeed()<MAX_GAME_SPEED){
                    this.setGameSpeed(this.getGameSpeed()+SPEED_CHANGE_INTERVAL);
                }
            }
            else if(key==SPEED_DOWN_KEY && this.currentLevel==1){
                if(this.getGameSpeed()>0){
                    this.setGameSpeed(this.getGameSpeed()-SPEED_CHANGE_INTERVAL);
                }
            }
            else if(key==KEY_PAUSE_GAME){
                this.isPaused=!this.isPaused;
            }
        }
    }


    //Handles reacting to a single mouse click in the game window
    protected MouseEvent reactToMouseClick(MouseEvent click){
        if (click != null && coinsCollected>0) {
            Shot shot = new Shot(player.getX() + player.getWidth(), player.getY() + (player.getHeight() / 2));
            shot.setScrollSpeed(10);
            entities.add(shot);
            coinsCollected--;
        }
        return click;
    }

}
