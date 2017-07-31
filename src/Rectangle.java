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

    public Rectangle(Vector position, Vector speed, Vector size, Color color, boolean movesVert, int rectSpeed, boolean isBullet) {
        this.position = position;
        this.speed = speed;
        this.size = size;
        this.color = color;
        this.movesVert = movesVert;
        this.rectSpeed = rectSpeed;
        this.isBullet = isBullet;
    }

    public void draw(Graphics2D g, Rectangle rect) {
        g.setColor(color);
        g.drawImage(Game.createTexture("C:\\Users\\IGMAdmin\\JacksGame\\images\\missile.png"), (int)rect.position.x, (int)rect.position.y, (int)rect.size.x, (int)rect.size.y, null);

    }

    /**
     * @param dt - delta time
     * @param rectangle - the rectangle being updated
     * @param g - the graphics 2d object
     * @param movesVert - weather it moves verticle or horizontal
     */
    boolean rectangleAlive = true;
    public void update(int dt, Rectangle rectangle, Graphics2D g, boolean movesVert, int obstSize) {
        if(rectangle.isBullet) {
            rectangle.position.add(rectangle.speed);
            draw(g, rectangle);
        } else {
        if(!Game.checkCollision(new Vector(493, 360), rectangle, 50, 80)) {
            rectangleAlive = false;
        }
        if(rectangleAlive) {
            draw(g, rectangle);
            speed = Vector.sub(Game.characterPosition, rectangle.position);
            speed.setMag(rectangle.rectSpeed);
            rectangle.position.add(speed);
            rectangle.size = new Vector(obstSize, obstSize);
        } } }


    public boolean newRectangleNeeded(Vector characterPosition, Rectangle rectangle, int OBSTSIZE, int characterSize) {
        if (!Game.checkCollision(characterPosition, rectangle, OBSTSIZE, characterSize))
        {
            System.out.println("Collision detected");
            return true;
        } else {
            return false;

        }
    }

    public void makeNewRects(boolean newRectangleNeeded, Rectangle rectangle, int WIDTH) {
        if(newRectangleNeeded) {
            if(!rectangle.movesVert) {
                rectangle.position.x = 88;
                rectangle.position.y =  (int)(Math.random() * 550);
            } else {
                rectangle.position.x = (int) (Math.random() * (WIDTH - 88 - 50) + 88);
                rectangle.position.y = 0;
                System.out.println("Vert Rect Moved");
            }
        }



    }




        }
