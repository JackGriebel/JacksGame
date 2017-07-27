import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Game extends JFrame implements KeyListener{
    boolean firstTime = true;
    long numFrames = 0;
    int spawnFrequency = 500; //lower is faster

    final int ENEMYSPEED = 3;

    static int coins = 0;
    static int coinMultiplier = 1;
    int coinStopper = 0;

    boolean inGame = true;

    final int OBSTSIZE = 50;

    private int clearSwitcher = 0;


    //window vars
    private final int MAX_FPS; //maximum refresh rate
    public  final int WIDTH; //window width
    public final int HEIGHT; //window height

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

    public static Vector characterPosition;
    Vector characterVelocity;
    Vector angle;
    float push;
    float friction = 0.998f;
    static int characterSize;

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
        //initializes window characterSize
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

        characterPosition = new Vector(400,200);
        characterVelocity = new Vector(0, 0);
        angle = new Vector(0,0);
        characterSize = 10;
        push = 500;
    }

    /*
    /*
     * update()
     * updates all relevant game variables before the frame draws
     */

    public static boolean checkCollision(Vector characterPosition, Rectangle movingRect, int OBSTSIZE, int characterSize) {
        if (
                characterPosition.x < movingRect.position.x + OBSTSIZE &&
                        characterPosition.x + characterSize > movingRect.position.x &&
                        characterPosition.y < movingRect.position.y + OBSTSIZE &&
                        characterPosition.y + characterSize > movingRect.position.y
                ) {
             coins+= coinMultiplier;
             return false;
        } else {
            return true;
        }
    }

    private void update(){
        if(inGame) {
            numFrames++;
            //update current fps
            fps = (int) (1f / dt);
            handelKeys();
            //bounce off walls
            if (characterPosition.x + characterSize > WIDTH  || characterPosition.x < 88) {
                characterVelocity.setX(characterVelocity.x *= -1);
                angle = new Vector(0, 0);
            }
            if (characterPosition.y + characterSize > HEIGHT || characterPosition.y < 30) {
                characterVelocity.setY(characterVelocity.y *= -1);
                angle = new Vector(0, 0);
            }

            if(getAgeInSeconds() == 15) {
                spawnFrequency = 400;
            }
            if(getAgeInSeconds() == 20) {
                spawnFrequency = 300;
            }
            if(getAgeInSeconds() == 30) {
                spawnFrequency = 200;
            }
            if(getAgeInSeconds() == 40) {
                spawnFrequency = 100;
            }
            if(getAgeInSeconds() == 60) {
                spawnFrequency = 50;
            }
            if(getAgeInSeconds() == 85) {
                spawnFrequency = 25;
            }
            characterVelocity.add(Vector.mult(angle, dt));
            characterPosition.add(Vector.mult(characterVelocity, dt));

        }

    }

    /*
     * draw()
     * gets the canvas (Graphics2D) and draws all elements
     * disposes canvas and then flips the buffer
     */
   ArrayList rectangles = new ArrayList();
        public void createNewRectangle(Graphics2D g){
            if(clearSwitcher % 2 == 0) {
                rectangles.add(new Rectangle(new Vector((int) (Math.random() * 534 + 88), 0), new Vector(0, ENEMYSPEED), new Vector(OBSTSIZE, OBSTSIZE), new Color((int) (Math.random() * 0x1000000)), true, (int) (Math.random() * 5) + 1));
                clearSwitcher++;
            } else {
                rectangles.add(new Rectangle(new Vector(88, (int)(Math.random() * 750)), new Vector(3, 0), new Vector(OBSTSIZE, OBSTSIZE), new Color((int) (Math.random() * 0x1000000)), false, (int) (Math.random() * 5) + 1));
                clearSwitcher++;
            }


        }
    private final long createdMillis = System.currentTimeMillis();

    public int getAgeInSeconds() {
        long nowMillis = System.currentTimeMillis();
        return (int)((nowMillis - this.createdMillis) / 1000);
    }

        private void draw() {
        //get canvas
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

        if(inGame) {
            g.clearRect(0, 0, WIDTH, HEIGHT);
            if(numFrames % spawnFrequency == 0) {
                createNewRectangle(g);
            }




            for(int i = 0; i < rectangles.size(); i++) {
                Rectangle rect = (Rectangle) rectangles.get(i);
                rect.update((int)dt,rect, g, rect.movesVert);
            }

            //Rectangle rect = (Rectangle) rectangles.get(1);
            //rect.update((int)dt, rect, g, rect.movesVert);



            g.setColor(Color.GREEN);
            g.drawString(Long.toString(fps), 10, 40);
            g.setColor(Color.yellow);
            g.drawString("Coins: " + coins, 10, 60);
            g.setColor(Color.cyan);
            g.fillRect(characterPosition.ix, characterPosition.iy, characterSize, characterSize);
            firstTime = false;
            //release resources, show the buffer
            g.setColor(Color.cyan);
            g.fillRect(0,100,88,HEIGHT - 100);
        } else {
            g.setFont(new Font("", Font.PLAIN,75));
            g.setColor(Color.white);
            g.drawString("PAUSE", WIDTH / 2 - 175  , HEIGHT/ 2);
        }
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
        switch (keyEvent.getKeyCode()) {

            case KeyEvent.VK_ESCAPE:
                if(inGame) {
                    inGame = false;
                    coinStopper = coinMultiplier;
                    coinMultiplier = 0;
                } else {
                    inGame = true;
                    coinMultiplier = coinStopper;

                }
                System.out.println("Not in game");
                break;

            case KeyEvent.VK_W:
                angle = Vector.unit2D((float)Math.toRadians(-90));
                angle.mult(push);
                break;

            case KeyEvent.VK_D:
                angle = Vector.unit2D(0);
                angle.mult(push);
                break;
            case KeyEvent.VK_A:
                angle = Vector.unit2D((float)Math.toRadians(180));
                angle.mult(push);
                break;

            case KeyEvent.VK_S:
                angle = Vector.unit2D((float)Math.toRadians(90));
                angle.mult(push);
                break;

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
        while(isRunning) {
            //new loop, clock the start
            startFrame = System.currentTimeMillis();




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
        Game game = new Game(1066, 800, 60);
        game.run();
    }


}





/*
vertRectNeeded1 = checkCollision(characterPosition, vertMovingRect1, OBSTSIZE, characterSize);
horizRectNeeded1 = checkCollision(characterPosition, horizMovingRect1, OBSTSIZE, characterSize);

horizMovingRect1.x+=3;
vertMovingRect1.y+=3;

if (horizMovingRect1.x + OBSTSIZE > WIDTH || horizMovingRect1.x < 0 || firstTime) {
                horizMovingRect1.x = 88;
                horizMovingRect1.y = (float) Math.random() * 550;
                System.out.println("Horizontal rectangle created");
            }
            if (horizRectNeeded1) {
                g.setColor(Color.GREEN);
                g.drawRect((int) horizMovingRect1.x, (int) horizMovingRect1.y, OBSTSIZE, OBSTSIZE);
            } else {
                horizMovingRect1.x = 88;
                horizMovingRect1.y = (float) Math.random() * 550;
                horizRectNeeded1 = true;
            }



            if (vertMovingRect1.y + OBSTSIZE > HEIGHT || vertMovingRect1.y < 0 || firstTime) {
                vertMovingRect1.x = (float) Math.random() * (WIDTH - 88 - 50)+ 88;
                vertMovingRect1.y = 0;
                System.out.println("vertical rectangle created");
            }
            if (vertRectNeeded1) {
                g.setColor(Color.MAGENTA);
                g.fillRect((int) vertMovingRect1.x, (int) vertMovingRect1.y, OBSTSIZE, OBSTSIZE);
            } else {
                vertMovingRect1.x = (float) Math.random() * (WIDTH - 88 - 50) + 88;
                vertMovingRect1.y = 0;
                vertRectNeeded1 = true;
            }








 */
