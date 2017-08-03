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
    boolean firstTime = true;
    long numFrames = 0;
    int spawnFrequency = 100; //lower is faster, should be a multiple of
    private boolean gameOver = false;
    boolean restartActive = false;
    int resetSwitcher = 0;

   int xCoinPosition = (int) ((Math.random() * 850) + 750);
    int yCoinPosition = (int) ((Math.random() * 520) + 200);

    boolean needNewCoin = false;

    Vector killBox = new Vector(1760 / 2 - 40, 990 / 2 - 40);

    int lives = 2;

    int score = 10;

    boolean trueFirstTime = true;

    final int NUMSTARS = 200;
    final int NUMGALAXIES = 10;
    final int OGCHARACTERSIZE = 100;
    int[] randomStarPosWidth = new int[NUMSTARS];
    int[] randomStarPosHeight = new int[NUMSTARS];

    boolean inPause = true;

    int[] randomGalaxyPosWidth = new int[NUMGALAXIES];
    int[] randomGalaxyPosHeight = new int[NUMGALAXIES];

    int rectType = 0;

    int direction = 1;
    boolean movingD = false;
    boolean movingW = true;
    boolean movingA = false;
    boolean movingS = false;

    String lastMoved = "w";

    boolean touchingWalls = false;


    private boolean needUp = false;
    private boolean needDown = false;
    private boolean needLeft = false;
    private boolean needRight = false;


    int playerSpeed = 6;
    int ENEMYSPEED = 3;
    int bulletSpeed = 8;

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


    boolean inGame = false;

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

    public static Vector characterPosition = new Vector(0, 0);
    Vector characterVelocity;
    Vector angle;
    float push;
    float friction = 0.998f;
    int characterSize;

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
        lives = 2;
        numFrames = 0;
        spawnFrequency = 500;
        coins = 10;
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
        obstSize = 50;
        firstTime = true;
        characterSize = OGCHARACTERSIZE;
        playerSpeed = 6;
    }
    //used for when a life is lost
    public void babyClearAll() {
        try {
            System.out.println("Sleeping...");
            Thread.sleep(100);
            System.out.println("Done sleeping, no interrupt.");
        } catch (InterruptedException e) {
            System.out.println("I was interrupted!");
            e.printStackTrace();
        }
        rectangles.clear();
    }

    private void update() {
        if (inPause) {
            if (characterPosition.x + characterSize > WIDTH - 60) {
                characterPosition.x -= 1;
                touchingWalls = true;
            }
            if (characterPosition.x < 750) {
                characterPosition.x += 1;
                touchingWalls = true;
            }
            if (characterPosition.y + characterSize > HEIGHT - 220) {
                characterPosition.y -= 1;
                touchingWalls = true;
            }
            if (characterPosition.y < 150) {
                characterPosition.y += 1;
                touchingWalls = true;
            }
            handelKeys();
            numFrames++;
        }
        if (inGame) {
            numFrames++;

            //update current fps
            fps = (int) (1f / dt);
            handelKeys();
            //bounce off walls
            if (characterPosition.x + characterSize > WIDTH) {
                characterPosition.x -= 1;
                touchingWalls = true;
            }
            if (characterPosition.x < 0) {
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

            if (getAgeInSeconds() == 5) {
                spawnFrequency = 300;
                System.out.println("Spawn frequency level 2");
            }
            if (getAgeInSeconds() == 10) {
                spawnFrequency = 200;
                coinMultiplier++;
                System.out.println("Spawn frequency level 3");
            }
            if (getAgeInSeconds() == 15) {
                spawnFrequency = 100;
                System.out.println("Spawn frequency level 4");
            }
            if (getAgeInSeconds() == 25) {
                spawnFrequency = 50;
                coinMultiplier++;
                System.out.println("Spawn frequency level 5");
            }
            if (getAgeInSeconds() == 35) {
                spawnFrequency = 25;
                System.out.println("Spawn frequency level 6");
            }
            if (getAgeInSeconds() == 45) {
                spawnFrequency = 10;
                coinMultiplier++;
                System.out.println("Spawn frequency level 7");
            }
            if (getAgeInSeconds() == 55) {
                spawnFrequency = 5;
                System.out.println("Spawn frequency level 8");
            }
            if (getAgeInSeconds() == 70) {
                spawnFrequency = 1;
                coinMultiplier++;
                System.out.println("MAX MAX MAX Spawn frequency level 9 MAX MAX MAX");
            }
            characterVelocity.add(Vector.mult(angle, dt));
            characterPosition.add(Vector.mult(characterVelocity, dt));

        }

    }

    /*
     * draw ()
     * gets the canvas (Graphics2D) and draws all elements
     * disposes canvas and then flips the buffer
     */
    ArrayList rectangles = new ArrayList();


    public void createNewRectangle(Graphics2D g) {
        ArrayList colors = new ArrayList();
        colors.add(new Color(255, 106, 0));
        colors.add(new Color(0, 235, 255));
        colors.add(new Color(255, 242, 0));
        colors.add(new Color(67, 255, 82));
        colors.add(new Color(255, 0, 209));
        colors.add(new Color(0, 145, 255));
        colors.add(new Color(136, 0, 255));
        int arrayPicker = (int) (Math.random() * colors.size());
        //spawns top then left then right then bottom
        if (clearSwitcher == 0) {
            rectangles.add(new Rectangle(new Vector((int) (Math.random() * WIDTH - 50), 0), new Vector(0, ENEMYSPEED), new Vector(obstSize, obstSize), (Color) colors.get(arrayPicker), true, (int) (Math.random() * 5) + 1, false, rectType, 0, false));
            rectType++;
            clearSwitcher++;
        } else if(clearSwitcher == 1){
            rectangles.add(new Rectangle(new Vector(0, (int) (Math.random() * HEIGHT - 50)), new Vector(ENEMYSPEED, 0), new Vector(obstSize, obstSize), (Color) colors.get(arrayPicker), false, (int) (Math.random() * 5) + 1, false, rectType, 0, false));
            rectType++;
            clearSwitcher++;
        } else if(clearSwitcher == 2) {
            rectangles.add(new Rectangle(new Vector(WIDTH - 50, (int) (Math.random() * HEIGHT - 50)), new Vector(-ENEMYSPEED, 0), new Vector(obstSize, obstSize), (Color) colors.get(arrayPicker), false, (int) (Math.random() * 5) + 1, false, rectType, 0, false));
            rectType++;
            clearSwitcher++;
        } else if(clearSwitcher == 3) {
            rectangles.add(new Rectangle(new Vector((int) (Math.random() * 534), HEIGHT - 50), new Vector(0, -ENEMYSPEED), new Vector(obstSize, obstSize), (Color) colors.get(arrayPicker), true, (int) (Math.random() * 5) + 1, false, rectType, 0, false));
            rectType++;
            clearSwitcher++;
        }
        if(clearSwitcher > 3) {
            clearSwitcher = 0;
        }
        if (rectType > 5) {
            rectType = 0;
        }


    }

    public double getAgeInSeconds() {
        return numFrames / 60.0;
    }

    static BufferedImage createTexture(String path) {
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
            if ((coins >= checkCoinPrice(upgradesPurchased1)) && characterSize > 20) {
                coins -= checkCoinPrice(upgradesPurchased1);
                upgradesPurchased1++;
                characterSize -= 20;
            }
        }
        if (upgradeType == 2) {
            if (coins >= checkCoinPrice(upgradesPurchased2)) {
                coins -= checkCoinPrice(upgradesPurchased2);
                upgradesPurchased2++;
                playerSpeed += upgradesPurchased2;
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
            if (coins >= checkCoinPrice(upgradesPurchased4) && upgradesPurchased4 < 4) {
                if (obstSize > 10) {
                    coins -= checkCoinPrice(upgradesPurchased4);
                    upgradesPurchased4++;
                    obstSize -= 10;
                }
            }
        }
        if (upgradeType == 5) {
            if (coins >= 50 && upgradesPurchased5 == 0) {
                coins -= 50;
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
            }
        }
    }

    private void buy1() {
        /*
        if (upgradesPurchased1 == 0) characterSize = 100;
        else if (upgradesPurchased1 == 1) characterSize = OGCHARACTERSIZE - 20;
        else if (upgradesPurchased1 == 2) characterSize = OGCHARACTERSIZE - 40;
        else if (upgradesPurchased1 == 3) characterSize = OGCHARACTERSIZE - 60;
        else if (upgradesPurchased1 == 4) characterSize = OGCHARACTERSIZE - 80;
        else if (upgradesPurchased1 == 5) characterSize = OGCHARACTERSIZE - 90;
        */

    }

    private void buy2() {

    }

    private void buy3() {
        //all upgrades handled inside of the purchase upgrades method
    }

    private void buy4() {
        //all upgrades handled inside of the purchase upgrades method
    }

    private void buy5() {
        //all upgrades handled inside of the purchase upgrades method
    }

    private void buy6() {

    }

    private void buy7() {

    }

    private void killDirections() {
        if (movingD || movingW || movingA || movingS) {
            movingD = false;
            movingW = false;
            movingA = false;
            movingS = false;
        }
    }

    private void draw() {
        //get canvas
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
        if (firstTime) {
            characterPosition = new Vector(WIDTH - 200, HEIGHT - 200);
        }
        if (trueFirstTime) {
            characterPosition = new Vector(WIDTH - 200, HEIGHT - 400);
            trueFirstTime = false;
        }
        if (inGame) {
            drawShop();
            if (
                    (characterPosition.x < killBox.x + 80 &&
                            characterPosition.x + characterSize > killBox.x &&
                            characterPosition.y < killBox.y + 80 &&
                            characterPosition.y + characterSize > killBox.y)
                    ) {
                if(lives == 0) gameOver = true;
                else {
                    lives--;
                    babyClearAll();
                    characterPosition = new Vector(WIDTH - 200, HEIGHT - 200);

                }
            }
        }
        g.clearRect(0, 0, WIDTH, HEIGHT);
        if (inGame) {
            if (firstTime) {
                createNewRectangle(g);
                createNewRectangle(g);
            }
            if (numFrames % spawnFrequency == 0) {
                createNewRectangle(g);
            }

            for (int k = 150; k >= 5; k -= 5) {
                g.setColor(Color.white);
                g.drawOval(WIDTH / 2 - k / 2, HEIGHT / 2 - k / 2, k, k);
            }
            g.setColor(Color.YELLOW);
            g.drawOval(xCoinPosition, yCoinPosition, 15, 23);
            if (
                    characterPosition.x < xCoinPosition + 15 &&
                            characterPosition.x + characterSize > xCoinPosition &&
                            characterPosition.y < yCoinPosition + 23 &&
                            characterPosition.y + characterSize > yCoinPosition
                    ) {
                coins += 10;
                xCoinPosition = (int) ((Math.random() * WIDTH - 50) + 50);
                yCoinPosition = (int) ((Math.random() * HEIGHT - 50) + 50);
            }
            if (
                            killBox.x < xCoinPosition + 15 &&
                            killBox.x + 80 > xCoinPosition &&
                            killBox.y < yCoinPosition + 23 &&
                            killBox.y + 80 > yCoinPosition
                    ) {
                for(int i = 0; i < 1000; i++) {
                    System.out.println("coin position corrected");
                }
                xCoinPosition = (int) ((Math.random() * WIDTH - 100) + 50);
                yCoinPosition = (int) ((Math.random() * HEIGHT - 100) + 50);
            }
        }


        g.setColor(Color.GREEN);
        g.drawString(Long.toString(fps), 10, 40);
        g.setColor(Color.yellow);
        g.drawString("Coins: " + coins  + "  (+" + coinMultiplier + ")", 10, 60);
        if (upgradesPurchased5 > 0) {
            g.setColor(Color.white);
        }
        g.setColor(Color.white);
        for (int i = 0; i < NUMSTARS; i++) {
            g.fillRect(randomStarPosWidth[i], randomStarPosHeight[i], 2, 2);
        }


        killDirections();

        // g.fillRect(characterPosition.ix, characterPosition.iy, characterSize, characterSize);
        firstTime = false;
        //release resources, show the buffer
        g.setColor(Color.BLACK);
        if (gameOver) {
            g.setColor(Color.black);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setFont(new Font("", Font.PLAIN, 75));
            g.setColor(Color.WHITE);
            g.drawString("Game Over", WIDTH / 2 - 200, HEIGHT / 2);
            g.setFont(new Font("", Font.PLAIN, 40));
            if(score < 100) {
                g.drawString("Score " + score, WIDTH / 2 - 75, HEIGHT / 2 - 100);
            } else if(score < 1000) {
                g.drawString("Score " + score, WIDTH / 2 - 90, HEIGHT / 2 - 100);
            } else {
                g.drawString("Score " + score, WIDTH / 2 - 100, HEIGHT / 2 - 100);
            }

            if (numFrames % 50 == 0) {
                resetSwitcher++;
            }
            if (resetSwitcher % 2 == 0) {
                g.setFont(new Font("", Font.PLAIN, 50));
                g.drawString("Press R to Restart", WIDTH / 2 - 207, HEIGHT / 2 + 100);
            }
        } else if(!gameOver) {
            score = (int)(getAgeInSeconds() * 12) + (int)(Math.random() * 10);
        }
        if (inPause) {
            //J:\JacksGame\JacksGame\JacksGame\images\menu.png
            g.drawImage(createTexture("C:\\Users\\IGMAdmin\\JacksGame\\images\\menu.png"), 0, 25, WIDTH, HEIGHT, null);
            g.setColor(Color.white);
            //g.drawRect(450, 100, 576, 550);
            g.drawRect(750, 150, 950, 620);
            g.setColor(Color.yellow);
            g.drawString("Coins " + coins + "  (+" + coinMultiplier + ")", 760, 170);
            if (upgradesPurchased5 > 0) {
                g.setColor(Color.white);
            }
            for(int i = 0; i < rectangles.size(); i++) {
                Rectangle rect = (Rectangle)rectangles.get(i);
                if (rect.position.x + rect.size.x > 1700) {
                    rect.outOfBounds = true;
                }
                if (rect.position.x < 750) {
                    rect.outOfBounds = true;
                }
                if (rect.position.y + rect.size.x > 770) {
                    rect.outOfBounds = true;
                }
                if (rect.position.y < 150) {
                    rect.outOfBounds = true;
                }
            }
            if(needNewCoin) {
                xCoinPosition = (int) ((Math.random() * 850) + 750);
                yCoinPosition = (int) ((Math.random() * 520) + 200);
                needNewCoin = false;
            }
                g.setColor(Color.YELLOW);
                g.drawOval(xCoinPosition, yCoinPosition, 15, 23);
                if (
                        characterPosition.x < xCoinPosition + 15 &&
                                characterPosition.x + characterSize > xCoinPosition &&
                                characterPosition.y < yCoinPosition + 23 &&
                                characterPosition.y + characterSize > yCoinPosition
                        ) {
                    coins += 10;
                    needNewCoin = true;
                }



        }
        if ((inGame || inPause) && !gameOver) {
            if (numFrames % 60 == 0) {
                coins += coinMultiplier;
            }
            for (int i = 0; i < rectangles.size(); i++) {
                Rectangle rect = (Rectangle) rectangles.get(i);
                //cleaning up old rectangles
                if(rect.age > 600) {
                    if(!rect.rectangleAlive) {
                        rectangles.remove(i);
                    }

                }
                rect.age++;

                for (int k = 0; k < rectangles.size(); k++) {
                    Rectangle interceptRect = (Rectangle) rectangles.get(k);
                    if (interceptRect.isBullet) {
                        if (!checkCollision(rect.position, interceptRect, (int) interceptRect.size.x, (int) rect.size.x)) {
                            rect.rectangleAlive = false;
                        }
                    }
                }
                rect.update((int) dt, rect, g, rect.movesVert, obstSize);
                if (!rect.isBullet) {
                    if (!checkCollision(characterPosition, rect, (int) rect.size.x, characterSize)) {
                        if (rect.rectangleAlive) {
                            if(lives == 0) gameOver = true;
                            else {
                                lives--;
                                babyClearAll();
                                characterPosition = new Vector(WIDTH - 200, HEIGHT - 200);
                            }
                        }
                    }
                }
            }

        }
        g.setColor(Color.white);
        if ((inGame || inPause) && !gameOver) {
            g.drawRect((int) characterPosition.x, (int) characterPosition.y, characterSize, characterSize);
            g.setColor(Color.white);
            int moveOverLives = 20;
            for(int i = lives + 1; i != 0; i--) {
                g.drawRect(WIDTH - moveOverLives, 40, 10, 10);
                moveOverLives+=20;
            }
            moveOverLives+=20;
            g.drawString("Lives:", WIDTH - moveOverLives, 50);

        }

        if (inGame || inPause) {
            if (needRight) {
                rectangles.add(shoot(1));
                needRight = false;
            }
            if (needUp) {
                rectangles.add(shoot(2));
                needUp = false;
            }
            if (needLeft) {
                rectangles.add(shoot(3));
                needLeft = false;
            }
            if (needDown) {
                rectangles.add(shoot(4));
                needDown = false;
            }
        }
        g.dispose();
        strategy.show();
    }

    private void needShoot(int direction) {
        if (direction == 1) {
            needRight = true;
        }
        if (direction == 2) {
            needUp = true;
        }
        if (direction == 3) {
            needLeft = true;
        }
        if (direction == 4) {
            needDown = true;
        }
    }

    public Rectangle shoot(int direction) {
        Vector spawnPos;
        if (direction == 1) {
            spawnPos = new Vector(characterPosition.x + characterSize, (float) (characterPosition.y + (0.5 * characterSize)));
            return new Rectangle(spawnPos, new Vector(bulletSpeed, 0), new Vector(10, 10), Color.RED, false, 8, true, 100, 0, false);
        } else if (direction == 2) {
            spawnPos = new Vector((float) (characterPosition.x + (0.5 * characterSize)), characterPosition.y);
            return new Rectangle(spawnPos, new Vector(0, -bulletSpeed), new Vector(10, 10), Color.RED, false, 0, true, 100, 0, false);
        } else if (direction == 3) {
            spawnPos = new Vector(characterPosition.x, (float) (characterPosition.y + (0.5 * characterSize)));
            return new Rectangle(spawnPos, new Vector(-bulletSpeed, 0), new Vector(10, 10), Color.RED, false, 0, true, 100, 0, false);
        } else {
            spawnPos = new Vector((float) (characterPosition.x + (0.5 * characterSize)), characterPosition.y + characterSize);
            return new Rectangle(spawnPos, new Vector(0, bulletSpeed), new Vector(10, 10), Color.RED, false, 0, true, 100, 0, false);
        }

    }

    private void handelKeys() {
        if (!touchingWalls) {
            for (int i = 0; i < keys.size(); i++) {
                switch (keys.get(i)) {
                    case KeyEvent.VK_W:
                        direction = 2;
                        characterPosition.add(new Vector(0, -playerSpeed));
                        movingW = true;
                        lastMoved = "w";
                        break;

                    case KeyEvent.VK_S:
                        direction = 4;
                        characterPosition.add(new Vector(0, playerSpeed));
                        lastMoved = "s";
                        movingS = true;
                        break;

                    case KeyEvent.VK_D:
                        direction = 1;
                        characterPosition.add(new Vector(playerSpeed, 0));
                        lastMoved = "d";
                        movingD = true;
                        break;

                    case KeyEvent.VK_A:
                        direction = 3;
                        characterPosition.add(new Vector(-playerSpeed, 0));
                        lastMoved = "a";
                        movingA = true;
                        break;

                }
            }

        } else {
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
                clearAll();
                if (inGame) {
                    inGame = false;
                    inPause = true;
                    clearAll();
                } else if (inPause) {
                    inPause = false;
                    inGame = true;
                    xCoinPosition = (int)((Math.random() * WIDTH - 50) + 50);
                    yCoinPosition = (int)((Math.random() * HEIGHT - 50) + 50);
                    clearAll();
                }
                break;

            case KeyEvent.VK_R:
                if (gameOver) {
                    clearAll();
                    gameOver = false;
                    lives = 2;
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
                purchaseUpgrades(5);
                break;
            case KeyEvent.VK_5:
                purchaseUpgrades(0); //this just does nothing
                break;
            case KeyEvent.VK_6:
                purchaseUpgrades(6);
                break;
            case KeyEvent.VK_7:
                purchaseUpgrades(7);
                break;
            case KeyEvent.VK_RIGHT:
                if (upgradesPurchased5 > 0) {
                    needShoot(1);
                }
                break;
            case KeyEvent.VK_UP:
                if (upgradesPurchased5 > 0) {
                    needShoot(2);
                }
                break;
            case KeyEvent.VK_LEFT:
                if (upgradesPurchased5 > 0) {
                    needShoot(3);
                }
                break;
            case KeyEvent.VK_DOWN:
                if (upgradesPurchased5 > 0) {
                    needShoot(4);
                }
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
            * updates all timing variables and then calls update () and draw ()
            * dynamically sleeps the main thread to maintain angle framerate close to target fps
         */
    public void run() {
        setBackground(Color.black);
        for (int i = 0; i < NUMSTARS; i++) {
            randomStarPosHeight[i] = (int) (Math.random() * HEIGHT);
        }
        for (int i = 0; i < NUMSTARS; i++) {
            randomStarPosWidth[i] = (int) (Math.random() * WIDTH);
        }
        for (int i = 0; i < NUMGALAXIES; i++) {
            randomGalaxyPosHeight[i] = (int) (Math.random() * HEIGHT);
        }
        for (int i = 0; i < NUMGALAXIES; i++) {
            randomGalaxyPosWidth[i] = (int) (Math.random() * WIDTH);
        }
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
        Game game = new Game(1760, 990, 60);
        game.run();
    }


}
