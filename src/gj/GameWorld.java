package gj;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import gj.entities.Entity;
import gj.entities.EntityList;
import gj.graphics.Camera;
import gj.graphics.RenderWindow;

/**
 * An instance of this class represents a simulated game world that can contain
 * any number of entities that can be simulated over time. The game world is
 * responsible for managing its list of entities and modelling the logical
 * coordinate space (which is independent of the actual coordinate space used
 * for display).
 * <p>
 * Note that each game world created can only be used once - when a world is
 * terminated using the stop() method, it cannot be started again.
 * 
 * @author Joseph
 * @version 17-May-2016
 */
public class GameWorld {
    /**
     * The collection of entities currently in this world, which are indexed by
     * their height values.
     */
    private final EntityList ents;
    
    /**
     * The time of the most recent update to the world.
     */
    private long latestUpdate;
    
    /**
     * The rate at which updates are made to the game world, in updates per
     * second.
     */
    private final int tickRate;
    
    /**
     * The camera used to render the world.
     */
    private final Camera camera;
    
    /**
     * This instance represents the thread containing the main game loop.
     */
    private final MainLoop mainLoop;
    
    public GameWorld(int tickRate, RenderWindow window) {
        this.ents = new EntityList();
        this.tickRate = tickRate;
        this.latestUpdate = System.currentTimeMillis();
        this.camera = new Camera(this, window);
        this.mainLoop = new MainLoop();
    }
    
    public boolean addEntity(Entity ent) {
        return ents.addEntity(ent);
    }
    
    public boolean removeEntity(Entity ent) {
        return ents.removeEntity(ent);
    }
    
    /**
     * Starts the main loop for this game world.
     * 
     * @throws IllegalThreadStateException If the world has previously been
     * started.
     */
    public void start() throws IllegalThreadStateException {
        mainLoop.start();
    }
    
    /**
     * Shuts down this game world by interrupting the main loop.
     */
    public void stop() {
        mainLoop.interrupt();
    }
    
    public Collection<Entity> getAllEntities() {
        return ents.getAllEntities();
    }
    
    public Collection<Entity> getIntersectingEntities(Rectangle2D region) {
        return ents.getIntersectingEntities(region);
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public void setDebugMode(boolean debug) {
        camera.setDebugVisuals(debug);
    }
    
    private class MainLoop extends Thread {
        @Override
        public final void run() {
            while (!isInterrupted()) {
                //Thread.yield();
                long curTime = System.currentTimeMillis();
                // Abort this cycle if it isn't time to go on yet (using sleep() in a loop is generally discouraged).
                if (curTime < latestUpdate + (1000 / tickRate)) continue;
                //try { Thread.sleep(1000 / tickRate); } catch (InterruptedException ex) {}

                double deltaTime = (curTime - latestUpdate) / 1000.0; // Time since the previous update, in SECONDS.
                latestUpdate = curTime;

                simulate(deltaTime);
                camera.render(deltaTime);
            }
        }
    }
    
    /**
     * Simulates this game world for the given amount of logical time. Each
     * entity is updated in ascending order based on their heights.
     * 
     * @param dt The amount of time to simulate, in seconds.
     */
    private void simulate(double dt) {
        for (Entity ent : ents) {
            ent.update(dt);
        }
    }
}
