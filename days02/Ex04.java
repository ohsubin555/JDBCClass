package days02;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import org.doit.domain.DeptVO;

import com.util.DBConn;

/**
 * @author subin 
 * @date 2026. 7. 6. 오후 4:29:31
 * @subject CRUD 
 * @content 부서테이블 조회 days01.EX04.java
 *          부서테이블 추가 deptno, dname, loc, 
 *          부서테이블 수정
 *          부서테이블 삭제
 *          부서테이블 검색
 */
public class Ex04 {

	public static void main(String[] args) {
		//1
		Scanner scanner = new Scanner(System.in);
		String dname, loc ;
		
		System.out.println(">부서명, 지역명 입력?");
		dname = scanner.next();
		loc = scanner.next();
		
		//2.
		
		Connection conn = null;
		Statement stmt = null; // SQL 실행 객체
		
		conn = DBConn.getConnection();
		
		String sql =  String.format( "INSERT INTO dept (deptno , dname, loc) "
						+ " VALUES (SEQ_DEPT.NEXTVAL, '%s' , '%s')"
                     , dname, loc );
		
		int rowCount = 0;
		try {
			stmt = conn.createStatement();
			rowCount = stmt.executeUpdate(sql); 
			
			if (rowCount == 1) {
				System.out.println("부서 추가 성공");
				
			}

			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				
				stmt.close();
				// 4
				DBConn.close();
			} catch (SQLException e) { 
				e.printStackTrace();
			}
		}


		System.out.println(" end ");

	}//main

}
