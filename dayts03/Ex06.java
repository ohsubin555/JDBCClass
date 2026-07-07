package dayts03;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

import com.util.DBConn;

public class Ex06 {
	public static void main(String[] args) {
		
		Scanner scanner = new Scanner (System.in);
		System.out.println("로그인 체크할 id - empno , pw - ename 입력?");
		int id = scanner.nextInt();
        String pwd = scanner.next();
        
		// UP_IDCHECK
		Connection conn = null;
		CallableStatement cstmt = null;


		String sql = "{CALL UP_LOGIN(?,?,?)}";
		// pid => ? , pcheck => ? 이거도 됨 ~
		try {
			conn = DBConn.getConnection();
			cstmt = conn.prepareCall(sql);
			cstmt.setInt(1,id);
			cstmt.setString(2, pwd);

			cstmt.registerOutParameter(3, Types.INTEGER);
			cstmt.executeQuery();
			int check = cstmt.getInt(3);

			if (check == 0) {
				System.out.println("로그인 성공 ");
			}else if (check == -1) {
				System.out.println("로그인 실패! \n 아이디는 존재하지만 비밀번호가 잘못됨 ");
			}else if(check == 1 ) {
				System.out.println("로그인 실패! \n 존재하지 않는 아이디입니다.  ");
			}
		} catch (SQLException e) { 
			e.printStackTrace();
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
