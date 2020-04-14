/**
 * TetrisScore
 */
public class TetrisScore {

    private boolean tspin;
    private boolean BtoB;
    private long REN;
    private long score;
    private long scoreLine;
    private long level;
    private long speed;
    private String msg;
    volatile private int damage;

    /**
     * 0->tspin,1->single,2->double,3->triple,4->tetris
     */
    private long[] counts;
    private long countBtoB;
    private long maxREN;

    private String player;
    private TetrisScoreDB db;

    public TetrisScore(long level) {
        tspin = false;
        BtoB = false;
        REN = -1;
        score = 0;
        scoreLine = 0;
        this.level = level;
        speed = 1000;
        for (int i = 1; i < level; i++) {
            speed = speed * 3 / 4;
        }
        msg = "START!";
        damage=0;
        counts = new long[5];
        countBtoB = 0;
        maxREN = 0;
        player = "player";
    }

    public long getScore() {
        return score;
    }

    public long getLines() {
        return scoreLine;
    }

    public long getLevel() {
        return level;
    }

    public String getMsg() {
        return msg;
    }

    public String gameOver() {
        msg += " Gameover!Score:" + score;
        return msg;
    }

    public String getResult() {
        String result = "";
        result = "".concat("player:" + player).concat("\n score:" + score).concat("\n lines:" + scoreLine)
                .concat("\nT-Spin:" + counts[0]).concat("\nsingle:" + counts[1]).concat("\ndouble:" + counts[2])
                .concat("\ntriple:" + counts[3]).concat("\nTETRIS:" + counts[4]).concat("\nBack to Back:" + countBtoB)
                .concat("\nMAX REN:" + maxREN);
        return result;
    }

    public void setDB() {
        if (db == null) {
            db = new TetrisScoreDB();
        }
        db.addRecord(player, score);
    }

    /**
     * for fastDrop and hardDrop
     */
    public void increment(int i) {
        score+=i;
    }

    /**
     * 
     * @param isTspin
     * @return same to isTspin
     */
    public boolean setTspin(boolean isTspin) {
        return tspin = isTspin;
    }

    /**
     * This method uses tspin param and level. It counts clearLines, T-Spins, BtoBs
     * and RENs. It sets level and msg.
     * 
     * @param numClears
     */
    public int addScore(int numClears) {
        int power=0;
        msg = "";
        boolean preBtoB = BtoB;
        if (tspin) {
            counts[0]++;
            msg = "T-Spin ";
            power++;
        }
        if (numClears > 0) {
            BtoB = false;
            counts[numClears]++;
            REN++;
            if (maxREN < REN)
                maxREN = REN;
        } else
            REN = -1;

        switch (numClears) {
        case 0:
            if (tspin)
                score += 100 * level;
            break;
        case 1:
            score += 100 * level;
            if (tspin)
                score += 100 * level;
            BtoB = tspin;
            msg += "single";
            break;
        case 2:
            score += 200 * level;
            power+=1;
            if (tspin)
                score += 200 * level;
            BtoB = tspin;
            msg += "double";
            break;
        case 3:
            score += 400 * level;
            power+=2;
            if (tspin)
                score += 400 * level;
            BtoB = tspin;
            msg += "triple";
            break;
        case 4:
            score += 800 * level;
            power+=4;
            BtoB = true;
            msg += "TETRIS!";
            break;
        }
        if (scoreLine % 10 + numClears >= 10) {
            level++;
            speed = speed * 3 / 4;
        }
        scoreLine += numClears;
        if (REN > 0) {
            msg += " REN:" + REN;
            score += 100 * level * REN;
        }
        if (BtoB && preBtoB && numClears > 0) {
            msg += " Back to Back!";
            score += 200 * level;
            power++;
            countBtoB++;
        }

        return power;
    }

    synchronized public void attack(int power){
        damage+=power;
    }

    synchronized public void recover(int power){
        if(power>damage)
            damage = 0;
        else
            damage -= power;
    }

    public int getDamage(){
        int temp = damage;
        damage = 0;
        return temp;
    }
}