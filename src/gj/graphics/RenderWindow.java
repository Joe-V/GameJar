package gj.graphics;

import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import javax.swing.JFrame;

/**
 * This class represents a physical window that can be used to visually draw
 * graphics to the user's screen. The render window supports both windowed and
 * full-screen modes.
 * <p>
 * Internally, all drawing operations are done to a buffered offscreen image,
 * which is then dispatched to the screen as a single atomic operation.
 * <p>
 * Internally, this class extends JFrame by composition. Whenever the current
 * display mode is modified, the old JFrame is discarded and a new one is
 * immediately created to replace it.
 * 
 * @author Joseph
 * @version 04-Jun-2016
 */
public class RenderWindow {
    /**
     * The internal JFrame used to represent this window. RenderWindow extends
     * JFrame through composition.
     * <p>
     * Whenever the display mode of the window changes, the current JFrame is
     * disposed of and a new one is created to replace it.
     */
    private JFrame frame;
    
    /**
     * The title of this window.
     */
    private final String title;
    
    /**
     * The current display mode used by this window. This should match one of
     * the display modes supported by the current machine.s
     */
    private DisplayMode displayMode;
    
    /**
     * Whether or not this window is currently being rendered as a full-screen
     * image.
     */
    private boolean fullscreen;
    
    /**
     * A static class reference to the graphics device for the current machine.
     * This is used to identify what display modes are available and to active
     * full-screen rendering.
     */
    private static GraphicsDevice device;
    
    /**
     * An internal panel to which graphics are drawn.
     */
    private RenderPanel panel;
    
    /**
     * Constructs a new RenderWindow in full-screen mode with the given window
     * title, resolution, colour bit depth and refresh rate. The display may
     * either be full-screen or windowed.
     * 
     * The display mode and full-screen mode cannot be changed once the window
     * has been created. To change either or both of these, dispose of the
     * current RenderWindow and create a new one.
     * 
     * @param title The title of the created window.
     * @param dispMode The display mode to use, which defines the resolution of
     * the window, the bit depth to use for colours and the refresh rate. This
     * should be one of the elements returned by getAvailableDisplayModes().
     * @param fullscreen Whether or not this window should be shown in
     * full-screen mode. This cannot change following the creation of the
     * window.
     */
    public RenderWindow(String title, DisplayMode dispMode, boolean fullscreen) {
        this.title = title;
        this.displayMode = dispMode;
        this.fullscreen = fullscreen;
        
        reloadFrame();
    }
    
    /**
     * Constructs a new RenderWindow in windowed mode with the given title using
     * the lowest quality display mode available, in windowed mode.
     * 
     * @param title The title of the created window.
     */
    public RenderWindow(String title) {
        this(title, getAvailableDisplayModes()[0], false);
    }
    
    /**
     * An internal method for disposing of the current frame (if one exists) and
     * loading a new one based on the current display mode and full-screen
     * setting.
     */
    private void reloadFrame() {
        KeyListener[] keyListeners;
        MouseListener[] mouseListeners;
        
        if (frame != null) { // If the frame is already active...
            // Get its existing key and mouse event listeners.
            keyListeners = frame.getListeners(KeyListener.class);
            mouseListeners = frame.getListeners(MouseListener.class);
            
            // Dispose of the old frame as we are about to replace it.
            frame.dispose();
        } else {
            keyListeners = new KeyListener[0];
            mouseListeners = new MouseListener[0];
        }
        
        //Create the new frame.
        this.frame = new JFrame(title);
        
        // Reregister the listeners for the old frame with the new one.
        for (KeyListener kL : keyListeners) frame.addKeyListener(kL);
        for (MouseListener mL : mouseListeners) frame.addMouseListener(mL);
        
        frame.setName(title);
        frame.setIgnoreRepaint(true); // Ignore OS calls to repaint() since we are handling all repaint calls natively.
        
        this.panel = new RenderPanel(displayMode.getWidth(), displayMode.getHeight());
        frame.add(panel);
        panel.initOffscreenGraphics();
        
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (device == null) {
            device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        }
        
        frame.setUndecorated(fullscreen); // Hide the title bar and control buttons when entering fullscreen mode.
        if (fullscreen) {
            device.setFullScreenWindow(frame);
            device.setDisplayMode(displayMode);
        } else {
            frame.setVisible(true);
            frame.pack();
        }
    }
    
    /**
     * Returns an array of all the valid display modes according to the local
     * graphics environment. This method is identical to calling
     * <code>GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayModes()</code>.
     * 
     * @return An array of legal display modes.
     */
    public static DisplayMode[] getAvailableDisplayModes() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayModes();
    }
    
    /**
     * Sets the current display mode to the one provided. The proposed display
     * mode should be one of those returned by
     * {@link getAvailableDisplayModes()}.
     * 
     * @param dispMode The new display mode to use.
     * @throws IllegalArgumentException If the proposed display mode isn't
     * supported by the current machine (i.e. the OS refuses to switch to it).
     * Use {@link getAvailableDisplayModes()} to identify which display modes
     * are valid.
     */
    public void setDisplayMode(DisplayMode dispMode) throws IllegalArgumentException {
        for (DisplayMode validMode : getAvailableDisplayModes()) {
            if (dispMode.equals(validMode)) {
                this.displayMode = dispMode;
                reloadFrame();
                return;
            }
        }
        throw new IllegalArgumentException("This display mode is not supported: " + displayMode.getWidth() + "x" + displayMode.getHeight() + " : " + displayMode.getBitDepth() + "-bit colour at " + displayMode.getRefreshRate() + "Hz");
    }
    
    public void setFullScreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        reloadFrame();
    }
    
    /**
     * Retrieves the graphics context used to draw to the offscreen image used
     * by this window. Note that any modifications made to the offscreen image
     * won't be applied until the window is repainted.
     * 
     * @return The offscreen Graphics context, or null if no such context exists
     * yet.
     */
    public Graphics getOffscreenGraphics() {
        return panel.getOffscreenGraphics();
    }
    
    public DisplayMode getCurrentDisplayMode() {
        return displayMode;
    }
    
    public void close() {
        frame.dispose();
    }
    
    public void repaint() {
        frame.repaint();
    }
    
    public void addKeyListener(KeyListener l) {
        frame.addKeyListener(l);
    }
    
    public void addMouseListener(MouseListener l) {
        frame.addMouseListener(l);
    }
}
