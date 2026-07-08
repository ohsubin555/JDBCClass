package days04.board.persistence;

import java.sql.SQLException;
import java.util.List;

import days04.board.domain.BoardDTO;

public interface BoardDAO {
//1.목록보기 + 페이징 처리x 
  List<BoardDTO>select() throws SQLException;
  
//2.새 글
   int insert (BoardDTO dto) throws SQLException;
//3. 조회수 증가
   int increaseReaded(long seq) throws SQLException;
//3-2 상세보기
   BoardDTO view(long seq) throws SQLException;
	
}

