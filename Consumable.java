//Interface to be implemented by any Entities which are "consumable"
//Consumable means that the player's HP and/or Score values are modified
//when they collide with this Entity
public interface Consumable {
    
    //The amount that the player's score is modified (positive or negative) 
    //Ex: if colliding with this entity would increase the Player's score by 10 points,
    //     this method would return 10 
    public int getScoreModifier();
    //The amount that the player's HP is modified (positive or negative)
    //Ex: if colliding with this entity would reduce the Player's HP by 2 points,
    //     this method would return -2
    public int getHPModifier();
    
}
