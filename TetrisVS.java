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
                { -0.833786492906575, -0.8167544113939942, -0.7986686842277038, -0.20733595508756308,
                        -0.2523769094048205 },
                { -0.44089310685897876, 0.8448345236112056, 0.7663839526822442, -0.6749823929373355,
                        0.9405828556980675 },
                { -0.23848486877678865, -0.009487526344881037, 0.3898366063254939, 0.7692256898981575,
                        0.343626367648187 },
                { -0.468341384770256, -0.8280286375877006, -0.14478058611333955, 0.6648427924552331,
                        0.11945756749586023 },
                { 0.511513321580437, 0.6077399292623125, -0.7476622543512734, -0.3018410277707311, 0.3573325788957955 },
                { -0.5473071008897039, 0.07553574278062092, -0.8070633785094197, 0.9471759540420364,
                        -0.6324827122268826 },
                { 0.5562271619500181, -0.829392838385925, -0.7760296745162156, -0.03725862553885606,
                        0.9047326513274192 },
                { 0.5744460265735707, 0.5430198548899365, 0.03997983349342782, 0.5868050360183503,
                        0.21278696951760767 } };
        double[] outputWeight = { 0.33154288708024593, 0.054022297492330784, 0.4503183806437141, -0.8614179186511086,
                -0.40861217184416443 };
        JFrame f = new JFrame("Tetris");
        f.setLayout(new FlowLayout());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int windowFrameX = 22;
        int windowFrameY = 56;
        f.setSize((Tetris.width + Tetris.blockSM * 4 + windowFrameX) * 2, Tetris.height + windowFrameY+50);
        f.setVisible(true);

        final Tetris game = new Tetris();
        final Tetris ai = new Tetris();
        game.setVS(ai);
        ai.setVS(game);
        game.init();
        ai.initAI(new TetrisAI(inputWeight,outputWeight));
        ai.setBounds(Tetris.width + Tetris.blockSM * 4 + windowFrameX, 0, Tetris.width + 2 + Tetris.blockSM * 6, Tetris.height + 30);
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
                if (!game.isGameOver()&&!ai.isGameOver()) {
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
                        ai.initAI(new TetrisAI(inputWeight,outputWeight));
                        ai.setBounds(Tetris.width + Tetris.blockSM * 4 + windowFrameX, 0, Tetris.width + 2 + Tetris.blockSM * 6, Tetris.height + 30);
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