package dayts03;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import com.util.DBConn;

/**
 * @author subin 
 * @date 2026. 7. 7. 오후 4:18:01
 * @subject
 * @content
 */
public class Ex07_02 {

	public static void main(String[] args) {
		
	
		      Scanner sc = new Scanner(System.in);
		         String dname, loc;
		         
		         System.out.print("> 부서명 입력 하세요: ");
		         dname = sc.nextLine();
		         System.out.print("> 지역 입력 하세요: ");
		         loc = sc.nextLine();
		         // 2.
		         Connection conn = null;
		         CallableStatement cstmt = null;
		         
		         
		         
		         
		         String sql = "{call up_insertdept(?,?)}";
		         
		         try {
		            conn = DBConn.getConnection();
		            cstmt = conn.prepareCall(sql);
		            // in
		            cstmt.setString(1, dname);
		            cstmt.setString(2, loc);
		            
		            cstmt.execute();
		            System.out.printf("등록 성공입니다. 부서명: %s, 부서 지역: %s\n",dname,loc);
		            
		         } catch (SQLException e) {
		            if(e.getErrorCode()==20001) {
		               System.out.println("등록실패. 동일한 부서명이 있습니다.");
		            } else {
		               System.out.println("다시 시도해주세요.");
		            }
		         } finally {
		            try {
		               cstmt.close();
		               DBConn.close();
		            } catch (SQLException e) {
		               // TODO Auto-generated catch block
		               e.printStackTrace();
		            }
		         }
		         System.out.println("end");
		   }
	}


