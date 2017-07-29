import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends JFrame implements KeyListener {
    //TODO make door upgrades (ask mike)
    boolean firstTime = true;
    long numFrames = 0;
    int spawnFrequency = 100; //lower is faster, should be a multiple of
    private boolean gameOver = false;
    boolean restartActive = false;
    int resetSwitcher = 0;

    boolean touchingWalls = false;

    private boolean needUp = false;
    private boolean needDown = false;
    private boolean needLeft = false;
    private boolean needRight = false;


    int playerSpeed = 5;
    int ENEMYSPEED = 3;
    int bulletSpeed = 3;

    static int coins = 300;
    static int coinMultiplier = 1;
    int coinStopper = 0;

    int upgradesPurchased1;
    int upgradesPurchased2;
    int upgradesPurchased3;
    int upgradesPurchased4;
    int upgradesPurchased5;
    int upgradesPurchased6;
    int upgradesPurchased7;


    boolean inGame = true;

    int obstSize = 50;

    private int clearSwitcher = 0;

    enum GAME_STATES {
        MENU,
        PLAY,
        PAUSE,
        GAMEOVER;
    }

    public GAME_STATES game_states = GAME_STATES.PLAY;


    //window vars
    private final int MAX_FPS; //maximum refresh rate
    public final int WIDTH; //window width
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

    public Game(int width, int height, int fps) {
        super("My Game");
        this.MAX_FPS = fps;
        this.WIDTH = width;
        this.HEIGHT = height;
    }


    /*
     * init()
     * initializes all variables needed before the window opens and refreshes
     */
    void init() {
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

        characterPosition = new Vector(400, 200);
        characterVelocity = new Vector(0, 0);
        angle = new Vector(0, 0);
        characterSize = 100;
        push = 0;
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
            return false;
        } else {
            return true;
        }
    }

    public void clearAll() {
        rectangles.clear();
        numFrames = 0;
        spawnFrequency = 500;
        coins = 0;
        coinMultiplier = 1;
        coinStopper = 0;
        clearSwitcher = 0;
        upgradesPurchased1 = 0;
        upgradesPurchased2 = 0;
        upgradesPurchased3 = 0;
        upgradesPurchased4 = 0;
        upgradesPurchased5 = 0;
        upgradesPurchased6 = 0;
        upgradesPurchased7 = 0;
    }

    private void update() {
        if (firstTime) {
            game_states = GAME_STATES.PLAY;
        }
        if (inGame) {
            numFrames++;
            switch (game_states) {
                case MENU:
                    break;
                case PLAY:
                    break;
                case PAUSE:
                    break;
                case GAMEOVER:
                    break;
            }
            //update current fps
            fps = (int) (1f / dt);
            handelKeys();
            //bounce off walls
            if (characterPosition.x + characterSize > WIDTH) {
                characterPosition.x -= 1;
                touchingWalls = true;
            }
            if (characterPosition.x < 88) {
                characterPosition.x += 1;
                touchingWalls = true;
            }
            if (characterPosition.y + characterSize > HEIGHT) {
                characterPosition.y -= 1;
                touchingWalls = true;
            }
            if (characterPosition.y < 30) {
                characterPosition.y += 1;
                touchingWalls = true;
            }

            if (getAgeInSeconds() == 10) {
                spawnFrequency = 400;
                System.out.println("Spawn frequency level 2");
            }
            if (getAgeInSeconds() == 15) {
                spawnFrequency = 300;
                coinMultiplier++;
                System.out.println("Spawn frequency level 3");
            }
            if (getAgeInSeconds() == 20) {
                spawnFrequency = 200;
                System.out.println("Spawn frequency level 4");
            }
            if (getAgeInSeconds() == 30) {
                spawnFrequency = 100;
                coinMultiplier++;
                System.out.println("Spawn frequency level 5");
            }
            if (getAgeInSeconds() == 40) {
                spawnFrequency = 50;
                System.out.println("Spawn frequency level 6");
            }
            if (getAgeInSeconds() == 50) {
                spawnFrequency = 25;
                coinMultiplier++;
                System.out.println("Spawn frequency level 7");
            }
            if (getAgeInSeconds() == 60) {
                spawnFrequency = 10;
                System.out.println("Spawn frequency level 8");
            }
            if (getAgeInSeconds() == 80) {
                spawnFrequency = 5;
                coinMultiplier++;
                System.out.println("MAX MAX MAX Spawn frequency level 9 MAX MAX MAX");
            }
            characterVelocity.add(Vector.mult(angle, dt));
            characterPosition.add(Vector.mult(characterVelocity, dt));
            if (gameOver == true) game_states = GAME_STATES.GAMEOVER;

        }

    }

    /*
     * draw ()
     * gets the canvas (Graphics2D) and draws all elements
     * disposes canvas and then flips the buffer
     */
    ArrayList rectangles = new ArrayList();

    public void createNewRectangle(Graphics2D g) {
        if (clearSwitcher % 2 == 0) {
            rectangles.add(new Rectangle(new Vector((int) (Math.random() * 534 + 88), 0), new Vector(0, ENEMYSPEED), new Vector(obstSize, obstSize), new Color((int) (Math.random() * 0x1000000)), true, (int) (Math.random() * 5) + 1, false));
            clearSwitcher++;
        } else {
            rectangles.add(new Rectangle(new Vector(88, (int) (Math.random() * 750)), new Vector(3, 0), new Vector(obstSize, obstSize), new Color((int) (Math.random() * 0x1000000)), false, (int) (Math.random() * 5) + 1, false));
            clearSwitcher++;
        }


    }

    public double getAgeInSeconds() {
        return numFrames / 60.0;
    }

    private BufferedImage createTexture(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void drawShop() {
        buy1();
        buy2();
        buy3();
        buy4();
        buy5();
        buy6();
        buy7();
    }

    private void purchaseUpgrades(int upgradeType) {
        if (upgradeType == 1) {
            if (coins >= checkCoinPrice(upgradesPurchased1)) {
                coins -= checkCoinPrice(upgradesPurchased1);
                upgradesPurchased1++;
            }
        }
        if (upgradeType == 2) {
            if (coins >= checkCoinPrice(upgradesPurchased2)) {
                coins -= checkCoinPrice(upgradesPurchased2);
                upgradesPurchased2++;
            }
        }
        if (upgradeType == 3) {
            if (coins >= checkCoinPrice(upgradesPurchased3)) {
                coins -= checkCoinPrice(upgradesPurchased3);
                upgradesPurchased3++;
                coinMultiplier++;
                System.out.println("CoinMultiplier increased from purchase, is now " + coinMultiplier);
            }
        }
        if (upgradeType == 4) {
            if (coins >= checkCoinPrice(upgradesPurchased4)) {
                coins -= checkCoinPrice(upgradesPurchased4);
                upgradesPurchased4++;
                obstSize -= 10;
            }
        }
        if (upgradeType == 5) {
            if (coins >= checkCoinPrice(upgradesPurchased5)) {
                coins -= checkCoinPrice(upgradesPurchased5);
                upgradesPurchased5++;
            }
        }
        if (upgradeType == 6) {
            if (coins >= checkCoinPrice(upgradesPurchased6)) {
                coins -= checkCoinPrice(upgradesPurchased6);
                upgradesPurchased6++;
            }
        }
        if (upgradeType == 7) {
            if (coins >= checkCoinPrice(upgradesPurchased7)) {
                coins -= checkCoinPrice(upgradesPurchased7);
                upgradesPurchased7++;
            }
        }
    }

    private void buy1() {
        if (upgradesPurchased1 == 0) characterSize = 100;
        else if (upgradesPurchased1 == 1) characterSize = 80;
        else if (upgradesPurchased1 == 2) characterSize = 60;
        else if (upgradesPurchased1 == 3) characterSize = 40;
        else if (upgradesPurchased1 == 4) characterSize = 20;
        else if (upgradesPurchased1 == 5) characterSize = 5;

    }

    private void buy2() {
        if (upgradesPurchased2 == 0) playerSpeed = 5;
        else playerSpeed = 5 + upgradesPurchased2;
    }

    private void buy3() {
        //all upgrades handled inside of the purchase upgrades method
    }

    private void buy4() {
        //all upgrades handled inside of the purchase upgrades method
    }

    private void buy5() {

    }

    private void buy6() {

    }

    private void buy7() {

    }

    private void draw() {
        //get canvas
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

        switch (game_states) {
            case MENU:
                break;
            case PLAY:
                drawShop();
                Vector killBox = new Vector(WIDTH / 2 - 40, HEIGHT / 2 - 40);
                if(needRight) {
                    rectangles.add(shoot(1));
                    needRight = false;
                }
                if(needUp) {
                    rectangles.add(shoot(2));
                    needUp = false;
                }
                if(needLeft) {
                    rectangles.add(shoot(3));
                    needLeft = false;
                }
                if(needDown) {
                    rectangles.add(shoot(4));
                    needDown = false;
                }
                if (
                        (characterPosition.x < killBox.x + 80 &&
                                characterPosition.x + characterSize > killBox.x &&
                                characterPosition.y < killBox.y + 80 &&
                                characterPosition.y + characterSize > killBox.y)
                        ) {
                    gameOver = true;
                }
                g.clearRect(0, 0, WIDTH, HEIGHT);
                if (numFrames % spawnFrequency == 0) {
                    createNewRectangle(g);
                }
                if (numFrames % 60 == 0) {
                    coins += coinMultiplier;
                }
                //g.setColor(Color.GREEN);
                // g.fillRect(WIDTH / 2 - 40, HEIGHT / 2 - 40, 80, 80);

                for (int k = 150; k >= 5; k -= 5) {
                    g.drawOval(WIDTH / 2 - k / 2, HEIGHT / 2 - k / 2, k, k);
                }
                inGame = true;
                for (int i = 0; i < rectangles.size(); i++) {
                    Rectangle rect = (Rectangle) rectangles.get(i);
                    if(rect.isBullet) {
                        for (int k = 0; k < rectangles.size(); k++) {
                            Rectangle interceptRect = (Rectangle) rectangles.get(k);
                            if(!checkCollision(rect.position, interceptRect, (int)interceptRect.size.x, (int)rect.size.x)) {
                                rect.rectangleAlive = false;
                                System.out.println("Rectangle killed");
                            }
                        }
                    }
                    rect.update((int) dt, rect, g, rect.movesVert, obstSize);
                    if(!rect.isBullet) {
                        if(!checkCollision(characterPosition, rect, (int) rect.size.x, characterSize)) {
                            if (rect.rectangleAlive) {
                                gameOver = true;
                                //characterPosition.set((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT));
                            }
                        }
                    }
                }

                g.setColor(Color.GREEN);
                g.drawString(Long.toString(fps), 10, 40);
                g.setColor(Color.yellow);
                g.drawString("Coins: " + coins, 10, 60);
                g.setColor(Color.cyan);
                g.fillRect(characterPosition.ix, characterPosition.iy, characterSize, characterSize);
                firstTime = false;
                //release resources, show the buffer
                g.setColor(Color.BLACK);
                g.fillRect(0, 100, 88, HEIGHT - 100);

                break;
            case PAUSE:
                g.setFont(new Font("", Font.PLAIN, 75));
                g.setColor(Color.white);
                g.drawString("PAUSE", WIDTH / 2 - 175, HEIGHT / 2);
                System.out.println("in pause");
                break;
            case GAMEOVER:
                g.setColor(Color.black);
                g.fillRect(0, 0, WIDTH, HEIGHT);
                //g.drawImage(createTexture("C:\\Users\\IGMAdmin\\JacksGame\\images\\Game_Over.jpg"), 0,0, WIDTH, HEIGHT, null);
                g.setFont(new Font("", Font.PLAIN, 75));
                g.setColor(Color.WHITE);
                g.drawString("Game Over", WIDTH / 2 - 200, HEIGHT / 2);
                if (numFrames % 50 == 0) {
                    resetSwitcher++;
                }
                if (resetSwitcher % 2 == 0) {
                    g.setFont(new Font("", Font.PLAIN, 50));
                    g.drawString("Press R to Restart", WIDTH / 2 - 207, HEIGHT / 2 + 100);
                }
                break;
        }
        g.dispose();
        strategy.show();
    }
    private void needShoot(int direction) {
        if(direction == 1) {
            needRight = true;
        }
        if(direction == 2) {
            needUp = true;
        }
        if(direction == 3) {
            needLeft = true;
        }
        if(direction == 4) {
            needDown = true;
        }
    }
    public Rectangle shoot(int direction) {
        Vector spawnPos;
        if(direction == 1) {
            spawnPos = new Vector(characterPosition.x + characterSize, (float)(characterPosition.y + (0.5 * characterSize)));
            return new Rectangle(spawnPos, new Vector(bulletSpeed, 0), new Vector(10, 10), Color.RED, false, 8, true);
        }
        else if(direction == 2) {
            spawnPos = new Vector((float)(characterPosition.x + (0.5 * characterSize)), characterPosition.y );
            return new Rectangle(spawnPos, new Vector(0, -bulletSpeed), new Vector(10, 10), Color.RED, false, 0, true);
        }
        else if(direction == 3) {
            spawnPos = new Vector(characterPosition.x , (float)(characterPosition.y + (0.5 * characterSize)));
            return new Rectangle(spawnPos, new Vector(-bulletSpeed, 0), new Vector(10, 10), Color.RED, false, 0, true);
        }
        else {
            spawnPos = new Vector( (float)(characterPosition.x + (0.5 * characterSize)), characterPosition.y + characterSize);
            return new Rectangle(spawnPos, new Vector(0, bulletSpeed), new Vector(10, 10), Color.RED, false, 0, true);
        }

    }

    private void handelKeys() {
        if (!touchingWalls) {
            for (int i = 0; i < keys.size(); i++) {
                switch (keys.get(i)) {
                    case KeyEvent.VK_W:
                        characterPosition.add(new Vector(0, -playerSpeed));
                        break;

                    case KeyEvent.VK_D:
                        characterPosition.add(new Vector(playerSpeed, 0));
                        break;
                    case KeyEvent.VK_A:
                        characterPosition.add(new Vector(-playerSpeed, 0));
                        break;

                    case KeyEvent.VK_S:
                        characterPosition.add(new Vector(0, playerSpeed));
                        break;

                }
            }

        } else {
            System.out.println("hit wall");
            touchingWalls = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (!keys.contains(keyEvent.getKeyCode()))
            keys.add(keyEvent.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        for (int i = keys.size() - 1; i >= 0; i--) {
            if (keyEvent.getKeyCode() == keys.get(i))
                keys.remove(i);
        }
        switch (keyEvent.getKeyCode()) {

            case KeyEvent.VK_ESCAPE:
                if (inGame) {
                    inGame = false;
                    coinStopper = coinMultiplier;
                    coinMultiplier = 0;
                } else {
                    inGame = true;
                    coinMultiplier = coinStopper;

                }
                break;
            case KeyEvent.VK_R:
                if (gameOver) {
                    game_states = GAME_STATES.PLAY;
                    clearAll();
                    characterPosition.set((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT));
                    gameOver = false;
                }
                break;
            case KeyEvent.VK_1:
                purchaseUpgrades(1);
                break;
            case KeyEvent.VK_2:
                purchaseUpgrades(2);
                break;
            case KeyEvent.VK_3:
                purchaseUpgrades(3);
                break;
            case KeyEvent.VK_4:
                purchaseUpgrades(4);
                break;
            case KeyEvent.VK_5:
                purchaseUpgrades(5);
                break;
            case KeyEvent.VK_6:
                purchaseUpgrades(6);
                break;
            case KeyEvent.VK_7:
                purchaseUpgrades(7);
                break;
            case KeyEvent.VK_RIGHT:
                needShoot(1);
                break;
            case KeyEvent.VK_UP:
                needShoot(2);
                break;
            case KeyEvent.VK_LEFT:
                needShoot(3);
                break;
            case KeyEvent.VK_DOWN:
                needShoot(4);
                break;

        }
    }

    private int checkCoinPrice(int upgradesPurchased) {
        int price = upgradesPurchased * 5 + 5;
        if (price == 0) {
            price = 5;
        }
        return price;
    }

    /*
         * run()
         * calls init() to initialize variables
         * loops using isRunning
            * updates all timing variables and then calls update() and draw ()
            * dynamically sleeps the main thread to maintain angle framerate close to target fps
         */
    public void run() {
        init();
        while (isRunning) {
            //new loop, clock the start
            startFrame = System.currentTimeMillis();


            //calculate delta time
            dt = (float) (startFrame - lastFrame) / 1000;

            //update lastFrame for next dt
            lastFrame = startFrame;

            //call update and draw methods
            update();
            draw();

            //dynamic thread sleep, only sleep the time we need to cap the framerate
            //rest = (max fps sleep time) - (time it took to execute this frame)
            rest = (1000 / MAX_FPS) - (System.currentTimeMillis() - startFrame);
            if (rest > 0) { //if we stayed within frame "budget", sleep away the rest of it
                try {
                    Thread.sleep(rest);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    //entry point for application
    public static void main(String[] args) {
        Game game = new Game(1066, 800, 60);
        game.run();
    }


}
