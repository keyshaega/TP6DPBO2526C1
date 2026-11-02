import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;

public class View extends JPanel {
    private final int width = Logic.FRAME_WIDTH;
    private final int height = Logic.FRAME_HEIGHT;
    private Logic logic;

    public View(Logic logic) {
        this.logic = logic;
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        setBackground(new Color(120, 200, 245));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBackground(g2);
        drawPipes(g2);
        drawPlayer(g2);
        drawGround(g2);
        drawScore(g2);
        drawMessage(g2);
        g2.dispose();
        Toolkit.getDefaultToolkit().sync(); // animasi lebih mulus di Linux
    }

    private void drawBackground(Graphics2D g) {
        g.setPaint(new GradientPaint(0, 0, new Color(95, 190, 245),
                0, height, new Color(210, 240, 255)));
        g.fillRect(0, 0, width, height);

        // Awan dekorasi
        g.setColor(new Color(255, 255, 255, 210));
        g.fillOval(22, 105, 65, 25);
        g.fillOval(48, 90, 55, 38);
        g.fillOval(235, 170, 75, 28);
        g.fillOval(265, 153, 52, 40);
        g.fillOval(120, 340, 55, 22);
    }

    private void drawPipes(Graphics2D g) {
        for (Logic.Pipe pipe : logic.getPipes()) {
            int x = pipe.getX();
            int pipeWidth = pipe.getWidth();
            int topHeight = pipe.getGapTop();
            int bottomY = pipe.getGapBottom();

            g.setColor(new Color(35, 155, 55));
            g.fillRect(x, 0, pipeWidth, topHeight);
            g.fillRect(x, bottomY, pipeWidth, Logic.GROUND_Y - bottomY);

            g.setColor(new Color(75, 205, 75));
            g.fillRect(x + 7, 0, 12, topHeight);
            g.fillRect(x + 7, bottomY, 12, Logic.GROUND_Y - bottomY);

            g.setColor(new Color(25, 125, 45));
            g.fillRect(x - 5, topHeight - 22, pipeWidth + 10, 22);
            g.fillRect(x - 5, bottomY, pipeWidth + 10, 22);

            g.setColor(new Color(20, 105, 40));
            g.setStroke(new BasicStroke(2));
            g.drawRect(x, 0, pipeWidth, topHeight);
            g.drawRect(x, bottomY, pipeWidth, Logic.GROUND_Y - bottomY);
        }
    }

    private void drawPlayer(Graphics2D g) {
        Player player = logic.getPlayer();
        Image bird = player.getImage();
        int x = player.getPosX();
        int y = player.getPosY();

        if (bird != null && bird.getWidth(this) > 0) {
            g.drawImage(bird, x, y, player.getWidth(), player.getHeight(), this);
        } else {
            g.setColor(new Color(255, 190, 35));
            g.fillOval(x, y, player.getWidth(), player.getHeight());
            g.setColor(Color.WHITE);
            g.fillOval(x + 27, y + 7, 9, 9);
            g.setColor(Color.BLACK);
            g.fillOval(x + 31, y + 10, 4, 4);
            g.setColor(new Color(240, 100, 25));
            g.fillPolygon(new int[]{x + 37, x + 47, x + 37},
                    new int[]{y + 17, y + 21, y + 24}, 3);
        }
    }

    private void drawGround(Graphics2D g) {
        g.setColor(new Color(105, 190, 65));
        g.fillRect(0, Logic.GROUND_Y, width, height - Logic.GROUND_Y);
        g.setColor(new Color(65, 150, 45));
        g.fillRect(0, Logic.GROUND_Y, width, 8);

        g.setColor(new Color(220, 185, 115));
        g.fillRect(0, Logic.GROUND_Y + 8, width, height - Logic.GROUND_Y - 8);
        g.setColor(new Color(190, 145, 80));
        int offset = logic.getGroundOffset();
        for (int x = -24 + offset; x < width; x += 24) {
            g.drawLine(x, Logic.GROUND_Y + 18, x + 12, height);
        }
    }

    private void drawScore(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 42));
        String score = String.valueOf(logic.getScore());
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(score)) / 2;
        g.setColor(new Color(0, 0, 0, 75));
        g.drawString(score, x + 2, 72);
        g.setColor(Color.WHITE);
        g.drawString(score, x, 70);
    }

    private void drawMessage(Graphics2D g) {
        String state = logic.getGameState();
        if (state.equals("PLAYING")) {
            return;
        }

        g.setColor(new Color(0, 0, 0, 115));
        g.fillRoundRect(28, 220, width - 56, 175, 22, 22);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 27));
        drawCentered(g, state.equals("READY") ? "FLAPPY BIRD" : "GAME OVER", 265);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (state.equals("READY")) {
            drawCentered(g, "Tekan SPACE untuk mulai", 310);
            drawCentered(g, "Tekan berulang untuk menjaga terbang", 340);
        } else {
            drawCentered(g, "Skor: " + logic.getScore() + "   Best: " + logic.getBestScore(), 310);
            drawCentered(g, "Tekan SPACE untuk bermain lagi", 345);
        }
    }

    private void drawCentered(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (width - fm.stringWidth(text)) / 2, y);
    }
}
