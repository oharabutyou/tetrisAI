import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * TestPostgresql
 */
public class TestPostgresql {

    public static void main(String[] args) {
        Connection con;
        Statement stat;
        ResultSet set;
        try {
            con = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/testdb", "postgres", "pass");
            stat = con.createStatement();
            set = stat.executeQuery("select * from test");
            while(set.next())
                System.out.println(set.getString("name"));
            stat.close();
            con.close();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
        }
    }
}