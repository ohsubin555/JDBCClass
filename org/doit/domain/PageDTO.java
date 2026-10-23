package org.doit.domain;

import java.sql.Connection;
import java.sql.SQLException;

import com.util.DBConn;

import days04.board.persistence.BoardDAO;
import days04.board.persistence.BoardDAOImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter

public class PageDTO {

	//
	//	int pageSize = 10;   // 한페이지에 출력할 게시글 수 
	//	int totalPages = 15; // 149/pageSize // 전체 페이지수
	//	int blockSize = 10; // page numver 수
	//	int startPage = 1 , endPage = 10;
	//	int currentPage ;
	//	

	private int startPage = 1 ;
	private int endPage = 10 ;
	private boolean prev ;
	private boolean next ;



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
