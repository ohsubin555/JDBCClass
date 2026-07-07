package dayts03;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Scanner;

import org.doit.domain.DeptVO;

import com.util.DBConn;

public class eX07_04 {

	public static void main(String[] args) {
		//1.
				Scanner scanner = new Scanner(System.in);
				String searchCondition, searchKeyword ;

				System.out.println(">검색조건, 검색어 입력?");
				searchCondition = scanner.next(); //d, n, l, dl
				searchKeyword = scanner.next();


				//2.

				Connection conn = null;
				CallableStatement cstmt = null; 
				ResultSet rs = null; 
				ArrayList <DeptVO> list = null;
				DeptVO vo = null;

				//1+2
				conn = DBConn.getConnection();

				String sql = "{ call up_searchdept(?,?,?) }";
				try {
					cstmt = conn.prepareCall(sql);
					
					cstmt.setString(1, searchCondition);
					cstmt.setString(2, searchKeyword);
					cstmt.registerOutParameter(3, Types.REF_CURSOR);			
					cstmt.execute();			
					rs = (ResultSet) cstmt.getObject(3);

					int deptno;
					String dname, loc;

					if ( rs.next()  ) {  // 첫 번째 레코드는 존재한다.
						list = new ArrayList<DeptVO>();
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
						} while (rs.next() );
					} // if


					dispDeptInfo(list);

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						rs.close();
						cstmt.close();
						// 4
						DBConn.close();
					} catch (SQLException e) { 
						e.printStackTrace();
					}
				}


				System.out.println(" end ");
			} // main

			private static void dispDeptInfo(ArrayList<DeptVO> list) {
				list.forEach( System.out::println );

			}

		} // class


		/*
		CREATE OR REPLACE PROCEDURE up_searchdept
		(
		   psearchCondition VARCHAR2, 
		   psearchKeyword VARCHAR2,
		   pcur OUT SYS_REFCURSOR
		)
		IS
		  vsql VARCHAR2(1000);  -- 동적 쿼리
		BEGIN
		  vsql := 'SELECT * FROM dept WHERE ';
		  
		  IF psearchCondition = 'd' THEN
		    vsql := vsql || 'deptno = :1';
		  ELSIF psearchCondition = 'n' THEN
		    vsql := vsql || 'REGEXP_LIKE( dname, :1, ''i'') ';
		  ELSIF psearchCondition = 'l' THEN
		    vsql := vsql || 'REGEXP_LIKE( loc, :1, ''i'') ';
		  ELSIF psearchCondition = 'nl' THEN
		    vsql := vsql || 'REGEXP_LIKE( dname, :1, ''i'') OR REGEXP_LIKE( loc, :2, ''i'') ';
		  ELSE 
		        RAISE_APPLICATION_ERROR(
		            -20002,
		            '잘못된 검색조건입니다.'
		        );
		  END IF;
		  vsql := vsql || ' ORDER BY deptno ASC ';
		  
		  IF psearchCondition = 'nl' THEN
		    OPEN pcur FOR vsql USING psearchKeyword, psearchKeyword;
		  ELSE
		    OPEN pcur FOR vsql USING psearchKeyword;
		  END IF; 
		  
		EXCEPTION
		 WHEN OTHERS THEN
		   RAISE;
		END;
		*/

