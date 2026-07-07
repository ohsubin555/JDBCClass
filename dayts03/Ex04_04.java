package dayts03;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import com.util.DBConn;

public class Ex04_04 {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		int udeptno;
		String udname, uloc;
		
		System.out.print("> 수정할 부서번호 입력 ? ");//필수입력
		udeptno = Integer.parseInt(scanner.nextLine());
		
		System.out.print("> 수정할 부서번호 입력 ? "); // 엔터 "" 수정x
		udname = scanner.nextLine();
		
		System.out.print("> 수정할 지역명 입력 ? ");
		uloc = scanner.nextLine();
		
		System.out.println();
		
		String sql = """
				UPDATE dept 				
				""";
				if (udname.isEmpty() && !uloc.isEmpty()) {
					sql += "SET loc = ? ";
				} else if (!udname.isEmpty() && uloc.isEmpty()){
					sql += "SET dname = ? ";

				}else if (!udname.isEmpty() && !uloc.isEmpty()){
					sql += "SET dname = ? , loc = ? ";
				}sql += "WHERE deptno = ?";
				
				Connection conn = null;
				PreparedStatement pstmt = null; 
				int rowCount = 0;
				
				try {
					conn = DBConn.getConnection();
					pstmt = conn.prepareStatement(sql);
					
					if (udname.isEmpty() && !uloc.isEmpty()) {
						pstmt.setString(1, uloc);
					} else if (!udname.isEmpty() && uloc.isEmpty()){
						pstmt.setString(2, udname);

					}else if (!udname.isEmpty() && !uloc.isEmpty()){
						sql += "SET dname = ? , loc = ? ";
					}
					
					sql += "WHERE deptno = ?";
					
                    pstmt.execute();
					
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						
						pstmt.close();
						// 4
						DBConn.close();
					} catch (SQLException e) { 
						e.printStackTrace();
					}
				}


				System.out.println(" end ");


	}

}
