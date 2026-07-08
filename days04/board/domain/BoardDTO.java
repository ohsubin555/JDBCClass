package days04.board.domain;

import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {
	
	
	 private int seq;
	 private String writer;
	 private String pwd ;
	 private String email ;
	 private String title ;
	 private Date writedate ;
	 private int readed ;
	 private int tag ;
	 private String content ;

}
