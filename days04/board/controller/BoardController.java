package days04.board.controller;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.doit.domain.PageDTO;

import com.util.DBConn;

import days04.board.domain.BoardDTO;
import days04.board.service.BoardService;

//컨트롤러 + VIEW 화면 입출력
public class BoardController {

	private Scanner scanner = null;
	private int selectedNumber;

	private BoardService boardService = null;

	public BoardController() {
		this.scanner = new Scanner(System.in);
	}

	public BoardController(BoardService boardService) {
		this();
		this.boardService = boardService;
	}

	//게시판 메뉴 열거

	public enum Board{

		NEW, //새 글
		LIST, // 목록
		VIEW, // 상세보기
		EDIT, // 수정
		DELETE,//삭제
		SEARCH,//검색
		EXIT//종료
	}

	// 게시판 기능 시작
	public void boardStart() {



		while (true) {
			//무한루프

			메뉴출력();
			메뉴선택();
			메뉴처리();
		}//while

	}

	private void 메뉴처리() {
		Board selectedMenu = Board.values()[this.selectedNumber-1];

		switch (selectedMenu) {

		case NEW: 새글쓰기();
		break;
		case LIST: 목록보기();
		break;
		case VIEW: 상세보기();
		break;
		case EDIT: 수정하기();
		break;
		case DELETE: 삭제하기();
		break;
		case SEARCH: 검색하기();
		break;
		case EXIT: 종료하기();
		break;

		}

		일시정지();
	}

	private void 일시정지() {
		System.out.println(" \t\t 계속하려면 엔터치세요.");
		try {
			System.in.read();
			System.in.skip(System.in.available()); // 13, 10
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}


	private void 종료하기() {
		DBConn.close();
		System.out.println("\t\t\t 프로그램 종료!!");
		System.exit(-1);
	}

	private void 검색하기() {
		System.out.println("검색조건: 제목 (t), 내용(c) , 작성자(w), 제목+내용(tc) 입력?");
		String searchCondition = this.scanner.nextLine();

		System.out.println("검색어 입력?");
		String searchKeyword = this.scanner.nextLine();

		System.out.println("현재 페이지 번호 입력");
		this.currentPage = this.scanner.nextInt();

//		List<BoardDTO> list = this.boardService.searchService(searchCondition,searchKeyword);

		List<BoardDTO> list = this.boardService.searchService(searchCondition,searchKeyword, currentPage, pageSize);
        
		//뷰역할 컨트롤러에서 하기 
		System.out.println("\t\t\t  게시판");
		System.out.println("-------------------------------------------------------------------------");
		System.out.printf("%s\t%-40s\t%s\t%-10s\t%s\n", 
				"글번호","글제목","글쓴이","작성일","조회수");
		System.out.println("-------------------------------------------------------------------------");
		if (list == null) {
			System.out.println("\t\t 게시글이 존재하지 않습니다.");
		}else {
			list.forEach(dto -> {
				System.out.printf("%d\t%-30s  %s\t%-10s\t%d\n",
						dto.getSeq(), 
						dto.getTitle(),
						dto.getWriter(),
						dto.getWritedate(),
						dto.getReaded());
			});			
		}//if
		//페이징 처리하는 부분이다
		System.out.println("-------------------------------------------------------------------------");
//		System.out.println("\t\t[1] 2 3 ");
		PageDTO pageDTO = new PageDTO (currentPage, pageSize, blockSize);
		System.out.print("\t\t");
		if (pageDTO.isPrev()) {
			System.out.println(" ◀ ");
		}
		
		for (int i = pageDTO.getStartPage(); i <= pageDTO.getEndPage(); i++) {
			System.out.printf(i == currentPage ? " [%d] " : " %d " , i);
		}
		
		
		if (pageDTO.isNext()) {
			System.out.println(" ▶ ");
		}
		
		System.out.println();
		System.out.println("-------------------------------------------------------------------------");


	}

	private void 삭제하기() {
		System.out.println("삭제할 게시글 번호(seq) 입력?");
		long seq = this.scanner.nextLong();
		int rowCount = this.boardService.deleteService(seq);
		if (rowCount == 1 ) {
			System.out.println("게시글 삭제 성공");
			목록보기();

		}//if
	}

	private void 수정하기() {
		//상세보기 -> [수정] 버튼 클릭 -> 게시글 정보 읽어와서 수정할 페이지로 이동
		System.out.println("수정할 게시글 번호(seq) 입력?");
		long seq = this.scanner.nextLong();

		BoardDTO dto = this.boardService.viewService(seq,false);

		if (dto == null) {
			System.out.println("? 수정할 게시글이 존재하지 않습니다. ");
			return;			
		}

		System.out.println("\tㄱ. 글번호 : " + seq );
		System.out.println("\tㄴ. 작성자 : " + dto.getWriter() );
		System.out.println("\tㄷ. 조회수 : " + dto.getReaded() );
		System.out.println("\tㄹ. 글제목 : " + dto.getTitle() );
		System.out.println("\tㅁ. 글내용 : " + dto.getContent() );
		System.out.println("\tㅂ. 작성일 : " + dto.getWritedate() );
		if("조지훈".equals(dto.getWriter())) {
			System.out.println("\t\n [수정] [삭제]");
		}
		System.out.println("\t\n [답글] [목록(home)]");
		scanner.nextLine(); 


		System.out.print("> email, title, tag, content 입력 ? ");

		String email = this.scanner.nextLine();
		String title = this.scanner.nextLine();
		int tag = Integer.parseInt(this.scanner.nextLine());
		String content = this.scanner.nextLine();

		//BoardDTO 객체생성

		BoardDTO uDto = BoardDTO.builder()
				.seq(seq)
				.email(email.equals("")? dto.getEmail(): email)
				.title(title.equals("")? dto.getTitle(): title)
				.tag(tag)
				.content(content.equals("")? dto.getContent(): content)
				.build();

		int rowCount = this.boardService.updateService(uDto);

		if (rowCount ==1) {

			System.out.println("새 글 쓰기 성공!!");

			상세보기(); // 목록페이지 요청이 일어난다.

		}
	}

	private void 상세보기() {
		System.out.println("보고자 하는 게시글 번호 (seq) 입력 ?");
		long seq = this.scanner.nextLong();

		BoardDTO dto = this.boardService.viewService(seq,true);
		if (dto != null) {
			System.out.println("\tㄱ. 글번호 : " + seq );
			System.out.println("\tㄴ. 작성자 : " + dto.getWriter() );
			System.out.println("\tㄷ. 조회수 : " + dto.getReaded() );
			System.out.println("\tㄹ. 글제목 : " + dto.getTitle() );
			System.out.println("\tㅁ. 글내용 : " + dto.getContent() );
			System.out.println("\tㅂ. 작성일 : " + dto.getWritedate() );
			if("조지훈".equals(dto.getWriter())) {
				System.out.println("\t\n [수정] [삭제]");
			}
			System.out.println("\t\n [답글] [목록(home)]");
		} else {
			System.out.println("😍😍😍게시글이 존재하지 않습니다.");
			목록보기();

		}

	}

	//현재 페이지 번호
	private int currentPage = 1;
	int blockSize = 10;
	int pageSize = 10;  

	private void 목록보기() {
		// BoardController.목록보기()
		// 목록리턴 BoardService.목록보기()            2)
		// 목록 리턴 BoardDao.목록보기()               1)
		//결과물 - > 뷰(View)                        3)
		System.out.println("현재 페이지 번호 입력");
		this.currentPage = this.scanner.nextInt();

		// 1.페이징 처리가 안됨
//		List<BoardDTO> list = this.boardService.selectService();
		//2. 페이징 처리 됨
		List<BoardDTO> list = this.boardService.selectService(currentPage,pageSize);
		//뷰 View 역할도 컨트롤러해서 담당하자 

		System.out.println("\t\t\t  게시판");
		System.out.println("-------------------------------------------------------------------------");
		System.out.printf("%s\t%-40s\t%s\t%-10s\t%s\n", 
				"글번호","글제목","글쓴이","작성일","조회수");
		System.out.println("-------------------------------------------------------------------------");
		if (list == null) {
			System.out.println("\t\t 게시글이 존재하지 않습니다.");
		}else {
			list.forEach(dto -> {
				System.out.printf("%d\t%-30s  %s\t%-10s\t%d\n",
						dto.getSeq(), 
						dto.getTitle(),
						dto.getWriter(),
						dto.getWritedate(),
						dto.getReaded());
			});			
		}//if
		//페이징 처리하는 부분이다
		System.out.println("-------------------------------------------------------------------------");
//		System.out.println("\t\t[1] 2 3 4 5 6 7 8 9 10 NEXT");
		PageDTO pageDTO = new PageDTO(currentPage, pageSize, blockSize);
		System.out.print("\t\t");
		if (pageDTO.isPrev()) {
			System.out.println(" ◀ ");
		}
		
		for (int i = pageDTO.getStartPage(); i <= pageDTO.getEndPage(); i++) {
			System.out.printf(i == currentPage ? " [%d] " : " %d " , i);
		}
		
		
		if (pageDTO.isNext()) {
			System.out.println(" ▶ ");
		}
		
		System.out.println();
			
		System.out.println("-------------------------------------------------------------------------");

	}

	private void 새글쓰기() {

		// 컨트롤러가 화면입력 역할 담당 + 입력값에 대한 유효성 검사
		System.out.print("> writer, pwd, email, title, tag, content 입력 ? ");
		String writer = this.scanner.nextLine();
		String pwd = this.scanner.nextLine();
		String email = this.scanner.nextLine();
		String title = this.scanner.nextLine();
		int tag = Integer.parseInt(this.scanner.nextLine());
		String content = this.scanner.nextLine();

		//BoardDTO 객체생성

		BoardDTO dto = BoardDTO.builder()
				.writer(writer)
				.pwd(pwd)
				.email(email)
				.title(title)
				.tag(tag)
				.content(content)
				.build();

		int rowCount = this.boardService.insertService(dto);
		if (rowCount ==1) {

			System.out.println("새 글 쓰기 성공!!");

			목록보기(); // 목록페이지 요청이 일어난다.

		}//if

	}

	private void 메뉴선택() {

		System.out.println(">메뉴 선택하세요?");
		this.selectedNumber = this.scanner.nextInt();
		this.scanner.nextLine(); //13 10 제거 

	}

	private void 메뉴출력() {

		String [] menu = {"새글","목록","보기","수정","삭제","검색","종료"};
		System.out.println("메뉴");
		for (int i = 0; i < menu.length; i++) {
			System.out.printf("%d, %s\t", i+1, menu[i]);



		}

		System.out.println();

	}
}
