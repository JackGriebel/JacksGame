import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Game extends JFrame implements KeyListener{
    boolean firstTime = true;
    boolean vertRectNeeded1 = true;
    boolean horizRectNeeded1 = true;
    boolean newRectsNeeded = true;

    boolean needNewHalfRects;

    final int ENEMYSPEED = 3;

    int coins = 0;
    int coinMultiplier = 1;
    int coinStopper = 0;

    boolean inGame = true;

    final int OBSTSIZE = 150;

    Vector vertMovingRect1 = new Vector((int)Math.random() * 800,0);
    Vector horizMovingRect1 = new Vector(0,(int)Math.random() * 800);
    Vector horizMovingRect2 = new Vector(0,(int)Math.random() * 800);
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

    Vector characterPosition;
    Vector characterVelocity;
    Vector angle;
    float push;
    float friction = 0.998f;
    int characterSize;

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

    private boolean checkCollision(Vector characterPosition, Vector movingRect, int OBSTSIZE, int characterSize) {
        if (
                characterPosition.x < movingRect.x + OBSTSIZE &&
                        characterPosition.x + characterSize > movingRect.x &&
                        characterPosition.y < movingRect.y + OBSTSIZE &&
                        characterPosition.y + characterSize > movingRect.y
                ) {
             coins+= coinMultiplier;
             return false;
        } else {
            return true;
        }
    }

    private void update(){
        if(inGame) {

            /*
            horizMovingRect1.x+=3;
            vertMovingRect1.y+=3;


            vertRectNeeded1 = checkCollision(characterPosition, vertMovingRect1, OBSTSIZE, characterSize);
            horizRectNeeded1 = checkCollision(characterPosition, horizMovingRect1, OBSTSIZE, characterSize);

            */

            //update current fps
            fps = (int) (1f / dt);
            handelKeys();
            //bounce off walls
            if (characterPosition.x + characterSize > WIDTH  || characterPosition.x < 88) {
                characterVelocity.setX(characterVelocity.x *= -1);
                angle = new Vector(0, 0);
            }
            if (characterPosition.y + characterSize > HEIGHT || characterPosition.y < 0) {
                characterVelocity.setY(characterVelocity.y *= -1);
                angle = new Vector(0, 0);
            }

            characterVelocity.add(Vector.mult(angle, dt));
            //characterVelocity.mult(friction);
            characterPosition.add(Vector.mult(characterVelocity, dt));
            //x+=x * dt;
            //y+=y * dt;
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(Color.white);
            g.drawString("ADSFASDFA", WIDTH / 2, HEIGHT / 2);
        }

    }

    /*
     * draw()
     * gets the canvas (Graphics2D) and draws all elements
     * disposes canvas and then flips the buffer
     */
    //TODO fix everything
    public void handleRectangle(boolean rectangleNeeded, int size, Vector rectangle, Graphics2D g, boolean movesVert, int rectSize, int rectSpeed, int rectangleLevel) {
        if (!checkCollision(characterPosition, rectangle, size, characterSize)) {
            rectangleNeeded = false;
            System.out.println("Collision detected");
        }
        if (!movesVert) {
            rectangle.x += ENEMYSPEED;
            if (rectangle.x + size > WIDTH || rectangle.x < 0 || firstTime) {
                rectangle.x = 88;
                rectangle.y = (float) Math.random() * 550;
                System.out.println("Horizontal rectangle created");
            }
            if (rectangleNeeded) {
                g.setColor(Color.GREEN);
                g.drawRect((int) rectangle.x, (int) rectangle.y, size, size);
            } else {
                if(rectangleLevel < 4) {
                    needNewHalfRects = true;
                   // handleRectangle(true, OBSTSIZE / 2, new Vector(100, 100), g, true, (int) (OBSTSIZE / 1.5), (int) (rectSpeed * 1.5), rectangleLevel++);
                } else {
                    newRectsNeeded = true;
                }
            }

        } else {
            rectangle.y += ENEMYSPEED;
            if (rectangle.y + size > HEIGHT || rectangle.y < 0 || firstTime) {
                rectangle.x = (float) Math.random() * (WIDTH - 88 - 50) + 88;
                rectangle.y = 0;
                System.out.println("vertical rectangle created");
            }
            if (rectangleNeeded) {
                g.setColor(Color.MAGENTA);
                g.drawRect((int) rectangle.x, (int) rectangle.y, size, size);
            } else {
                if(rectangleLevel < 4) {
                    handleRectangle(true, OBSTSIZE / 2, new Vector(100, 100), g, true, (int) (OBSTSIZE / 1.5), (int) (rectSpeed * 1.5), rectangleLevel++);
                } else {
                    newRectsNeeded = true;
                }
            }

        }
    }

        private void draw() {
        //get canvas
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();



        //clear screen

        if(inGame) {
            //draw fps
           // if (clearSwitcher % 2 == 0)
                g.clearRect(0, 0, WIDTH, HEIGHT);

            g.setColor(Color.GREEN);
            g.drawString(Long.toString(fps), 10, 40);

            g.setColor(Color.yellow);
            g.drawString("Coins: " + coins, 10, 60);

            g.setColor(Color.cyan);
            g.fillRect(characterPosition.ix, characterPosition.iy, characterSize, characterSize);

           // if(newRectsNeeded) {
            handleRectangle(newRectsNeeded, OBSTSIZE, horizMovingRect1, g, false, OBSTSIZE, ENEMYSPEED, 1);
            handleRectangle(newRectsNeeded, OBSTSIZE, vertMovingRect1, g, true, OBSTSIZE, ENEMYSPEED, 1);
            handleRectangle(true, OBSTSIZE / 2, new Vector(100, 100), g, true, (int) (OBSTSIZE / 1.5), (int) (ENEMYSPEED * 1.5), 2);
          //  newRectsNeeded = false;
              //  }



            /*
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
            firstTime = false;
            //release resources, show the buffer
            g.setColor(Color.cyan);
            g.fillRect(0,100,88,HEIGHT - 100);
        } else {

            //g.setColor(Color.BLACK);
            //g.fillRect(0, 0, 800, 600);
            g.setFont(new Font("", Font.PLAIN,75));
            g.setColor(Color.white);
            g.drawString("PAUSE", WIDTH / 2 - 175  , HEIGHT/ 2);

            //g.setColor(Color.cyan);
            //g.drawRect(100, 100, 100, 60);
            //g.setFont(new Font("", Font.PLAIN,15));
            //g.drawString("Press one to buy: ", 100, 150);

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


            clearSwitcher++;
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
