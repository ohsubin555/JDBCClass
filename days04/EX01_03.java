package days04;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.doit.domain.EmpVO;
import org.doit.domain.SalgradeVO;

import com.util.DBConn;

public class EX01_03 {
	public static void main(String[] args) {

	      LinkedHashMap<SalgradeVO, ArrayList<EmpVO>> map = new LinkedHashMap<>();
	      ArrayList<EmpVO> elist = null; // value

	      Connection conn = null;
	      PreparedStatement pstmt = null;
	      ResultSet rs = null;

	      conn = DBConn.getConnection();
	      String sql = """
	            SELECT empno, ename, hiredate, sal + NVL(comm, 0) pay , dname , e.deptno  ,sal , grade , losal, hisal
	                FROM emp e LEFT JOIN dept d ON e.deptno = d.deptno
	                JOIN salgrade s ON sal BETWEEN losal AND hisal
	            order by grade
	               """;
	      int rowCount = 0;

	      EmpVO eVo = null;
	      int oldGrade = -1, grade=0;
	      SalgradeVO sVo = null;

	      try {
	         pstmt = conn.prepareStatement(sql);
	         rs = pstmt.executeQuery();

	         if (rs.next()) {
	            do {
	               grade = rs.getInt("grade");
	               eVo = EmpVO.builder()
	                     .empno(rs.getInt("empno"))
	                     .ename(rs.getString("ename"))
	                     .deptno(rs.getInt("deptno"))
	                     .sal(rs.getDouble("sal"))
//	                     .dname(rs.getString("dname"))
	                     .build();
   
	               if(grade != oldGrade) {
	                  sVo = SalgradeVO.builder()
	                        .grade(grade)
	                        .losal(rs.getInt("losal"))
	                        .hisal(rs.getInt("hisal"))
	                        .build();
	                  elist = new ArrayList<>();
	                  elist.add(eVo);
	                  map.put(sVo, elist);
	                  oldGrade = grade;
	               }
	               else {
	                  elist.add(eVo);
	                  
	               }
	            } while (rs.next());
	         }

	         displayGradeEmp(map);
	      } catch (SQLException e) {
	         e.printStackTrace();
	      } finally {
	         try {
	            rs.close();
	         } catch (SQLException e) {
	            e.printStackTrace();
	         }

	         try {
	            pstmt.close();
	         } catch (SQLException e) {
	            e.printStackTrace();
	         }

	         DBConn.close();
	      }

	   }

	   private static void displayGradeEmp(LinkedHashMap<SalgradeVO, ArrayList<EmpVO>> map) {
	      map.forEach((sVo,elist)-> {
	         System.out.println("====================================");;
	         System.out.printf("%d 등급 (%d ~ %d) - %d명\n",sVo.getGrade(),sVo.getLosal(),sVo.getHisal(),elist.size());
	            if (elist == null || elist.isEmpty()) {
	               System.out.println("사원 존재 X");
	            } else {
	               System.out.printf("부서번호 부서명 사원번호 사원명 SAL \n");
	               elist.forEach(eVo -> {
	                  System.out.printf("%d %s %d %s %6.2f\n", eVo.getDeptno(),"X",eVo.getEmpno(),eVo.getEname(),eVo.getSal());
	               });
	            }
	      });
	   }
}
