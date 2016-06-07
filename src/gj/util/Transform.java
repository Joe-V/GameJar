package gj.util;

import java.awt.geom.Rectangle2D;

/**
 * This class represents a rectangular world transform with a position and a
 * size. It is implemented as an extension to Rectangle2D.Double and adds
 * support for modifying the position and size of the rectangle at runtime.
 * 
 * @author Joseph
 * @version 04-Jun-2016
 */
public class Transform extends Rectangle2D.Double {
    /**
     * Constructor for a new Transform.
     * 
     * @param initX The initial X coordinate of the transform.
     * @param initY The initial Y coordinate of the transform.
     * @param initW The initial width of the transform.
     * @param initH The initial height of the transform.
     */
    public Transform(double initX, double initY, int initW, int initH) {
        super(initX, initY, initW, initH);
    }
    
    public void setX(double newX) {
        setPosition(newX, this.getY());
    }
    
    public void setY(double newY) {
        setPosition(this.getX(), newY);
    }
    
    public void setPosition(double newX, double newY) {
        this.setRect(newX, newY, this.getWidth(), this.getHeight());
    }
    
    public void translate(double deltaX, double deltaY) {
        setPosition(this.getX() + deltaX, this.getY() + deltaY);
    }
    
    public void setWidth(double newWidth) {
        setSize(newWidth, this.getHeight());
    }
    
    public void setHeight(double newHeight) {
        setSize(this.getWidth(), newHeight);
    }
    
    public void setSize(double newWidth, double newHeight) {
        this.setRect(this.getX(), this.getY(), newWidth, newHeight);
    }
    
    public void scale(double scaleFactor) {
        setSize(this.getWidth() * scaleFactor, this.getHeight() * scaleFactor);
    }
}
