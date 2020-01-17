import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

/**
 * TetrisGUI
 */
public class TetrisGUI {

    public static void main(String[] args) {
        JFrame f = new JFrame("Tetris");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int windowFrameX = 22;
        int windowFrameY = 56;
        f.setSize(Tetris.width + Tetris.blockSM * 4 + windowFrameX, Tetris.height + windowFrameY);
        f.setVisible(true);

        final Tetris game = new Tetris();
        game.init();
        f.add(game);
        Thread th = new Thread() {
            @Override
            public void run() {
                while (true) {
                    while (!game.isGameOver()) {
                        try {
                            Thread.sleep(game.getSpeed());
                            game.dropDown();
                        } catch (InterruptedException e) {
                        }
                    }
                    try {
                        while (true)
                            Thread.sleep(Long.MAX_VALUE);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };

        // Keyboard controls
        f.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if (!game.isGameOver()) {
                    switch (e.getKeyCode()) {
                    case KeyEvent.VK_J:
                        if (game.rotate(+1))
                            th.interrupt();
                        break;
                    case KeyEvent.VK_K:
                        if (game.rotate(-1))
                            th.interrupt();
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        if (game.move(-1))
                            th.interrupt();
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        if (game.move(+1))
                            th.interrupt();
                        break;
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        if (game.fastDrop())
                            th.interrupt();
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        game.hardDrop();
                        th.interrupt();
                        break;
                    case KeyEvent.VK_H:
                        if (game.hold())
                            th.interrupt();
                    default:
                        return;
                    }
                } else {
                    switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        game.init();
                        th.interrupt();
                        break;
                    default:
                        return;
                    }
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        // Make the falling piece drop every second
        th.start();
    }
}