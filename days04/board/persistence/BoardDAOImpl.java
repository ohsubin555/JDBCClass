package days04.board.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import days04.board.domain.BoardDTO;

public class BoardDAOImpl implements BoardDAO {

	private Connection conn = null;
	private PreparedStatement pstmt =null;
	private BoardDTO dto = null;
	private ResultSet rs = null;


	//생성자를 사용한 의존성 주입 (DI)
	public BoardDAOImpl(Connection conn) {
		this.conn = conn;
	}

	// setter 사용한 의존성 주입(DI)

	public void setConn (Connection conn) {
		this.conn = conn;
	}



	public Connection getConn() {
		return conn;
	}

	@Override
	public List<BoardDTO> select() throws SQLException {

		String sql = """
				SELECT seq, title, writer, email, writedate, readed
				FROM tbl_cstvsboard
				ORDER BY seq DESC
				""";

		List<BoardDTO> list = null;

		this.pstmt = this.conn.prepareStatement(sql);
		this.rs = this.pstmt.executeQuery();

		if (this.rs.next()) {
			list = new ArrayList<BoardDTO>();
			do {
				this.dto = BoardDTO.builder()
						.seq(this.rs.getInt("seq"))
						.writer(this.rs.getString("writer"))
						.title(this.rs.getString("title"))
						.email(this.rs.getString("email"))
						.writedate(this.rs.getDate("writedate"))
						.readed(rs.getInt("readed"))
						.build();
				list.add(dto);

			} while (this.rs.next());
		}


		if( this.rs != null ) this.rs.close();
		if( this.pstmt != null ) this.pstmt.close();
		return list;
	}

	@Override
	public int insert(BoardDTO dto) throws SQLException {
		String sql = """
				INSERT INTO tbl_cstvsboard ( seq, writer, pwd, email, title, tag, content)
				VALUES (seq_tblcstvsboard.NEXTVAL, ?, ?, ?, ?, ?, ?)
				""";

		int rowCount = 0;
		this.pstmt = this.conn.prepareStatement(sql);
		this.pstmt.setString(1, dto.getWriter());
		this.pstmt.setString(2, dto.getPwd());
		this.pstmt.setString(3, dto.getEmail());
		this.pstmt.setString(4, dto.getTitle());
		this.pstmt.setInt(5, dto.getTag());
		this.pstmt.setString(6, dto.getContent());

		rowCount = this.pstmt.executeUpdate();
		if(this.pstmt != null)
			this.pstmt.close();

		return rowCount;
	}

	@Override
	public int increaseReaded(long seq) throws SQLException {
		String sql = """
				UPDATE tbl_cstvsboard
				SET readed = readed + 1
				WHERE seq = ?
				""";

		int rowCount = 0;

		this.pstmt = this.conn.prepareStatement(sql);
		this.pstmt.setLong(1, seq);

		rowCount = this.pstmt.executeUpdate();
		if(this.pstmt != null)
			this.pstmt.close();

		return rowCount;

	}

	@Override
	public BoardDTO view(long seq) throws SQLException {

		String sql = """
				SELECT seq, title, writer, email, writedate, readed, content
				FROM tbl_cstvsboard
				WHERE seq = ?
				""";

		BoardDTO dto = null;

		this.pstmt = this.conn.prepareStatement(sql);
		this.pstmt.setLong(1, seq);
		this.rs = this.pstmt.executeQuery();

		if (this.rs.next()) {

			dto = BoardDTO.builder()
					.seq(this.rs.getInt("seq"))
					.writer(this.rs.getString("writer"))
					.title(this.rs.getString("title"))
					.email(this.rs.getString("email"))
					.writedate(this.rs.getDate("writedate"))
					.readed(rs.getInt("readed"))
					.content(this.rs.getString("content"))
					.build();
		}


		if( this.rs != null ) this.rs.close();
		if( this.pstmt != null ) this.pstmt.close();



		return dto;

	}

	@Override
	public int delete(long seq) throws SQLException {

		String sql = """
				DELETE FROM tbl_cstvsboard 
				WHERE seq = ? 
				""";

		int rowCount = 0;

		this.pstmt = this.conn.prepareStatement(sql);


		this.pstmt.setLong(1, seq);


		rowCount = this.pstmt.executeUpdate();
		if(this.pstmt != null)
			this.pstmt.close();

		return rowCount;


	}

	@Override
	public int update(BoardDTO dto) throws SQLException {
		String sql = """
				UPDATE tbl_cstvsboard 
				SET email = ? , title = ?, tag = ?, content = ?
				WHERE seq = ?
				""";

		int rowCount = 0;
		this.pstmt = this.conn.prepareStatement(sql);


		this.pstmt.setString(1, dto.getEmail());
		this.pstmt.setString(2, dto.getTitle());
		this.pstmt.setInt(3, dto.getTag());
		this.pstmt.setString(4, dto.getContent());
		this.pstmt.setLong(5, dto.getSeq());

		rowCount = this.pstmt.executeUpdate();
		if(this.pstmt != null)
			this.pstmt.close();

		return rowCount;

	}

	@Override
	public List<BoardDTO> search(String searchCondition, String searchKeyword) throws SQLException {

		String sql = """
				SELECT seq, title, writer, email, writedate, readed
				FROM tbl_cstvsboard
				WHERE 1 = 1 AND 
				""";

		//"t" "c" "w" "tc" "tcw" 등등
		String [] scArr = searchCondition.split("");
		int len = scArr.length-1;
		for (int i = 0; i <= len; i++) {
			if (scArr [i].equals("t")) {
				sql += "REGEXP_LIKE( title, ? , 'i') ";
			}else if (scArr [i].equals("c")) {
				sql += "REGEXP_LIKE( contetn, ? ,'i') ";
			}else if (scArr [i].equals("w")) {
				sql += "REGEXP_LIKE( writer, ? , 'i') ";
			}

			if(i != len ) sql += " OR ";
		} // for

		sql += " ORDER BY seq DESC";

		//		System.out.println(sql);

		List<BoardDTO> list = null;
		this.pstmt = this.conn.prepareStatement(sql);

		//? 파라미터 마크 갯수만큼 추가..
		for (int i = 0; i <= len; i++) {
			pstmt.setString(i+1, searchKeyword);
		}

		this.rs = this.pstmt.executeQuery();

		if (this.rs.next()) {
			list = new ArrayList<BoardDTO>();
			do {
				this.dto = BoardDTO.builder()
						.seq(this.rs.getInt("seq"))
						.writer(this.rs.getString("writer"))
						.title(this.rs.getString("title"))
						.email(this.rs.getString("email"))
						.writedate(this.rs.getDate("writedate"))
						.readed(rs.getInt("readed"))
						.build();
				list.add(dto);

			} while (this.rs.next());
		}


		if( this.rs != null ) this.rs.close();
		if( this.pstmt != null ) this.pstmt.close();
		return list;

	}

	@Override
	public int getTotalPages(int pageSize) throws SQLException {
		String sql = """
				SELECT CEIL(COUNT(*) / ?)
				FROM tbl_cstvsboard
				 """;

		int totalPages = 0;
		ResultSet rs = null;
		try (
				PreparedStatement pstmt = conn.prepareStatement(sql);	    	
				) {
			pstmt.setInt(1, pageSize);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				totalPages = rs.getInt(1);
			}
		} finally {
			rs.close();
		}

		return totalPages;
	}

	@Override
	public List<BoardDTO> select(int currentPage, int pageSize) throws SQLException {
		int startNo = (currentPage - 1) * pageSize + 1;
		int endNo = currentPage * pageSize;

		String sql = """
				SELECT seq, title, writer, email, writedate, readed
				FROM (
				    SELECT ROW_NUMBER() OVER(ORDER BY seq DESC) no,
				           seq,
				           title,
				           writer,
				           email,
				           writedate,
				           readed
				    FROM tbl_cstvsboard
				)
				WHERE no BETWEEN ? AND ?
				""";

		List<BoardDTO> list = null;

		this.pstmt = this.conn.prepareStatement(sql);
		this.pstmt.setInt(1, startNo);
		this.pstmt.setInt(2, endNo);
		this.rs = this.pstmt.executeQuery();

		if (this.rs.next()) {
			list = new ArrayList<BoardDTO>();
			do {
				this.dto = BoardDTO.builder()
						.seq(this.rs.getInt("seq"))
						.writer(this.rs.getString("writer"))
						.title(this.rs.getString("title"))
						.email(this.rs.getString("email"))
						.writedate(this.rs.getDate("writedate"))
						.readed(rs.getInt("readed"))
						.build();
				list.add(dto);

			} while (this.rs.next());
		}


		if( this.rs != null ) this.rs.close();
		if( this.pstmt != null ) this.pstmt.close();
		return list;
	}

	//페이징 처리 포함된 검색함수
	@Override
	public List<BoardDTO> search(String searchCondition, String searchKeyword, int currentPage, int pageSize)
			throws SQLException {

		int startNo = (currentPage - 1) * pageSize + 1;
		int endNo = currentPage * pageSize;

		String sql = """
				SELECT seq, title, writer, email, writedate, readed
				FROM ( 
				    SELECT 
				       ROW_NUMBER() OVER( ORDER BY seq DESC ) no
				       , seq, title, writer, email, writedate, readed
				    FROM tbl_cstvsboard
				    WHERE 1 = 1 AND
				    """;
		// "t"  "c"  "w"  "tc"  "tcw" 등등
		String [] scArr =  searchCondition.split("");
		int len = scArr.length-1;
		for (int i = 0; i <= len; i++) {
			if ( scArr[i].equals("t")) {
				sql += " REGEXP_LIKE( title, ? , 'i' ) ";
			} else if ( scArr[i].equals("c")) {
				sql += " REGEXP_LIKE( content, ? , 'i' ) ";
			}else if ( scArr[i].equals("w")) {
				sql += " REGEXP_LIKE( writer, ? , 'i' ) ";
			}
			if(i != len ) sql += " OR ";
		} // for
		sql +=   """   
				)
				WHERE no BETWEEN ? AND ?
				""";

		sql += " ORDER BY seq DESC";

		System.out.println( sql );

		List<BoardDTO> list = null;
		this.pstmt = this.conn.prepareStatement(sql);

		//? 파라미터 마크 갯수만큼 추가..
		int i = 0;
		for ( i = 0; i <= len; i++) {
			pstmt.setString(i+1, searchKeyword);
		}
		pstmt.setInt(++i, startNo);
		pstmt.setInt(++i, endNo);

		this.rs = this.pstmt.executeQuery();

		if (this.rs.next()) {
			list = new ArrayList<BoardDTO>();
			do {
				this.dto = BoardDTO.builder()
						.seq(this.rs.getInt("seq"))
						.writer(this.rs.getString("writer"))
						.title(this.rs.getString("title"))
						.email(this.rs.getString("email"))
						.writedate(this.rs.getDate("writedate"))
						.readed(rs.getInt("readed"))
						.build();
				list.add(dto);

			} while (this.rs.next());
		}


		if( this.rs != null ) this.rs.close();
		if( this.pstmt != null ) this.pstmt.close();
		return list;

	}

	public int getTotalPages(int pageSize, String searchCondition, String searchKeyword) throws SQLException {
		
		String sql = """
				SELECT CEIL(COUNT(*) / ?)
				FROM tbl_cstvsboard
	            WHERE 1 = 1 AND 
				""";

		//"t" "c" "w" "tc" "tcw" 등등
		String [] scArr = searchCondition.split("");
		int len = scArr.length-1;
		for (int i = 0; i <= len; i++) {
			if (scArr [i].equals("t")) {
				sql += "REGEXP_LIKE( title, ? , 'i') ";
			}else if (scArr [i].equals("c")) {
				sql += "REGEXP_LIKE( contetn, ? ,'i') ";
			}else if (scArr [i].equals("w")) {
				sql += "REGEXP_LIKE( writer, ? , 'i') ";
			}

			if(i != len ) sql += " OR ";
		} // for


		int totalPages = 0;
		ResultSet rs = null;
		try (
				PreparedStatement pstmt = conn.prepareStatement(sql);	    	
				) {
			pstmt.setInt(1, pageSize);
			//? 파라미터 마크 갯수만큼 추가..
			for (int i = 0; i <= len; i++) {
				pstmt.setString(i+2, searchKeyword);
			}//FOR
			
			System.out.println(sql);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				totalPages = rs.getInt(1);
			}
		} finally {
			rs.close();
		}

		return totalPages;
	}

}

///* 더 좋은 코딩
//@Override
//public List<BoardDTO> select() throws SQLException {
//
//    String sql = """
//            SELECT seq,
//                   title,
//                   writer,
//                   email,
//                   writedate,
//                   readed
//              FROM tbl_cstvsboard
//             ORDER BY seq DESC
//            """;
//
//    List<BoardDTO> list = new ArrayList<>();
//
//    try (
//        PreparedStatement pstmt = conn.prepareStatement(sql);
//        ResultSet rs = pstmt.executeQuery()
//    ) {
//
//        while (rs.next()) {
//
//            BoardDTO dto = BoardDTO.builder()
//                    .seq(rs.getInt("seq"))
//                    .title(rs.getString("title"))
//                    .writer(rs.getString("writer"))
//                    .email(rs.getString("email"))
//                    .writedate(rs.getDate("writedate"))
//                    .readed(rs.getInt("readed"))
//                    .build();
//
//            list.add(dto);
//        }
//    }
//
//    return list;
//}
//*/