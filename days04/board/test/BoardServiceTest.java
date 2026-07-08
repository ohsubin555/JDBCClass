package days04.board.test;

import java.sql.Connection;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.util.DBConn;

import days04.board.domain.BoardDTO;
import days04.board.persistence.BoardDAO;
import days04.board.persistence.BoardDAOImpl;
import days04.board.service.BoardService;

class BoardServiceTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		
		
	}

	@BeforeEach
	void setUp() throws Exception {
		
		Connection conn = DBConn.getConnection();
		BoardDAO dao = new BoardDAOImpl(conn);
		this.boardService = new BoardService(dao);
	}

	@AfterEach
	void tearDown() throws Exception {
		
		DBConn.close();
	}

	private BoardService boardService;
	
	@Test
	void testSelectService() {
		List<BoardDTO> list = this.boardService.selectService();
		System.out.println(list ==null);
//		list.forEach(System.out::println);
	}

}
