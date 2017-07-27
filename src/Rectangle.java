import java.awt.*;
public class Rectangle {
    Vector position;
    Vector speed;
    Vector size;
    Color color;
    boolean movesVert;

    public Rectangle(Vector position, Vector speed, Vector size, Color color, boolean movesVert) {
        this.position = position;
        this.speed = speed;
        this.size = size;
        this.color = color;
        this.movesVert = movesVert;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int)position.x, (int)position.y, (int)size.x, (int)size.y);
    }
    public void update(int dt, Rectangle rectangle, Graphics2D g, boolean movesVert) {
        if(newRectangleNeeded(Game.characterPosition, rectangle, (int)rectangle.size.x, Game.characterSize)) {
            makeNewRects(true, rectangle, 1066);
        }
        draw(g);
        if(movesVert) {
            rectangle.position.y += rectangle.speed.y;
        } else {
            rectangle.position.x += rectangle.speed.x;
        }
        if (rectangle.position.x + size.x > 800 || rectangle.position.x < 0) {
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
