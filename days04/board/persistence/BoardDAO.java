package days04.board.persistence;

import java.sql.SQLException;
import java.util.List;

import days04.board.domain.BoardDTO;

public interface BoardDAO {
	
//1.목록보기 + 페이징 처리x 
  List<BoardDTO>select() throws SQLException;
  
 //1-2 목록보기 + 페이징 처리 o
  List<BoardDTO> select(int currentPage, int pageSize) throws SQLException;
  
//2.새 글
   int insert (BoardDTO dto) throws SQLException;
//3. 조회수 증가
   int increaseReaded(long seq) throws SQLException;
//3-2 상세보기
   BoardDTO view(long seq) throws SQLException;
   
//4 삭제
   int delete (long seq) throws SQLException;
	   
 //5 수정
   int update (BoardDTO dto) throws SQLException;
//6 검색
   List<BoardDTO> search(String searchCondition, String searchKeyword) throws SQLException;
//6-2 검색
   List<BoardDTO> search(String searchCondition, String searchKeyword, int currentPage, int pageSize) throws SQLException;
   
//7전체 페이지 수
   int getTotalPages(int pageSize) throws SQLException;
//검색 된 전체 페이지 수 
   int getTotalPages(int pageSize, String searchCondition, String searchKeyword) throws SQLException;

  

 

   
}




