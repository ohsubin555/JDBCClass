package dayts03;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.doit.domain.DeptVO;
import org.doit.domain.EmpVO;

import com.util.DBConn;

public class EX03 {

	public static void main(String[] args) {
		//	   -------------------------------------
		//		부서번호    부서명           사원수
		//		-------------------------------------
		//		10         ACCOUNTING      3
		//		20         RESEARCH        5
		//		30         SALES           6
		//		40         OPERATIONS      0

		String sql = """
				SELECT d.deptno, dname, COUNT(e.empno) empCount
				FROM dept d LEFT JOIN emp e ON d.deptno = e.deptno
				GROUP BY d.deptno, dname
				ORDER BY d.deptno ASC
				""";

		String eSql = """
				SELECT *
				FROM emp
				WHERE deptno= ? 
						ORDER BY empno ASC 
						""";

		Connection conn = null;
		PreparedStatement dPstmt = null, ePstmt = null; 
		ResultSet dRs = null,eRs = null;
		DeptVO dVo = null;
		EmpVO eVo = null;


		int deptno;
		String dname;
		int empCount;

		try {
			conn = DBConn.getConnection();
			dPstmt = conn.prepareStatement(sql);
			dRs = dPstmt.executeQuery();
			if (dRs.next()) {
				do {
					deptno = dRs.getInt("deptno");
					dname = dRs.getString("dname");
					empCount = dRs.getInt("empCount");

					System.out.printf("[%s(%d)\t%d명]\n", dname, deptno, empCount);

					// 시작 deptno 속한 사원들 SELECT 출력


					try {
						ePstmt = conn.prepareStatement(eSql);
						ePstmt.setInt(1, deptno);
						eRs = ePstmt.executeQuery();

						int empno;
						String ename;
						String job;
						int mgr;
						LocalDateTime hiredate;  
						double sal;
						double comm;


						if (eRs.next()) {// 첫번째 레코드는 존재한다. 



							do {
								empno = eRs.getInt("empno");
								ename = eRs.getString("ename");
								job = eRs.getString("job");
								mgr = eRs.getInt("mgr");

								hiredate = eRs.getDate("hiredate").toLocalDate().atStartOfDay();   

								sal = eRs.getDouble("sal");
								comm = eRs.getDouble("comm");
								deptno = eRs.getInt("deptno");

								System.out.printf(" [%d]\t%s\t\n" , empno, ename, job);



							} while (eRs.next());


						}else {
							System.out.println(" 사원 존재 x ");

						}
					}
					catch (SQLException e) {
						e.printStackTrace();
					} finally {
						try {
							eRs.close();
							ePstmt.close();
							// 4

						} catch (SQLException e) { 
							e.printStackTrace();
						}
					}



					//끝 

				} while (dRs.next());
			}
		}
		catch (SQLException e) {

			e.printStackTrace();
		}finally {
			try {
				dRs.close();
				dPstmt.close();
				DBConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}


	}
}
