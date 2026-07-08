package days04;

import java.sql.Connection;

import com.util.DBConn;

import days04.board.controller.BoardController;
import days04.board.persistence.BoardDAO;
import days04.board.persistence.BoardDAOImpl;
import days04.board.service.BoardService;

public class EX02 {

	public static void main(String[] args) {
		
		Connection conn = DBConn.getConnection();
		BoardDAO dao = new BoardDAOImpl(conn);
		BoardService service = new BoardService(dao);
		BoardController controller = new BoardController(service);
		
		controller.boardStart();
		
		/*
		 * 1. 목록보기
		 * 2. 새글쓰기
		 * 3. 상세보기: 목록보기에서 보고자하는 게시글의 제목을 클릭
		 *           해당 글 번호의 게시글 정보를 조회해서 출력
		 * 
		 */
		
		
		
   /*
    *      days04.board.controller 패키지 추가      
    *      BoardController
       *   days04.board.service 패키지 추가      
       *   days04.board.persistence 패키지 추가
       *   days04.board.domain 패키지 추가
    */
   
	}

}
