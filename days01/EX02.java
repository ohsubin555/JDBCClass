package days01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EX02 {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		         /*
		          * Java Application
					       │
					       │ JDBC API
					       ▼
					   JDBC Driver
					       │
					       ▼
					Database
					(MySQL, Oracle, PostgreSQL 등)
					
					jdbc 동작 순서 
					1. JDBC Driver 로드
					2. DB 연결(Connection) : DriverManager 객체 -> CONNECTION 생성
					3. SQL 작성
					4. SQL 실행
					5. 결과(ResultSet) 처리
					   STATMENT 생성 RESULTSET 조회
					6. 자원 해제
		          */
		//1. JDBC Driver 로드
//		Class.forName("oracle.jdbc.driver.OracleDriver");
		//2. DB 연결(Connection) : DriverManager 객체 -> CONNECTION 생성
		Connection conn =
				DriverManager.getConnection(
				    "jdbc:oracle:thin:@localhost:1521/XEPDB1",
				    "scott",
				    "tiger"
				    );
		System.out.println(conn.isClosed()); //DB 연결 0 false
		System.out.println(conn); //DB 연결 O FALSE
		//4 DB 닫기
		
		conn.close();
		System.out.println("end");
		
//		com.util.DBConn
				
		
	}

}
