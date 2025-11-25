import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.sound.sampled.*;

public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener {

    int boardWidth=700;
    int boardHeight=250;

    // images
    Image dinoImg, cactus1Img, cactus2Img, cactus3Img, dinoDead, dinoJump, cloudImg;
    Image bird1Img, bird2Img;

    class Block {
        int x, y, width, height;
        Image img;

        public Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int dinoWidth = 88;
    int dinoHeight = 94;
    int dinoX = 50;
    int dinoY = boardHeight - dinoHeight;

    int velocityX = -12;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Block> cactiArray = new ArrayList<>();
    ArrayList<Block> clouds = new ArrayList<>();
    ArrayList<Block> birds = new ArrayList<>();

    int cactus1Width = 30;
    int cactus2Width = 60;
    int cactus3Width = 90;
    int cactusHeight = 70;

    int cactusX = 700;
    int cactusY = boardHeight - cactusHeight;

    int cloudWidth = 70;
    int cloudHeight = 40;

    int birdWidth = 50;
    int birdHeight = 40;

    Block dino;

    Timer gameLoopTimer;
    Timer cactusTimer;
    Timer cloudTimer;
    Timer birdTimer;

    boolean gameOver = false;
    int score = 0;

    public ChromeDinosaur() {

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.lightGray);
        setFocusable(true);
        addKeyListener(this);

        // Load images
        dinoImg = new ImageIcon(getClass().getResource("/img/dino-run.gif")).getImage();
        cactus1Img = new ImageIcon(getClass().getResource("/img/cactus1.png")).getImage();
        cactus2Img = new ImageIcon(getClass().getResource("/img/cactus2.png")).getImage();
        cactus3Img = new ImageIcon(getClass().getResource("/img/cactus3.png")).getImage();
        dinoDead = new ImageIcon(getClass().getResource("/img/dino-dead.png")).getImage();
        dinoJump = new ImageIcon(getClass().getResource("/img/dino-jump.png")).getImage();
        cloudImg = new ImageIcon(getClass().getResource("/img/cloud.png")).getImage();
        bird1Img = new ImageIcon(getClass().getResource("/img/bird1.png")).getImage();
        bird2Img = new ImageIcon(getClass().getResource("/img/bird2.png")).getImage();

        dino = new Block(dinoX, dinoY, dinoWidth, dinoHeight, dinoImg);

        // Game loop
        gameLoopTimer = new Timer(1000/60, this);
        gameLoopTimer.start();

        cactusTimer = new Timer(1500, e -> placeCacti());
        cactusTimer.start();

        cloudTimer = new Timer(2000, e -> placeCloud());
        cloudTimer.start();

        birdTimer = new Timer(4000, e -> placeBird());
        birdTimer.start();
    }

    void placeCacti() {
        if (gameOver) return;

        double chance = Math.random();
        if (chance > 0.90)
            cactiArray.add(new Block(cactusX, cactusY, cactus3Width, cactusHeight, cactus3Img));
        else if (chance > 0.60)
            cactiArray.add(new Block(cactusX, cactusY, cactus2Width, cactusHeight, cactus2Img));
        else
            cactiArray.add(new Block(cactusX, cactusY, cactus1Width, cactusHeight, cactus1Img));
    }

    void placeCloud() {
        if (gameOver) return;
        clouds.add(new Block(700, 20 + (int)(Math.random()*50), cloudWidth, cloudHeight, cloudImg));
    }

    void placeBird() {
        if (gameOver) return;
        int birdY = dinoY - 40; // near the dino height
        Image birdImg = (Math.random() > 0.5) ? bird1Img : bird2Img;
        birds.add(new Block(700, birdY, birdWidth, birdHeight, birdImg));
    }

    // SOUND PLAYER
    public void playSound(String path) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        // clouds
        for (Block c : clouds)
            g.drawImage(c.img, c.x, c.y, c.width, c.height, null);

        // dino
        g.drawImage(dino.img, dino.x, dino.y, dino.width, dino.height, null);

        // cacti
        for (Block c : cactiArray)
            g.drawImage(c.img, c.x, c.y, c.width, c.height, null);

        // birds
        for (Block b : birds)
            g.drawImage(b.img, b.x, b.y, b.width, b.height, null);

        // score
        g.setColor(Color.black);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 32));

        if (gameOver)
            g.drawString("Game Over! Score: " + score, 10, 35);
        else
            g.drawString("Score: " + score, 10, 35);
    }

    boolean isColliding(Block a, Block b) {
        return (a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            gameLoopTimer.stop();
            cactusTimer.stop();
            cloudTimer.stop();
            birdTimer.stop();
        }
    }

    public void move() {
        velocityY += gravity;
        dino.y += velocityY;

        if (dino.y > dinoY) {
            dino.y = dinoY;
            velocityY = 0;
            dino.img = dinoImg;
        }

        // Move cacti
        for (Block c : cactiArray) {
            c.x += velocityX;
            if (isColliding(dino, c)) {
                dino.img = dinoDead;
                playSound("/sounds/gameOver.wav");
                gameOver = true;
            }
        }

        // Move clouds
        for (Block c : clouds)
            c.x += velocityX + 6;

        // Move birds
        for (Block b : birds) {
            b.x += velocityX;
            if (isColliding(dino, b)) {
                dino.img = dinoDead;
                playSound("/sounds/gameOver.wav");
                gameOver = true;
            }
        }

        if (!gameOver) score++;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_SPACE) {

            if (dino.y == dinoY) {
                velocityY = -17;
                dino.img = dinoJump;
                playSound("/sounds/jump.wav");
            }

            if (gameOver) {
                dino.y = dinoY;
                dino.img = dinoImg;
                velocityY = 0;
                cactiArray.clear();
                clouds.clear();
                birds.clear();
                score = 0;
                gameOver = false;

                gameLoopTimer.start();
                cactusTimer.start();
                cloudTimer.start();
                birdTimer.start();
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}
