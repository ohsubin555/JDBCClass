package days01;

import java.sql.Connection;

import com.util.DBConn;

/**
 * @author subin 
 * @date 2026. 7. 3. 오전 10:09:42
 * @subject
 * @content
 */
public class EX03 {

	   public static void main(String[] args) {


	      String url = "jdbc:oracle:thin:@192.168.10.166:1521/XEPDB1";
	      String user = "scott";
	      String password = "tiger";
	      Connection conn = DBConn.getConnection(url,user,password);
	      System.out.println(conn);

//	      Connection conn = DBConn.getConnection();
//	      System.out.println(conn);
//	      conn = DBConn.getConnection();
//	      System.out.println(conn);
//	      conn = DBConn.getConnection();
//	      System.out.println(conn);
//	      DBConn.close();
	//
//	      conn = DBConn.getConnection();
//	      System.out.println(conn);
//	      System.out.println("end");
	   }

	}