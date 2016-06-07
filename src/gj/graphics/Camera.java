package gj.graphics;

import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Image;
import gj.GameWorld;
import gj.entities.Entity;
import gj.util.Transform;
import java.awt.Color;

/**
 * This class represents a camera that watches a portion of a given game world
 * and renders it to the user's screen in a physical window.
 * <p>
 * The main role of the camera is to allow translation between world
 * coordinates, which are used by entities in the game world, and screen
 * coordinates, which are used to determine the actual physical location of
 * drawn entities on the screen. This allows entities to be translated in world
 * space without any concern for their resulting locations on the user's
 * display.
 * <p>
 * Cameras themselves can also be translated and manipulated to display
 * different parts of the game world. This should be particularly useful for
 * games such as side-scrolling platformers where the camera typically follows
 * the avatar and only displays a small portion of the level at any given time.
 * <p>
 * A camera can also display debug visuals on top of the rendered scene that aim
 * to assist with development. These visuals appear in red.
 * 
 * @author Joseph
 * @version 04-Jun-2016
 */
public class Camera {
    /**
     * The transform of this camera, which represents the position and size of
     * the viewport. With each rendering call, the camera will iterate over
     * every entity that intersects with the view in logical space and draw them
     * in physical space (i.e. with all necessary transformations applied).
     */
    private final Transform view;
    
    /**
     * The game world that the camera will display. Note that these classes have
     * a two-way dependency, which is necessary because the camera is updated in
     * the main game loop defined in GameWorld.
     */
    private final GameWorld logicalWorld;
    
    /**
     * The window to which the camera will draw the world.
     */
    private final RenderWindow physicalWindow;
    
    /**
     * If set to true, the camera will render additional information to assist
     * with development and debugging. This should always be false in released
     * products.
     */
    private boolean debugging;
    
    /**
     * Constructor for a new Camera that views a given portion of the provided
     * GameWorld and renders it to the given RenderWindow.
     * 
     * @param world The GameWorld to be rendered.
     * @param window The RenderWindow to render this camera's view in.
     * @param initX The initial X coordinate of this camera's view.
     * @param initY The initial Y coordinate of this camera's view.
     * @param initW The initial width of this camera's view.
     * @param initH The initial height of this camera's view.
     */
    public Camera(GameWorld world, RenderWindow window, int initX, int initY, int initW, int initH) {
        this.logicalWorld = world;
        this.physicalWindow = window;
        this.view = new Transform(initX, initY, initW, initH);
        this.debugging = false;
    }
    
    /**
     * Constructor for a new Camera that renders the given GameWorld to the
     * given RenderWindow on a 1:1 scale. In other words, the camera's view is
     * initialised to match the physical width and height of the window - the
     * logical and physical coordinate spaces are assumed to be the same.
     * 
     * @param world The GameWorld to be rendered.
     * @param window The RenderWindow to render this camera's view in.
     */
    public Camera(GameWorld world, RenderWindow window) {
        // Initialise the camera to match the physical resolution.
        this(world, window, 0, 0, window.getCurrentDisplayMode().getWidth(), window.getCurrentDisplayMode().getHeight());
    }
    
    /**
     * Acquires the transform representing this camera's view. Clients can
     * manipulate the transform to move and scale the camera.
     * 
     * @return The Transform defining the Camera.
     */
    public Transform getTransform() {
        return view;
    }
    
    /**
     * Determines whether to display debugging visuals on top of the rendered
     * scene.
     * 
     * @param debugging Whether to display debugging visuals.
     */
    public void setDebugVisuals(boolean debugging) {
        this.debugging = debugging;
    }
    
    /**
     * Renders this camera's associated GameWorld to its associated
     * RenderWindow. Only entities that fall within the view of this camera are
     * drawn.
     * 
     * @param dT The amount of time that has passed since the previous update.
     */
    public void render(double dT) {
        Graphics g = physicalWindow.getOffscreenGraphics();
        DisplayMode dispMode = physicalWindow.getCurrentDisplayMode();
        
        if (g != null) { // If the window is currently available for drawing...
            g.setColor(Color.red);
            g.setFont(new java.awt.Font("System", java.awt.Font.BOLD, 12));
            
            java.util.Collection<Entity> intersectingEnts = logicalWorld.getIntersectingEntities(view);
            for (Entity e : intersectingEnts) {
                Image drawImg = e.getImage();
                
                // Scale the image according to the difference in size between the physical and logical viewports.
                drawImg = drawImg.getScaledInstance(
                        (int)(e.getTransform().getWidth() * (dispMode.getWidth() / this.view.getWidth())),
                        (int)(e.getTransform().getHeight() * (dispMode.getHeight() / this.view.getHeight())),
                        Image.SCALE_FAST);
                
                // Draw the image, offsetting it from the origin of the physical viewport if the logical viewport is at a different location.
                int physicalX = (int)((e.getTransform().getX() - this.view.getX()) * (dispMode.getWidth() / this.view.getWidth()));
                int physicalY = (int)((e.getTransform().getY() - this.view.getY()) * (dispMode.getHeight() / this.view.getHeight()));
                g.drawImage(drawImg, physicalX, physicalY, null);
                
                // If debug mode is turned on, display the entity's string representation on top of it.
                if (debugging) {
                    g.drawString(e.toString(), physicalX, physicalY+10);
                }
            }
            
            // Display debugging information if debug mode is enabled.
            if (debugging) {
                // Draw an oval around the origin of the world ((0,0) in logical space).
                g.drawOval((int)-view.getX() - 5, (int)-view.getY() - 5, 10, 10);
                
                g.drawString("FPS: " + (1.0 / dT), 2, 10);
                g.drawString("Total entities: " + logicalWorld.getAllEntities().size(), 2, 25);
                g.drawString("Rendered entities: " + intersectingEnts.size(), 2, 40);
                g.drawString("Camera position: (" + view.getX() + ", " + view.getY() + ")", 2, 55);
                g.drawString("Camera size: [" + view.getWidth() + ", " + view.getHeight() + "]", 2, 70);
            }
            
            physicalWindow.repaint();
        } // End of 'if (g != null)'
    }
}
