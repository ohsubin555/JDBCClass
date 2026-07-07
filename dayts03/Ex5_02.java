package dayts03;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

import com.util.DBConn;

/**
 * @author subin 
 * @date 2026. 7. 7. 오후 2:36:01
 * @subject PreparedStatement
 * @content CallabledStatement 저장 프로시저, 저장 함수 x
 */
public class Ex5_02 {

	public static void main(String[] args) {
		Scanner scanner = new Scanner (System.in);
		System.out.println("중복체크할 id(empno) 입력?");
		int id = scanner.nextInt();

		// UP_IDCHECK
		Connection conn = null;
		CallableStatement cstmt = null;


		String sql = "{CALL UP_IDCHECK( ? )}";
		// pid => ? , pcheck => ? 이거도 됨 ~
		try {
			conn = DBConn.getConnection();
			cstmt = conn.prepareCall(sql);
			cstmt.setInt(1,id);

//			cstmt.executeQuery();
			cstmt.execute(); // boolean 저장 프로시저 실행 

			System.out.println("사용 가능 ");


		} catch (SQLException e) { 
			//이미사용중인 아이디 예외 처리 코딩을 하면 된다.....................................
//			e.printStackTrace();
			
			if (e.getErrorCode() == 20001) {
				System.out.println(" 이미 사용중인 아이디 입니다.  ");
				
			} else { // 그 외 예외처리 구문 추가 

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
