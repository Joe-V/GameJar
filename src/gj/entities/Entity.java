
package gj.entities;

import gj.util.Transform;
import java.awt.Image;

/**
 * An instance of this class represents a single logical entity in the game
 * world, which has its own position and size and can be rendered to the screen
 * and made to act.
 * 
 * @author Joseph
 * @version 07-Jun-2016
 */
public class Entity implements Comparable<Entity> {
    /**
     * The transform representing the position and size of this entity. The size
     * of the transform is always kept equal to the size of the image used to
     * represent it visually (below).
     */
    private final Transform transform;
    
    /**
     * The image used to visually render the entity, which also defines the
     * entity's 2D width and height.
     */
    private final Image image; // To be replaced with an animated sprite.
    
    /**
     * This entity's Z-height. Entities with a larger Z-height are drawn on top
     * of those with a smaller Z-height should they overlap. If two entities
     * have the same Z-height, the drawing order is undefined.
     */
    private final int zHeight;
    
    public Entity(double initX, double initY, Image img, int zHeight) {
        this.transform = new Transform(initX, initY, img.getWidth(null), img.getHeight(null));
        this.image = img;
        this.zHeight = zHeight;
    }
    
    public Entity(double initX, double initY, Image img) {
        this(initX, initY, img, 0);
    }
    
    public final int getZHeight() {
        return zHeight;
    }
    
    // Z-height manipulation. If this is changed, the entity would need to be
    // readded to the entity list to move it to the correct position.
    /*public final void setZHeight(int newHeight) {
        if (zHeight != newHeight) {
            this.zHeight = newHeight;
        }
    }*/
    
    /**
     * Accessor method for the image used to represent this entity visually.
     * 
     * @return The Image representing this Entity for the purpose of graphics
     * rendering.
     */
    public final Image getImage() {
        return image;
    }
    
    /**
     * Logically updates this entity by simulating the given time period.
     * Subtypes of Entity should implement this method to include the logical
     * process of updating the entity each time the game world advances by a
     * certain amount. The default implementation of this method does nothing.
     * <p>
     * The amount of elapsed time since the previous update may vary according
     * to the current simulation rate. The implementation of this method is
     * expected to use the formal delta-time parameter to scale actions
     * accordingly (e.g. multiplying a velocity by dt means that the velocity is
     * measured in units per second rather than units per frame).
     * 
     * @param dt The amount of time that has passed since the previous update,
     * measured in seconds.
     */
    public void update(double dt) {
        // Do nothing by default. Leaving this method as non-abstract means subclasses aren't required to implement it.
    }
    
    public Transform getTransform() {
        return transform;
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s", this.getClass().getName(), this.transform.toString());
    }
    
    @Override
    public int compareTo(Entity other) {
        return this.zHeight - other.zHeight;
    }
}
