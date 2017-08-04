import org.w3c.dom.css.Rect;

import java.awt.*;

public class Rectangle {
    Vector position;
    Vector speed;
    Vector size;
    Color color;
    boolean movesVert;
    int rectSpeed;
    boolean isBullet;
    int rectType;
    int age;
    boolean outOfBounds;


    public Rectangle(Vector position, Vector speed, Vector size, Color color, boolean movesVert, int rectSpeed, boolean isBullet, int rectType, int age, boolean outOfBounds) {
        this.position = position;
        this.speed = speed;
        this.size = size;
        this.color = color;
        this.movesVert = movesVert;
        this.rectSpeed = rectSpeed;
        this.isBullet = isBullet;
        this.rectType = rectType;
        this.age = age;
        this.outOfBounds = outOfBounds;
    }

    public void draw(Graphics2D g, Rectangle rect) {
        g.setColor(color);
        g.drawRect((int) rect.position.x, (int) rect.position.y, (int) rect.size.x, (int) rect.size.y);
        if (!rect.isBullet) {
            if (rect.rectType == 0) {
                int xPoly[] = {(int) rect.position.x, (int) (rect.position.x + (rect.size.x / 2)), (int) (rect.position.x + (rect.size.x)), (int) (rect.position.x + (rect.size.x / 2))};
                int yPoly[] = {(int) (rect.position.y + (rect.size.x / 2)), (int) (rect.position.y + (rect.size.x)), (int) (rect.position.y + (rect.size.x / 2)), (int) (rect.position.y)};
                g.drawPolygon(xPoly, yPoly, xPoly.length);
            } else if (rect.rectType == 1) {
                g.drawOval((int) rect.position.x, (int) rect.position.y, (int) rect.size.x, (int) rect.size.y);
            } else if (rect.rectType == 2) {
                int rectSmallFactor = 20;
                g.drawRect((int) (rect.position.x + ((rect.size.x - rectSmallFactor * 1.5) / 2)), (int) (rect.position.y + ((rect.size.x - rectSmallFactor * 1.5) / 2)), (int) (rect.size.x - rectSmallFactor), (int) (rect.size.x - rectSmallFactor));
            } else if (rect.rectType == 3) {
                int xPoly[] = {(int) rect.position.x, (int) (rect.position.x + rect.size.x), (int) (rect.position.x + rect.size.x / 2)};
                int yPoly[] = {(int) (rect.position.y + rect.size.y), (int) (rect.position.y + rect.size.y), (int) (rect.position.y)};
                g.drawPolygon(xPoly, yPoly, xPoly.length);
            } else if (rect.rectType == 4) {
                int smallRectSize = 20;
                g.drawRect((int) rect.position.x, (int) rect.position.y, smallRectSize, smallRectSize);
                g.drawRect((int) (rect.position.x), (int) (rect.position.y + rect.size.x - smallRectSize), smallRectSize, smallRectSize);
                g.drawRect((int) (rect.position.x + rect.size.x - smallRectSize), (int) rect.position.y, smallRectSize, smallRectSize);
                g.drawRect((int) (rect.position.x + rect.size.x - smallRectSize), (int) (rect.position.y + rect.size.x - smallRectSize), smallRectSize, smallRectSize);
            }
        }
    }

    /**
     * @param dt - delta time
     * @param rectangle - the rectangle being updated
     * @param g - the graphics 2d object
     * @param movesVert - weather it moves verticle or horizontal
     */
    boolean rectangleAlive = true;

    public void update(int dt, Rectangle rectangle, Graphics2D g, boolean movesVert, int obstSize) {

        //moves bullets free of collision
        if (rectangle.isBullet) {
            if (!rectangle.outOfBounds) {
                rectangle.position.add(rectangle.speed);
                draw(g, rectangle);
            }
        } else {
            //checks void collision <--- useful to future me
            if (!Game.checkCollision(new Vector(820, 455), rectangle, 50, 80)) {
                rectangleAlive = false;
            }
            if (rectangleAlive) {
                draw(g, rectangle);
                speed = Vector.sub(Game.characterPosition, rectangle.position);
                speed.setMag(rectangle.rectSpeed);
                rectangle.position.add(speed);
                rectangle.size = new Vector(obstSize, obstSize);
            }
        }
    }


    public boolean newRectangleNeeded(Vector characterPosition, Rectangle rectangle, int OBSTSIZE, int characterSize) {
        if (!Game.checkCollision(characterPosition, rectangle, OBSTSIZE, characterSize)) {
            System.out.println("Collision detected");
            return true;
        } else {
            return false;

        }
    }

    public void makeNewRects(boolean newRectangleNeeded, Rectangle rectangle, int WIDTH) {
        if (newRectangleNeeded) {
            if (!rectangle.movesVert) {
                rectangle.position.x = 88;
                rectangle.position.y = (int) (Math.random() * 550);
            } else {
                rectangle.position.x = (int) (Math.random() * (WIDTH - 50) + 88);
                rectangle.position.y = 0;
                System.out.println("Vert Rect Moved");
            }
        }


    }


}
