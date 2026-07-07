package dayts03;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Scanner;

import org.doit.domain.DeptVO;

import com.util.DBConn;

/**
 * @author subin 
 * @date 2026. 7. 7. 오후 4:18:49
 * @subject
 * @content up_updatedept 수정할 부서번호, 부서명, 지역명 
 */
public class Ex07_03 {

	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);
	      System.out.print("> 수정할 부서id(deptno)를 입력?");
	      int deptno = Integer.parseInt(scanner.nextLine());
	      System.out.print("> 수정할 부서명 입력?");
	      String dname = scanner.nextLine();
	      System.out.print("> 수정할 지역명 입력?");
	      String loc = scanner.nextLine();

	      // UP_IDCHECK
	      Connection conn = null;
	      CallableStatement cstmt = null;

	      String sql = "{Call UP_UPDATEDEPT(?,?,?)";
	      
	      try {
	         conn = DBConn.getConnection();
	         cstmt = conn.prepareCall(sql);
	         cstmt.setInt(1, deptno);      
	         cstmt.setString(2, dname);      
	         cstmt.setString(3, loc);      
	         cstmt.execute();
	         System.out.println("수정되었습니다.");
	         
	      } catch (SQLException e) {
	         if(e.getErrorCode() == 20001) {
	            System.out.println("존재하지 않는 부서입니다.");
	         } else if(e.getErrorCode() == 20002) {
	               System.out.println("수정할값이 없습니다.");
	         } else {
	            // 그 회 예외처리구현
	            e.printStackTrace();
	         }         
	      }   
	   } // main
}