import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.FlowLayout;
import javax.swing.JFrame;

/**
 * TetrisGUI
 */
public class TetrisVS {

    public static void main(String[] args) {

        double[][] inputWeight = {
            { -12.63, 0, 0, 0, 0, },
            { 6.60, 0, 0, 0, 0, },
            { -9.22, 0, 0, 0, 0, },
            { -19.77, 0, 0, 0, 0, },
            { -13.08, 0, 0, 0, 0, },
            { -10.49, 0, 0, 0, 0, },
            { -1.61, 0, 0, 0, 0, },
            { -24.04, 0, 0, 0, 0, } };
        double[] outputWeight = { 1.0, 0, 0, 0, 0, };
        JFrame f = new JFrame("Tetris");
        f.setLayout(new FlowLayout());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int windowFrameX = 22;
        int windowFrameY = 56;
        f.setSize((Tetris.width + Tetris.blockSM * 4 + windowFrameX) * 2, Tetris.height + windowFrameY + 50);
        f.setVisible(true);

        final Tetris game = new Tetris();
        final Tetris ai = new Tetris();
        game.setVS(ai);
        ai.setVS(game);
        game.init();
        ai.initAI(new TetrisAI(inputWeight, outputWeight));
        ai.setBounds(Tetris.width + Tetris.blockSM * 4 + windowFrameX, 0, Tetris.width + 2 + Tetris.blockSM * 6,
                Tetris.height + 30);
        f.add(game);
        f.add(ai);
        Thread th = new Thread() {
            @Override
            public void run() {
                while (true) {
                    while (!game.isGameOver() && !ai.isGameOver()) {
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
        Thread aiThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    while (!game.isGameOver() && !ai.isGameOver()) {
                        try {
                            Thread.sleep(1000);
                            ai.AIPlay();
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
                if (!game.isGameOver() && !ai.isGameOver()) {
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
                        ai.initAI(new TetrisAI(inputWeight, outputWeight));
                        ai.setBounds(Tetris.width + Tetris.blockSM * 4 + windowFrameX, 0,
                                Tetris.width + 2 + Tetris.blockSM * 6, Tetris.height + 30);
                        th.interrupt();
                        aiThread.interrupt();
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
        aiThread.start();
    }
}