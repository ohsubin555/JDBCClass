package dayts03;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import org.doit.domain.EmpVO;

import com.util.DBConn;

//import jdk.internal.org.jline.terminal.TerminalBuilder.SystemOutput;

public class Ex01 {

	public static void main(String[] args) {

		Scanner scanner = new Scanner (System.in);
		String searchCondition , searchKeyword;

		System.out.println("검색조건 입력");
		searchCondition = scanner.next();
		searchKeyword = scanner.next();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList <EmpVO> list = null;
		EmpVO vo = null;

		conn = DBConn.getConnection();

		String sql = """
				SELECT ename, job, hiredate, sal, deptno
				FROM emp
				""";
		
		if (searchCondition.equalsIgnoreCase("ename")) {
	         sql += String.format("ename = %s", searchKeyword );
	      } else if(searchCondition.equalsIgnoreCase("job")) {
	    	  sql += String.format("job = %s", searchKeyword );
	      } else if(searchCondition.equalsIgnoreCase("hiredate")) {
	    	  sql += String.format("hiredate = %s", searchKeyword );
	      } else if(searchCondition.equalsIgnoreCase("sal")) {
	    	  sql += String.format("sal = %s", searchKeyword );
	      }

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			String ename;
			String job;
			LocalDateTime hiredate;
			double sal;
			int deptno;

			if (rs.next()) {
				list = new ArrayList<EmpVO>();
			}
			do {
				ename = rs.getString("ename");
				job = rs.getString("job");
				hiredate = rs.getDate("hiredate").toLocalDate().atStartOfDay();
				sal = rs.getDouble("sal");
				deptno = rs.getInt("deptno");

				vo = EmpVO.builder()
						.ename(ename)
						.job(job)
						.hiredate(hiredate)
						.sal(sal)
						.deptno(deptno)
						.build();


			} while (rs.next());


			dispEmpInfo(list);


		}
		catch (SQLException e) {
			e.printStackTrace();
			
		}finally {
			try {
				rs.close();
				stmt.close();
				DBConn.close();
			} catch ( SQLException e) {
				e.printStackTrace();
			}
		}
         
		System.out.println("end");


	}

	private static void dispEmpInfo(ArrayList<EmpVO> list) {
		list.forEach(System.out :: println);

	}
}
