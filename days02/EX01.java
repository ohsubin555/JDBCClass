package days02;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.doit.domain.EmpVO;

import com.util.DBConn;

public class EX01 {

	public static void main(String[] args) {
		/*
		 * 1. JDBC Driver 메모리 로딩
		 * Class.forName("oracle.jdbc.driver.OracleDriver");
		 * 2. DB연결 -> CONNECTION 객체 생성
		 * DriverManager.getConnection(url,user,password);
		 * 3. 쿼리 작성-> 실행 -> 결과
		 * String sql = "SELECT * FROM dept";
		 * Statement stmt = conn.createStatement();
		 * Result rs = stmt.executeQuery();
		 * 
		 * while (boolean rs.next() ) {
		 *     int deptno = rs.getInt("deptno");
		 *     //부서명 
		 *     //지역명 
		 * 
		 * 4. DB 연결 해제(닫기)
		 * stmt.close()
		 * conn.close()
		 *
		 */
		
		/*
		 * DBConn.java 싱글톤
		 * ㄴ Connect getConnection(){}
		 * ㄴ Connect getConnection(String url, String user, String password){}
		 * 
		 * 
		 * 
		 */
		
		//모든 사원 정보를 조회해서 ArrayList<>에 저장하고 출력하는 메서드를 만들어 출력
		// org.doit.domain.Empeo.java
		//dispEmpInfo() 메서드 구현
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList <EmpVO> list = null;
		EmpVO eo = null;
		
		conn = DBConn.getConnection();
		
		String sql = """
				    SELECT *
				    FROM emp
				    ORDER BY empno ASC
				    """;
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			 int empno;
			 String ename;
			 String job;
			 int mgr;
			 LocalDateTime hiredate; //String , Date
			 double sal;
			 double comm;
			 int deptno;
			 
			 if (rs.next()) {// 첫번째 레코드는 존재한다. 
			 	list = new ArrayList<EmpVO>();

			}

			do {
				empno = rs.getInt("empno");
				ename = rs.getString("ename");
				job = rs.getString("job");
				mgr = rs.getInt("mgr");
				hiredate =	rs.getDate("hiredate").toLocalDate().atStartOfDay(); 
				//String hiredate = rs.getString("hiredate")
				// Date hiredate = rs.getDate("hiredate")	
				//LocalDate -> LocalDateTime 변환
//				hd.atStartOfDay();
//				hd.atTime(0,0,0);
				
				sal = rs.getDouble("sal");
				comm = rs.getDouble("comm");
				deptno = rs.getInt("deptno");
				
				
				
				eo = EmpVO.builder()
						.empno(empno)
						.ename(ename)
						.job(job)
						.MGR(mgr)
						.hiredate(hiredate)
						.sal(sal)
						.comm(comm)
						.deptno(deptno)
						.build();
				//		            System.out.println( eo );
				list.add(eo);
			} while (rs.next());



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
		
		

		System.out.println(" end ");
	} // main

	private static void dispEmpInfo(ArrayList<EmpVO> list) {
		//1. 반복자 처리
		//		Iterator <Empeo> ir= list.iterator();
		//		while (ir.hasNext()) {
		//		Empeo eo = ir.next();
		//		System.out.println(eo);
		//			

		list.forEach(new Consumer<EmpVO> () {
			public void accept(EmpVO eo) {
				System.out.println(eo);
			}
		});

		list.forEach(eo-> System.out.println(eo));;

		list.forEach(System.out::println);

	}


} // class

		

		
	
