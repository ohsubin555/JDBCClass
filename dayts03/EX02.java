package dayts03;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;

import org.doit.domain.DeptVO;
import org.doit.domain.EmpVO;

import com.util.DBConn;

/**
 * @author subin 
 * @date 2026. 7. 6. 오후 3:17:58
 * @subject
 * @content 1. 부서정보 출력
 *          2. 부서번호를 선택하세요? 20
 *          3. 20번 부서원들을 출력. 
 *          
 *          
 */
public class EX02 {

	public static void main(String[] args) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 
		ArrayList <DeptVO> list = null;
		DeptVO vo = null;

		//1+2
		conn = DBConn.getConnection();

		String sql = """
				 SELECT *
				 FROM dept
				 ORDER BY deptno ASC

				""";

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			int deptno;
			String dname, loc;

			if (rs.next()) {// 첫번째 레코드는 존재한다. 
				list = new ArrayList<DeptVO>();

			}

			do {
				deptno = rs.getInt("deptno");
				dname = rs.getString("dname");
				loc = rs.getString("loc");

				vo = DeptVO.builder()
						.deptno(deptno)
						.dname(dname)
						.loc(loc)
						.build();
  
				list.add(vo);
			} while (rs.next());



			dispDeptInfo(list);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
				// 4
				
			} catch (SQLException e) { 
				e.printStackTrace();
			}
		}


		//2
		Scanner scanner = new Scanner (System.in);
		System.out.println(">부서번호(deptno) 입력?");
		int pDeptno = scanner.nextInt();

		//3 해당 부서원들 조회

		ArrayList<EmpVO> elist = null;
		EmpVO evo = null;


		sql = """
				SELECT *
				FROM emp
				WHERE deptno= ? 
						ORDER BY empno ASC 
						""";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, pDeptno);
			rs = pstmt.executeQuery();

			// Alt + Shift + A
			int empno;
			String ename;
			String job;
			int mgr;
			LocalDateTime hiredate;  
			double sal;
			double comm;
			int deptno;

			if ( rs.next()  ) {  // 첫 번째 레코드는 존재한다.
				elist = new ArrayList<EmpVO>();
				do {
					empno = rs.getInt("empno");
					ename = rs.getString("ename");
					job = rs.getString("job");
					mgr = rs.getInt("mgr");
					// [ LocalDateTime hiredate; ]  
					// String hiredate = rs.getString("hiredate");
					// Date  hiredate = rs.getDate("hiredate");
					hiredate = rs.getDate("hiredate").toLocalDate().atStartOfDay();   
					// LocalDate -> LocalDateTime 변환
					// hd.atStartOfDay() == hd.atTime(0, 0, 0)               
					sal = rs.getDouble("sal");
					comm = rs.getDouble("comm");
					deptno = rs.getInt("deptno");

					evo = EmpVO.builder()
							.empno(empno)
							.ename(ename)
							.job(job)
							.MGR(mgr)
							.hiredate(hiredate)
							.sal(sal)
							.comm(comm)
							.deptno(deptno)
							.build();

					//                 System.out.println( vo );
					elist.add(evo);
				} while (rs.next() );
			} // if


			dispEmpInfo(elist,pDeptno);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
				// 4
				DBConn.close();
			} catch (SQLException e) { 
				e.printStackTrace();
			}
		} 

		System.out.println(" end ");

	} // main

	private static void dispDeptInfo(ArrayList<DeptVO> list) {
		list.forEach(System.out::println);

	}
	
	private static void dispEmpInfo(ArrayList<EmpVO> elist,int deptno) { 
		try {
			int deptEmpCount = elist.size();
			System.out.printf("[%d] 부서원(%d명) 조회\n", deptno, deptEmpCount);
			// 메서드 참조
			elist.forEach( System.out::println ); 
			
		} catch (NullPointerException e) {
			System.out.printf("[%d] 부서원(%d명) 조회\n", deptno, 0);
			System.out.println("사원이 존재하지 않습니다. ");
			
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

} // class