package days02;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.doit.domain.EmpDeptSalgradeVO;

import com.util.DBConn;

public class EX03 {

	public static void main(String[] args) {

		Connection conn = null;
		Statement stmt = null; 
		ResultSet rs = null; 
		ArrayList <EmpDeptSalgradeVO> list = null;

		EmpDeptSalgradeVO vo = null;

		conn = DBConn.getConnection();

		String sql = """
				SELECT empno, ename, hiredate, sal + NVL(comm, 0) pay  
				, dname    , grade 
				FROM emp e LEFT JOIN dept d ON e.deptno = d.deptno 
				JOIN salgrade s ON sal BETWEEN losal AND hisal

				""";

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			int empno;                 
			String ename;              
			LocalDateTime hiredate;    
			double pay;                
			String dname;              
			int grade;   

			if (rs.next()) {// 첫번째 레코드는 존재한다. 
				list = new ArrayList<EmpDeptSalgradeVO>();
				do {
					empno = rs.getInt("empno");
					ename = rs.getString("ename");
					hiredate = rs.getDate("hiredate").toLocalDate().atStartOfDay();
					pay = rs.getDouble("pay");
					dname = rs.getString("dname");
					grade = rs.getInt("grade");

					vo = EmpDeptSalgradeVO.builder()
							.empno(empno)
							.ename(ename)
							.hiredate(hiredate)
							.pay(pay)
							.dname(dname)
							.grade(grade)
							.build();


					list.add(vo);
				} while (rs.next());
			}


			dispEmpDeptSalgradeInfo(list);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
				// 4
				DBConn.close();

			} catch (SQLException e) { 
				e.printStackTrace();
			}
		}
		System.out.println("end");

	} // main

	private static void dispEmpDeptSalgradeInfo(ArrayList<EmpDeptSalgradeVO> list) {
		list.forEach(System.out::println);
	}
 

} // class