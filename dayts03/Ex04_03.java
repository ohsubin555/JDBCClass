package dayts03;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import org.doit.domain.DeptVO;

import com.util.DBConn;

/**
 * @author subin 
 * @date 2026. 7. 6. 오후 5:20:07
 * @subject 부서테이블 삭제 / 삭제 할 부서번호를 스캐너로 입력받아 부서 삭제
 * @content
 */
public class Ex04_03 {
	public static void main(String[] args) {


	Scanner scanner = new Scanner(System.in);
	int deptno ;
	
	System.out.print("> 삭제할 부서번호 입력 ? ");		
	deptno = scanner.nextInt(); 

	// 2. 
	Connection conn = null;
	PreparedStatement pstmt = null; 

	conn = DBConn.getConnection();

	

String sql = """
		      DELETE FROM dept 
		      WHERE deptno = ?
		      """;


	
	int rowCount = 0;
	try {
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, deptno);
		
		rowCount = pstmt.executeUpdate(); 
		
		if (rowCount == 1) {
			System.out.println("부서 삭제 성공");
			
		}

		
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		try {
			
			pstmt.close();
			// 4
			DBConn.close();
		} catch (SQLException e) { 
			e.printStackTrace();
		}
	}


	System.out.println(" end ");
}
}

// 사원 검색: 검색 조건     사원명, 잡, 입사일자, 등급