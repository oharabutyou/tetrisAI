
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

/**
 * TetrisAITest
 */
public class TetrisAITest {

    public static void main(String[] args) {
        JFrame f = new JFrame("Tetris");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int windowFrameX = 22;
        int windowFrameY = 56;
        f.setSize(Tetris.width + Tetris.blockSM * 4 + windowFrameX, Tetris.height + windowFrameY);
        f.setVisible(true);

        TetrisGA ga = new TetrisGA(100, 500);
        ga.startSearch();
        final Tetris game = new Tetris();
        game.initAI(new TetrisAI(ga.getInputWeight(),ga.getOutputWeight()));
        f.add(game);
        Thread th = new Thread() {
            @Override
            public void run() {
                while (true) {
                    while (!game.isGameOver()) {
                        try {
                            if(game.getLines()!=0)
                            while(true)
                            Thread.sleep(Long.MAX_VALUE);
                            game.AIPlay();
                        } catch (InterruptedException e) {
                        }
                    }/*
                    try {
                        while (true)
                            Thread.sleep(Long.MAX_VALUE);
                    } catch (InterruptedException e) {
                    }*/
                    game.initAI(new TetrisAI(ga.getInputWeight(),ga.getOutputWeight()));
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
                    case KeyEvent.VK_SPACE:
                        game.AIPlay();
                        th.interrupt();
                        break;
                    default:
                        return;
                    }
                } else {
                    switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        game.initAI(new TetrisAI(ga.getInputWeight(),ga.getOutputWeight()));
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