package days04.board.service;

import java.sql.SQLException;
import java.util.List;

import days04.board.domain.BoardDTO;
import days04.board.persistence.BoardDAO;
import days04.board.persistence.BoardDAOImpl;

//선택. 트랜잭션 처리
public class BoardService {

	private BoardDAO dao = null;

	public BoardService(BoardDAO dao) {
		super();
		this.dao = dao;
	}
	
	public BoardDAO getDao() {
		return dao;
	}


    //setter di
	public void setDao(BoardDAO dao) {
		this.dao = dao;
	}

	// 2. 새글 서비스 메서드
	
	public int insertService(BoardDTO dto) {
		int rowCount = 0;
		
		
		try {
			((BoardDAOImpl)this.dao).getConn().setAutoCommit(false);
			
			//1. 로그 기록
			System.out.println("게시글 새글: 로그 기록 작업...");
			
		
			//2. DAO 새글 INSERT
			rowCount = this.dao.insert(dto);
			
			((BoardDAOImpl)this.dao).getConn().commit();
			
			
		} catch (Exception e) {
			try {
				((BoardDAOImpl)this.dao).getConn().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally {
			try {
				((BoardDAOImpl)this.dao).getConn().setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return rowCount;
	}
	
	
	//목록보기 서비스 메서드
	public List<BoardDTO> selectService(){
		
		List<BoardDTO> list = null;
		
		try {
			((BoardDAOImpl)this.dao).getConn().setAutoCommit(false);
			
			//1. 로그 기록
			System.out.println("게시글 목록: 로그 기록 작업...");
			
			//3. 메일/문자 전송
			System.out.println("게시글 목록: 메일/문자 전송 작업..");
			
			//2. DAO 목록 SELECT
			list = this.dao.select();
			
			((BoardDAOImpl)this.dao).getConn().commit();
			
			
		} catch (Exception e) {
			try {
				((BoardDAOImpl)this.dao).getConn().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally {
			try {
				((BoardDAOImpl)this.dao).getConn().setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return list;

	}
    //3 상세보기 서비스 메서드 
	public BoardDTO viewService(long seq) {
		
		
		
		BoardDTO dto = null;
		
		
		try {
			((BoardDAOImpl)this.dao).getConn().setAutoCommit(false);
			
			//1. 로그 기록
			System.out.println("게시글 상세보기: 로그 기록 작업...");
			
			//2-1 조회수 1증가
		    int rowCount = this.dao.increaseReaded(seq);
			//2-2. DAO 상세보기
			dto = this.dao.view(seq);
			
			((BoardDAOImpl)this.dao).getConn().commit();
			
			
		} catch (Exception e) {
			try {
				((BoardDAOImpl)this.dao).getConn().rollback();
				e.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally {
			try {
				((BoardDAOImpl)this.dao).getConn().setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		  
		return dto;
	}
}
