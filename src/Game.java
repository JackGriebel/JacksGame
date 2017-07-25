import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Game extends JFrame implements KeyListener{

    float vertMovingRectPosX = (float)Math.random() * 800;
    float vertMovingRectPosY = 0;
    float horizMovingRectPosX = (float)Math.random() * 800;
    float horizMovingRectPosY = 0;
    private int clearSwitcher = 0;

    //window vars
    private final int MAX_FPS; //maximum refresh rate
    private final int WIDTH; //window width
    private final int HEIGHT; //window height

    //double buffer strategy
    private BufferStrategy strategy;
    private ArrayList<Integer> keys = new ArrayList<>();

    //loop variables
    private boolean isRunning = true; //is the window running
    private long rest = 0; //how long to sleep the main thread

    //timing variables
    private float dt; //delta time
    private long lastFrame; //time since last frame
    private long startFrame; //time since start of frame
    private int fps; //current fps

    Vector position;
    Vector velocity;
    Vector angle;
    float push;
    float friction = 0.998f;
    int size;

    public Game(int width, int height, int fps){
        super("My Game");
        this.MAX_FPS = fps;
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    /*
     * init()
     * initializes all variables needed before the window opens and refreshes
     */
    void init(){
        //initializes window size
        setBounds(0, 0, WIDTH, HEIGHT);
        setResizable(false);

        //set jframe visible
        setVisible(true);

        //set default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //create double buffer strategy
        createBufferStrategy(2);
        strategy = getBufferStrategy();

        //set initial lastFrame var
        lastFrame = System.currentTimeMillis();

        addKeyListener(this);
        setFocusable(true);
        //set background window color
        setBackground(Color.DARK_GRAY);

        position = new Vector(400,200);
        velocity = new Vector(0, 0);
        angle = new Vector(0,0);
        size = 10;
        push = 1000;
    }

    /*
    /*
     * update()
     * updates all relevant game variables before the frame draws
     */
    private void update(){

        //update current fps
        fps = (int)(1f/dt);
        handelKeys();
        if(position.x + size > WIDTH || position.x <0){
            velocity.setX(velocity.x *= -1);
            angle = new Vector(0,0);
        }
        if(position.y + size > HEIGHT|| position.y <0) {
            velocity.setY(velocity.y *= -1);
            angle = new Vector(0,0);
        }

        velocity.add(Vector.mult(angle, dt));
        //velocity.mult(friction);
        position.add(Vector.mult(velocity, dt));
        //x+=x * dt;
        //y+=y * dt;

    }

    /*
     * draw()
     * gets the canvas (Graphics2D) and draws all elements
     * disposes canvas and then flips the buffer
     */
    private void draw(){
        //get canvas
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

        //clear screen
        if(clearSwitcher % 2 == 0)
        g.clearRect(0,0,WIDTH, HEIGHT);

        //draw fps
        g.setColor(Color.GREEN);
        g.drawString(Long.toString(fps), 10, 40);

        g.setColor(Color.cyan);
        g.fillRect(position.ix, position.iy, size, size);

        g.setColor(Color.GREEN);
        g.fillRect((int)horizMovingRectPosX, (int)horizMovingRectPosY, 20, 20);
        g.fillRect((int)vertMovingRectPosX, (int)vertMovingRectPosY, 20, 20);



        //release resources, show the buffer
        g.dispose();
        strategy.show();


    }

    private void handelKeys() {
        for(int i = 0; i < keys.size(); i++) {
            switch(keys.get(i)) {

                case KeyEvent.VK_UP:
                    angle = Vector.unit2D((float)Math.toRadians(-90));
                    angle.mult(push);
                break;

                case KeyEvent.VK_RIGHT:
                    angle = Vector.unit2D(0);
                    angle.mult(push);
                    break;
                case KeyEvent.VK_LEFT:
                    angle = Vector.unit2D((float)Math.toRadians(180));
                    angle.mult(push);
                    break;

                case KeyEvent.VK_DOWN:
                    angle = Vector.unit2D((float)Math.toRadians(90));
                    angle.mult(push);
                    break;

            }
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if(!keys.contains(keyEvent.getKeyCode()))
            keys.add(keyEvent.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        for(int i = keys.size() - 1; i>=0; i--) {
            if(keyEvent.getKeyCode() == keys.get(i))
                keys.remove(i);
        }
    }

    /*
         * run()
         * calls init() to initialize variables
         * loops using isRunning
            * updates all timing variables and then calls update() and draw()
            * dynamically sleeps the main thread to maintain angle framerate close to target fps
         */
    public void run(){
        init();

        while(isRunning){
            //TODO make an if so these only make whenn their off the screen

            if(horizMovingRectPosX > WIDTH || horizMovingRectPosX < 0) {
            float vertMovingRectPosX = (float)Math.random() * 800;
            float vertMovingRectPosY = 0;
            }
            if(horizMovingRectPosY > WIDTH || horizMovingRectPosY < 0) {
            float horizMovingRectPosX = 0;
            float horizMovingRectPosY = (float)Math.random() * 600;
            }

            clearSwitcher++;
            //new loop, clock the start
            startFrame = System.currentTimeMillis();

            horizMovingRectPosX+=3;
            vertMovingRectPosY+=3;


            //calculate delta time
            dt = (float)(startFrame - lastFrame)/1000;

            //update lastFrame for next dt
            lastFrame = startFrame;

            //call update and draw methods
            update();
            draw();

            //dynamic thread sleep, only sleep the time we need to cap the framerate
            //rest = (max fps sleep time) - (time it took to execute this frame)
            rest = (1000/MAX_FPS) - (System.currentTimeMillis() - startFrame);
            if(rest > 0){ //if we stayed within frame "budget", sleep away the rest of it
                try{ Thread.sleep(rest); }
                catch (InterruptedException e){ e.printStackTrace(); }
            }
        }


    }



    //entry point for application
    public static void main(String[] args){
        Game game = new Game(800, 600, 60);
        game.run();
    }

}
