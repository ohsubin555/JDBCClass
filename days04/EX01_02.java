package days04;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.util.DBConn;

/**
 * @author kEnik  
 * @date 2026. 7. 8. 오전 7:05:39
 * @subject (복습문제)  
 * @content 각 급여 등급 및 사원수 출력과
 *           ㄴ 하위에 속한 사원들 정보 출력하는 코딩.
 *           ㄴ (조건) 모든 출력 정보를 Map 안에 저장해서 출력하기
 *           Ex01_02.java
 */
public class EX01_02 {

   public static void main(String[] args) {   
      
      String sql = """
               SELECT s.grade, s.losal, s.hisal
                  , COUNT(e.empno) empCount
               FROM salgrade s JOIN emp e ON e.sal BETWEEN s.losal AND s.hisal
               GROUP BY s.grade, s.losal, s.hisal
               ORDER BY s.grade ASC
                """;
      String eSql = """
               SELECT d.deptno, d.dname, e.empno, e.ename, e.sal
               FROM emp e LEFT OUTER JOIN dept d ON e.deptno = d.deptno
                          JOIN salgrade s ON e.sal BETWEEN s.losal AND s.hisal
               WHERE s.grade = ?
               ORDER BY d.deptno, e.empno
                """;
      try (
            Connection conn = DBConn.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
         ){
         
         int grade, losal, hisal, empCount;
         
         if (rs.next()) {
            
            ResultSet eRs = null;
            int deptno;
            String dname;
            int empno;
            String ename;
            double sal;
            
            do {
               grade = rs.getInt("grade");
               losal = rs.getInt("losal");
               hisal = rs.getInt("hisal");
               empCount = rs.getInt("empCount");
               System.out.printf("%d등급   ( %d ~ %d ) - %d명\n",  
                     grade, losal, hisal, empCount);
               // 시작
               try (
                     PreparedStatement ePstmt = conn.prepareStatement(eSql);   
                  ){
                  ePstmt.setInt(1, grade);
                  eRs = ePstmt.executeQuery();
                  
                  if ( eRs.next()) {
                     do {
                        // d.deptno, d.dname, e.empno, e.ename, e.sal
                        deptno = eRs.getInt("deptno");
                        dname = eRs.getString("dname");
                        empno = eRs.getInt("empno");
                        ename = eRs.getString("ename");
                        sal = eRs.getDouble("sal");
                        System.out.printf("   %d\t%s\t%d\t%s\t%.2f\n", deptno, dname, empno, ename, sal);
                     } while ( eRs.next());
                  } // if
                  
               } catch (Exception e) {
                  e.printStackTrace();
               } finally {
                  eRs.close();
               } 
               // try
               // 끝
            } while (rs.next());
         } // if
         
       
         
      } catch (Exception e) {
         e.printStackTrace();
      } // try   
      
       
      System.out.println(" end ");
   } // main
 
} // class










/* [문제❓]
각 급여 등급 및 사원수 출력과
 ㄴ 하위에 속한 사원들 정보 출력하는 코딩.
 ㄴ (조건) 모든 출력 정보를 Map 안에 저장해서 출력하기

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