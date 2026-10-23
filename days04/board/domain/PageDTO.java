package days04.board.domain;

import java.sql.Connection;
import java.sql.SQLException;

import com.util.DBConn;

import days04.board.persistence.BoardDAO;
import days04.board.persistence.BoardDAOImpl;
import lombok.Getter;

@Getter 
public class PageDTO {
	
//	private int pageSize = 10;   // 한 페이지에 출력할 게시글 수
//	private int totalPages = 15; // 전체 페이지 수 
//	private int blockSize = 10;  // Page Number 수 [1]  2  3  4  5  6  7  8  9  10 
//	private int currentPage= 1;  // 현재 페이지번호
		
	private int startPage=1;
	private int endPage=10;
	private boolean prev;
	private boolean next;
	
	public PageDTO(int currentPage, int pageSize, int blockSize) {
		Connection conn = DBConn.getConnection();
		BoardDAO dao = new BoardDAOImpl(conn);
		
		int totalPages;
		try {
			totalPages = dao.getTotalPages(pageSize);
			this.startPage = ((currentPage - 1) / blockSize) * blockSize + 1; 
			this.endPage = Math.min(startPage + blockSize - 1, totalPages);
			
			this.prev = startPage == 1 ? false : true;
			this.next = endPage == totalPages ? false: true;
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
	}

	public PageDTO(int currentPage, int pageSize, int blockSize, String searchCondition, String searchKeyword) {
		Connection conn = DBConn.getConnection();
		BoardDAO dao = new BoardDAOImpl(conn);
		
		int totalPages;
		try {
			totalPages = dao.getTotalPages(pageSize, searchCondition, searchKeyword);
			this.startPage = ((currentPage - 1) / blockSize) * blockSize + 1; 
			this.endPage = Math.min(startPage + blockSize - 1, totalPages);
			
			this.prev = startPage == 1 ? false : true;
			this.next = endPage == totalPages ? false: true;
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	

}
