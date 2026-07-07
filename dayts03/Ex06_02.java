package dayts03;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

import com.util.DBConn;

public class Ex06_02 {
	public static void main(String[] args) {

		Scanner scanner = new Scanner (System.in);
		System.out.println("로그인 체크할 id - empno , pw - ename 입력?");
		int id = scanner.nextInt();
		String pwd = scanner.next();

		// UP_IDCHECK
		Connection conn = null;
		CallableStatement cstmt = null;


		String sql = "{CALL UP_LOGIN(?,?)}";
		// pid => ? , pcheck => ? 이거도 됨 ~
		try {
			conn = DBConn.getConnection();
			cstmt = conn.prepareCall(sql);
			cstmt.setInt(1,id);
			cstmt.setString(2, pwd);


			cstmt.execute();

			System.out.println("로그인 성공");
			
		} catch (SQLException e) { 
			//e.printStackTrace();
			if (e.getErrorCode() == 20001) {
				System.out.println(" 존재하지 않는 아이디입니다 ");
				
			} else if (e.getErrorCode() == 20002) {
				System.out.println(" 비밀번호 일치 하지 않습니다.   ");
			} else {
				
				e.printStackTrace();
			} 
		} finally {
			try {
				cstmt.close();
				DBConn.close();
			} catch (SQLException e) { 
				e.printStackTrace();
			}
		}



	}//main

}//class
