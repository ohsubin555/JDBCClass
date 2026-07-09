package days04;

import java.util.Arrays;

public class EX03 {

	public static void main(String[] args) {
		
		String searchCondition = "t";
		String searchKeyword = "홍길동";
//		String [] m = searchCondition.split("");
//		System.out.println(Arrays.toString(m));
		
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
				sql += "REGXP_LIKE( title, ? , 'i') ";
			}else if (scArr [i].equals("c")) {
				sql += "REGXP_LIKE( contetn, ? ,'i') ";
			}else if (scArr [i].equals("w")) {
				sql += "REGXP_LIKE( writer, ? , 'i') ";
			}
			
			 if(i != len ) sql += " OR ";
	      } // for
		
		System.out.println(sql);

	}

}
