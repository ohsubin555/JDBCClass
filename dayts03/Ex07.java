package dayts03;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import org.doit.domain.DeptVO;

import com.util.DBConn;

public class Ex07 {

	public static void main(String[] args) {
		// 모든 부서 정보를 UP_SELECTDEPT 저장 프로시저를 사용해서 조회

		

		// UP_IDCHECK
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;
		ArrayList<DeptVO> list = null;
		DeptVO vo = null;


		String sql = "{CALL UP_SELECTDEPT(?)}";
		// pid => ? , pcheck => ? 이거도 됨 ~
		try {
			conn = DBConn.getConnection();
			cstmt = conn.prepareCall(sql);
		

			cstmt.registerOutParameter(1, Types.REF_CURSOR);
			cstmt.executeQuery();
			int check = (int) cstmt.getObject(1);

			if (rs.next()) {
				list = new ArrayList<DeptVO>();
				do {
					vo = DeptVO.builder()
							.deptno(rs.getInt("deptno"))
							.dname(rs.getString("dname"))
							 .loc(rs.getString("loc"))
		                     .build();
		               list.add(vo);
		            } while (rs.next());
		         } // if
		         
		         dispDeptInfo(list);

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

	   } // main

	   private static void dispDeptInfo(ArrayList<DeptVO> list) {
	      list.forEach(System.out::println);
	   }

	} // class
/*
CREATE OR REPLACE PROCEDURE up_selectdept
(
   pdeptcursor OUT SYS_REFCURSOR 
)
IS


BEGIN
   OPEN pdeptcursor FOR 

    select *
    from dept
    where deptno > 0;

--    EXCEPTION

END;
/
 */