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
    boolean rectangleAlive = true;
    public void update(int dt, Rectangle rectangle, Graphics2D g, boolean movesVert) {
        if(!Game.checkCollision(new Vector(493, 360), rectangle, 50, 80)) {
            for(Vector i = size; i.ix > 0; i.sub(new Vector(1,1))) {
                size = i;
                System.out.println("shrinking");
            }
            rectangleAlive = false;
        }
        if(rectangleAlive) {
            draw(g);
        }

        speed = Vector.sub(Game.characterPosition, rectangle.position);
        speed.setMag(rectangle.rectSpeed);
        rectangle.position.add(speed);
        //System.out.println("Rectangle position is " + rectangle.position.x + " , " + rectangle.position.y);

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
