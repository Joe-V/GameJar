package gj.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * 
 * @author Joseph
 * @version 19-May-2016
 */
class RenderPanel extends JPanel {
    /**
     * The image to which all drawing operations are applied. Rendering to an
     * offscreen image rather than drawing directly to the visible image means
     * that we can avoid screen tearing.
     */
    private Image offscreenImage;
    
    /**
     * The colour used to fill the background where nothing is present.
     */
    private final Color VOID_COLOUR = Color.DARK_GRAY;
    
    public RenderPanel(int width, int height) {
        super();
        setPreferredSize(new java.awt.Dimension(width, height));
    }
    
    public void initOffscreenGraphics() {
        Dimension prefSize = getPreferredSize();
        this.offscreenImage = this.getGraphicsConfiguration().createCompatibleImage(prefSize.width, prefSize.height);
    }
    
    public Graphics getOffscreenGraphics() {
        if (offscreenImage != null) {
            return offscreenImage.getGraphics();
        } else {
            return null;
        }
    }
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(offscreenImage, 0, 0, null); // Apply the offscreen image to the screen.
        
        // Clear the offscreen image to prepare for the next frame:
        Graphics offG = getOffscreenGraphics();
        offG.setColor(VOID_COLOUR);
        offG.fillRect(0, 0, getWidth(), getHeight());
    }
}
