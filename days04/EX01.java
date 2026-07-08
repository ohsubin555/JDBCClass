package days04;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.doit.domain.EmpVO;

import com.util.DBConn;

public class EX01 {

	public static void main(String[] args) {

		String sql = """ 
				SELECT s.grade,
				s.losal,
				s.hisal,
				COUNT(e.empno) salCount
				FROM salgrade s
				LEFT JOIN emp e
				ON e.sal BETWEEN s.losal AND s.hisal
				GROUP BY s.grade, s.losal, s.hisal
				ORDER BY s.grade;
				""";

		String esql = """
				SELECT *
				FROM emp e LEFT OUTER JOIN dept d ON e.deptno = d.deptno
				JOIN salgrade s ON e.sal BETWEEN 
				""";

		Connection conn = null;
		PreparedStatement Pstmt = null, ePstmt = null; 
		ResultSet rs = null,eRs = null;
		EmpVO eVo = null;
		SAL sa = null;

		LinkedHashMap<SAL, ArrayList<EmpVO>> map = new LinkedHashMap<>();
		ArrayList<EmpVO> elist = null;


		int grade;

		int salCount;

		try {
			conn = DBConn.getConnection();
			Pstmt = conn.prepareStatement(sql);
			rs = Pstmt.executeQuery();



			if (rs.next()) {
				do { 

					grade = rs.getInt("grade");
					salCount = rs.getInt("salCount");	

					System.out.printf("%d등급 , %d명\n" , grade, salCount);

					try {
						Pstmt = conn.prepareStatement(esql);
						ePstmt.setInt(1, grade);
						eRs = ePstmt.executeQuery();

						String ename;

						if (eRs.next()) {
							elist = new ArrayList<EmpVO>();

							do {
								ename = eRs.getString("ename");


								eVo = EmpVO.builder()
										.ename(ename)
										.build();

								elist.add(eVo);


							} while (eRs.next());

						}


					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						try {
							eRs.close();
							ePstmt.close();

						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					map.put(sa, elist);

				} while (rs.next());
			}//if

			dispLinkedHashMap(map);


		} catch (SQLException e) {
			// TODO: handle exception
		}finally {
			try {
				rs.close();
				Pstmt.close();
				DBConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	private static void dispLinkedHashMap(LinkedHashMap<SAL, ArrayList<EmpVO>> map) {

		map.forEach((sa,elist)-> {
			System.out.printf("등급: %d, 수: %d", sa.getGrade(), sa.getsalCount());
			if (elist == null || elist.isEmpty()) {
				System.out.println("사원존재 x");

			} else {
				elist.forEach(eVo->{
					System.out.printf(" [%d]\t%s\t\n" , eVo.getEmpno() ,eVo.getEname(), eVo.getJob());

				});
			}

		});

	}
}

/*
 * /* [문제❓]
각 급여 등급 및 사원수 출력과
 ㄴ 하위에 속한 사원들 정보 출력하는 코딩.
 ㄴ (조건) 모든 출력 정보를 Map 안에 저장해서 출력하기

(출력결과)
1등급   ( 700 ~ 1200 ) - 3명
    부서번호 부서명    사원번호  사원명   SAL
   20   RESEARCH   7369   SMITH   800.00 
   20   RESEARCH   7876   ADAMS   1100.00 
   30   SALES   7900   JAMES   950.00 

2등급   ( 1201 ~ 1400 ) - 3명
   10   ACCOUNTING   7934   MILLER   1300.00 
   30   SALES   7521   WARD   1250.00 
   30   SALES   7654   MARTIN   1250.00 

3등급   ( 1401 ~ 2000 ) - 2명
   30   SALES   7499   ALLEN   1600.00 
   30   SALES   7844   TURNER   1500.00 

4등급   ( 2001 ~ 3000 ) - 5명
   10   ACCOUNTING   7782   CLARK   2450.00 
   20   RESEARCH   7566   JONES   2975.00 
   20   RESEARCH   7788   SCOTT   3000.00 
   20   RESEARCH   7902   FORD   3000.00 
   30   SALES   7698   BLAKE   2850.00 

5등급   ( 3001 ~ 9999 ) - 1명
   0   null   7839   KING   5000.00 

 end  
 */

