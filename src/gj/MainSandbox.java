package gj;

import gj.graphics.RenderWindow;
import java.awt.DisplayMode;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.imageio.ImageIO;
import gj.entities.Entity;

/**
 * Experimental sandbox class.
 * 
 * @author Joseph
 * @version 07-Jun-2016
 */
public class MainSandbox {
    public static void main(String[] args) throws java.io.IOException {
        System.out.println("Available display modes:");
        for (DisplayMode m : RenderWindow.getAvailableDisplayModes()) {
            System.out.println(m.getWidth() + "x" + m.getHeight() + " : " + m.getBitDepth() + "-bit colour at " + m.getRefreshRate() + "Hz");
        }
        
        RenderWindow window = new RenderWindow("GameJar - Test RenderWindow", new DisplayMode(1366, 768, 32, 60), false);
        
        Image img = ImageIO.read(new File("assets\\test-image-A.png"));
        Image img2 = ImageIO.read(new File("assets\\test-image-B.gif"));
        GameWorld world = new GameWorld(60, window); world.setDebugMode(true);
        world.addEntity(new Entity(200, 20, img, 30));
        world.addEntity(new Entity(30, 0, img2, 45) {
            @Override
            public void update(double dt) {
                this.getTransform().translate(50 * dt, 0.4 * dt);
            }
        });
        
        world.start();
        
        window.addKeyListener(new java.awt.event.KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'c' : world.stop(); window.close(); break;
                    case '1' : window.setDisplayMode(new DisplayMode(800, 600, 32, 60)); break;
                    case '2' : window.setDisplayMode(new DisplayMode(1280, 720, 32, 60)); break;
                    case '3' : window.setDisplayMode(new DisplayMode(1366, 768, 32, 60)); break;
                    case 'f' : window.setFullScreen(true); break;
                    case 'n' : window.setFullScreen(false); break;
                }
            }
        });
    }
}
