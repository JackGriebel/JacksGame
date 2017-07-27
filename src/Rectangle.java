import java.awt.*;
public class Rectangle {
    Vector position;
    Vector speed;
    Vector size;
    Color color;
    boolean movesVert;
    int rectSpeed;

    public Rectangle(Vector position, Vector speed, Vector size, Color color, boolean movesVert, int rectSpeed) {
        this.position = position;
        this.speed = speed;
        this.size = size;
        this.color = color;
        this.movesVert = movesVert;
        this.rectSpeed = rectSpeed;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int)position.x, (int)position.y, (int)size.x, (int)size.y);
    }

    /**
     * @param dt - delta time
     * @param rectangle - the rectangle being updated
     * @param g - the graphics 2d object
     * @param movesVert - weather it moves verticle or horizontal
     */
    public void update(int dt, Rectangle rectangle, Graphics2D g, boolean movesVert) {
        //if(newRectangleNeeded(Game.characterPosition, rectangle, (int)rectangle.size.x, Game.characterSize)) {
        //    makeNewRects(true, rectangle, 1066);
       // }
        draw(g);
        speed = Vector.sub(Game.characterPosition, rectangle.position);
        speed.setMag(rectangle.rectSpeed);
        rectangle.position.add(speed);

        if (rectangle.position.x + size.x > 1066 || rectangle.position.x < 0) {
            rectangle.position.x = 88;
            rectangle.position.y = (float) Math.random() * 550;
            System.out.println("Horizontal rectangle created");
        }
        if (rectangle.position.y + size.y > 800 || rectangle.position.y < 0) {
            rectangle.position.x = (float) Math.random() * (1066 - 88 - 50) + 88;
            rectangle.position.y = 0;
            System.out.println("vertical rectangle created");
        }
    }



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
