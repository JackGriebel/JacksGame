import java.awt.*;

public class Rectangle {
    Vector position;
    Vector speed;
    Vector size;
    Color color;

    public Rectangle(Vector position, Vector speed, Vector size, Color color) {
        this.position = position;
        this.speed = speed;
        this.size = size;
        this.color = color;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect(position.ix, position.iy, size.ix, size.iy);
    }
    public void update(int dt) {
        position.add(Vector.mult(speed, dt));
    }






}
