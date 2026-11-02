import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class Logic implements ActionListener {
    public static final int FRAME_WIDTH = 360;
    public static final int FRAME_HEIGHT = 640;
    public static final int GROUND_Y = 570;

    private final int playerWidth = 42;
    private final int playerHeight = 32;
    private final double gravity = 0.42;
    private final double flapPower = -7.2;
    private final int pipeWidth = 64;
    private final int pipeGap = 170;
    private final int pipeSpeed = 3;
    private final int pipeCapOverhang = 5;   // bibir pipa lebih lebar 5px tiap sisi
    private final int pipeCapHeight = 22;
    private final long restartDelayMs = 500; // jeda sebelum bisa restart setelah game over

    private Player player;
    private View view;
    private Timer gameLoop;
    private ArrayList<Pipe> pipes;
    private Random random;
    private int score;
    private int bestScore;
    private int groundOffset;
    private String gameState;
    private boolean spaceHeld;      // true selama SPACE ditahan (abaikan auto-repeat)
    private long gameOverTime;

    public Logic() {
        Image birdImage = null;
        URL imageURL = getClass().getResource("/assets/bird.png");
        if (imageURL != null) {
            birdImage = new ImageIcon(imageURL).getImage();
        } else {
            File imageFile = new File("src/assets/bird.png");
            if (imageFile.exists()) {
                birdImage = new ImageIcon(imageFile.getAbsolutePath()).getImage();
            }
        }

        player = new Player(85, 270, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<>();
        random = new Random();
        score = 0;
        bestScore = 0;
        gameState = "READY";
        resetPipes();

        gameLoop = new Timer(16, this);
        gameLoop.start();
    }

    public void setView(View view) {
        this.view = view;

        // Key binding lebih stabil daripada KeyListener untuk aplikasi Swing.
        // SPACE ditekan: satu kepakan per tekanan (auto-repeat saat ditahan diabaikan),
        // sehingga pemain harus menekan berulang untuk menjaga keseimbangan.
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "flap");
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), "release");

        view.getActionMap().put("flap", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (spaceHeld) {
                    return;
                }
                spaceHeld = true;
                onSpacePressed();
            }
        });
        view.getActionMap().put("release", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spaceHeld = false;
            }
        });
    }

    private void onSpacePressed() {
        if (gameState.equals("READY")) {
            startGame();
        } else if (gameState.equals("GAME_OVER")) {
            if (System.currentTimeMillis() - gameOverTime >= restartDelayMs) {
                startGame();
            }
        } else if (gameState.equals("PLAYING")) {
            flap();
        }
    }

    public Player getPlayer() { return player; }
    public ArrayList<Pipe> getPipes() { return pipes; }
    public int getScore() { return score; }
    public int getBestScore() { return bestScore; }
    public String getGameState() { return gameState; }
    public int getGroundOffset() { return groundOffset; }

    private void resetPipes() {
        pipes.clear();
        for (int i = 0; i < 3; i++) {
            int x = FRAME_WIDTH + 80 + (i * 205);
            int gapCenter = 185 + random.nextInt(190);
            pipes.add(new Pipe(x, gapCenter, pipeWidth, pipeGap));
        }
    }

    private void startGame() {
        score = 0;
        player.setPosY(270);
        player.setVelocityY(0);
        resetPipes();
        gameState = "PLAYING";
        flap();
    }

    private void flap() {
        player.setVelocityY(flapPower);
    }

    private void updateGame() {
        if (!gameState.equals("PLAYING")) {
            return;
        }

        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + (int) Math.round(player.getVelocityY()));
        groundOffset = (groundOffset + pipeSpeed) % 24;

        for (Pipe pipe : pipes) {
            pipe.setX(pipe.getX() - pipeSpeed);

            if (!pipe.isPassed() && pipe.getX() + pipeWidth < player.getPosX()) {
                pipe.setPassed(true);
                score++;
                if (score > bestScore) {
                    bestScore = score;
                }
            }
        }

        if (pipes.get(0).getX() + pipeWidth < 0) {
            pipes.remove(0);
            int lastX = pipes.get(pipes.size() - 1).getX();
            int gapCenter = 185 + random.nextInt(190);
            pipes.add(new Pipe(lastX + 205, gapCenter, pipeWidth, pipeGap));
        }

        checkCollision();
    }

    private void gameOver() {
        gameState = "GAME_OVER";
        gameOverTime = System.currentTimeMillis();
    }

    private void checkCollision() {
        if (player.getPosY() < 0 || player.getPosY() + playerHeight >= GROUND_Y) {
            gameOver();
            return;
        }

        Rectangle birdBounds = new Rectangle(
                player.getPosX() + 6, player.getPosY() + 5,
                playerWidth - 12, playerHeight - 10
        );

        for (Pipe pipe : pipes) {
            // Badan pipa + bibir pipa (cap) sesuai yang digambar di View
            Rectangle topPipe = new Rectangle(pipe.getX(), 0, pipeWidth, pipe.getGapTop());
            Rectangle bottomPipe = new Rectangle(pipe.getX(), pipe.getGapBottom(),
                    pipeWidth, GROUND_Y - pipe.getGapBottom());
            Rectangle topCap = new Rectangle(pipe.getX() - pipeCapOverhang,
                    pipe.getGapTop() - pipeCapHeight, pipeWidth + 2 * pipeCapOverhang, pipeCapHeight);
            Rectangle bottomCap = new Rectangle(pipe.getX() - pipeCapOverhang,
                    pipe.getGapBottom(), pipeWidth + 2 * pipeCapOverhang, pipeCapHeight);

            if (birdBounds.intersects(topPipe) || birdBounds.intersects(bottomPipe)
                    || birdBounds.intersects(topCap) || birdBounds.intersects(bottomCap)) {
                gameOver();
                return;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        if (view != null) {
            view.repaint();
        }
    }

    public static class Pipe {
        private int x;
        private int gapCenter;
        private int width;
        private int gap;
        private boolean passed;

        public Pipe(int x, int gapCenter, int width, int gap) {
            this.x = x;
            this.gapCenter = gapCenter;
            this.width = width;
            this.gap = gap;
            this.passed = false;
        }

        public int getX() { return x; }
        public void setX(int x) { this.x = x; }
        public int getGapTop() { return gapCenter - gap / 2; }
        public int getGapBottom() { return gapCenter + gap / 2; }
        public int getWidth() { return width; }
        public boolean isPassed() { return passed; }
        public void setPassed(boolean passed) { this.passed = passed; }
    }
}
