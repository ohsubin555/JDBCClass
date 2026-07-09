package days04;

public class EX04 {

	public static void main(String[] args) {
		//PageDTO : 페이징 처리를 해주는 클래스 
		//Pagination [1] 2 3 4 5 6... 다음(NEXT, > )
		// currentPage [1]  현재 페이지
		//Page Number : 1/2/3/4...10
		// Page Navigation 다음(NEXT, > )
		//Page List :  [1] 2 3 4 5 6
		
		//blockSize : [1] 2 3 4 5 6...  개수
		// startPage : 시작하는 Page Number [1]		
		// endtPage : 끝나는 Page Number 10
		
				
		//총 게시글 수 : 149
		// 한 페이지에 출력할 게시글 수 : 10
		// 총 페이지 수 : 15 P
		// pageSize == 한페이지에 출력할 게시글 수 
		// totalPages ==총 페이지수
		
		int pageSize = 10;
		int totalPages = 15; // 149/pageSize
		int blockSize = 10;
		int startPage = 1 , endPage = 10;
		int currentPage ;
		
		for (currentPage = 1; currentPage <= totalPages ; currentPage++) {
			startPage = ((currentPage - 1) / blockSize) * blockSize + 1;
//			endPage = startPage + blockSize - 1;
//			if (endPage > totalPages) {
//			    endPage = totalPages;
//			}
		
			endPage = Math.min(startPage + blockSize - 1, totalPages);
			System.out.printf("현재페이지(%d)\t" , currentPage);
			
			for (int i = startPage; i <= endPage; i++) {
				System.out.printf(i == currentPage ? " [%d] " : " %d " , i);
			}
			System.out.println();
			
			
		}
	} 

}
