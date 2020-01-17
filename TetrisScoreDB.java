import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

/**
 * TetrisScoreDB
 */
public class TetrisScoreDB {
    static Scanner stdin = new Scanner(System.in);
    private String[] nameList;
    private String[] scoreList;
    private Connection con;
    private Statement stat;
    private ResultSet set;
    private final String conUrl = "jdbc:postgresql://localhost:5432/";
    private final String conUser = "postgres";
    private final String conPass = "pass";
    private final String qurSelect = "select * from HighScore order by score desc";

    public static void main(String[] args) {
        TetrisScoreDB score = new TetrisScoreDB();
        score.readDB();
        score.writeDB(stdin.next(), stdin.nextLong());
    }

    public TetrisScoreDB() {
        nameList = new String[10];
        scoreList = new String[10];
    }

    public void addRecord(String name,long score){
        writeDB(name, score);
    }

    private void readDB() {
        try {
            con = DriverManager.getConnection(conUrl, conUser, conPass);
            stat = con.createStatement();
            set = stat.executeQuery(qurSelect);
            while (set.next()) {
                String name = set.getString("name");
                System.out.print(name + ":");
                String score = set.getString("score");
                System.out.print(score + "\n");
            }
            stat.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void writeDB(String name, long score) {
        try {
            con = DriverManager.getConnection(conUrl, conUser, conPass);
            stat = con.createStatement();
            stat.execute("insert into HighScore(name,score) values(\'" + name + "\'," + score + ")");
            set = stat.executeQuery(qurSelect);
            for(int i = 1;i<=10&&set.next();i++) {
                System.out.print(i+".");
                String dbname = set.getString("name");
                scoreList[i]=dbname;
                System.out.print(dbname + ":");
                String dbscore = set.getString("score");
                nameList[i]=dbscore;
                System.out.print(dbscore + "\n");
            }
            stat.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}