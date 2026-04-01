import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet; 

public class Connections {
    
    public static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/EDPProject";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "johnmark";
    public static final String DB_USER_TABLE = "users";
    
    public static boolean register(String username, String password){
        
        try{
            if (!checkuser(username)) {
                Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                
                PreparedStatement insertUser = connect.prepareStatement("INSERT INTO " + DB_USER_TABLE +
                        "(username, password)" + "VALUES(?, ?)");
                
                insertUser.setString(1, username);
                insertUser.setString(2, password);
                insertUser.executeUpdate();
                return true;
                
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean checkuser(String username){
        try{
            Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            
            PreparedStatement checkuserExist = connect.prepareStatement("SELECT * FROM " + DB_USER_TABLE +
                    " WHERE USERNAME = ?");
            
            checkuserExist.setString(1, username);
            
            ResultSet resultset = checkuserExist.executeQuery();
            
            if (!resultset.isBeforeFirst()) {
                return false;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return true;
    }
    
    public static boolean validUser(String username, String password){
        try{
            Connection connect = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            
             PreparedStatement validateuser = connect.prepareStatement("SELECT * FROM " + DB_USER_TABLE +
                    " WHERE USERNAME = ? AND PASSWORD = ?");
             
             validateuser.setString(1, username);
             validateuser.setString(2, password);
             ResultSet resultset = validateuser.executeQuery();
             
             if (!resultset.isBeforeFirst()) {
                return false;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return true; 
    }
}
