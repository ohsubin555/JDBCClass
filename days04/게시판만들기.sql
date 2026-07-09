SELECT s.grade, s.losal, s.hisal 
       ,COUNT(e.empno)empCount
FROM salgrade s JOIN emp e ON e.sal BETWEEN s.losal AND s.hisal
GROUP BY s.grade, s.losal, s.hisal
ORDER BY s.grade ASC;

SELECT d.deptno, d.dname, e.empno, e.ename, e.sal, s.grade
FROM emp e LEFT OUTER JOIN dept d ON e.deptno = d.deptno
           JOIN salgrade s ON e.sal BETWEEN s.losal AND s.hisal
WHERE s.grade = ?
ORDER BY d.deptno, e.empno

 SELECT empno, ename, hiredate, sal + NVL(comm, 0) pay , dname , e.deptno  ,sal , grade , losal, hisal
	                FROM emp e LEFT JOIN dept d ON e.deptno = d.deptno
	                JOIN salgrade s ON sal BETWEEN losal AND hisal
	            order by grade
	               """;
 
 CREATE SEQUENCE SEQ_tblcstVSBoard
 NOCACHE;
 --
 
                   
 CREATE TABLE tbl_cstVSBoard (
  seq NUMBER NOT NULL PRIMARY KEY , --글 번호
  writer VARCHAR2 (20) NOT NULL , --작성자 
  pwd VARCHAR2 (20) NOT NULL ,-- 비밀번호
  email VARCHAR2 (100),
  title VARCHAR2 (200) NOT NULL ,
  writedate DATE NOT NULL DEFAULT SYSDATE, 
  readed NUMBER NOT NULL DEFAULT 0,
  tag NUMBER(1) NOT NULL ,-- 글 형식 일반 텍스트 0 , HTML 1
  content CLOB 
);

CREATE TABLE tbl_cstVSBoard (
  seq NUMBER  NOT NULL PRIMARY KEY,
  writer VARCHAR2(20) NOT NULL ,
  pwd VARCHAR2(20)  NOT NULL ,
  email VARCHAR2(100)  ,
  title VARCHAR2(200)  NOT NULL ,
  writedate DATE  DEFAULT  SYSDATE ,
  readed NUMBER DEFAULT  0 ,
  tag NUMBER(1) NOT NULL ,
  content CLOB
);


--페이징 처리 X + 목록보기

SELECT seq, title, writer, email, writedate, readed
FROM tbl_cstvsboard
ORDER BY seq DESC;

-- 게시글 150개 정도 더미데이터 인서트


BEGIN
 For i IN 1..150 LOOP
 INSERT INTO tbl_cstvsboard ( seq, writer, pwd, email, title, tag, content)
				VALUES (seq_tblcstvsboard.NEXTVAL, '홍길동' || MOD (i , 10), '1234', '홍길동' || MOD(i,10)|| '@naver.com' , '더미...' || MOD(i,10));
 END LOOP;
 COMMIT;
 
END; 


BEGIN
  FOR i IN 1..150 LOOP
      INSERT INTO tbl_cstvsboard ( seq, writer, pwd, email, title, tag, content )
      VALUES ( seq_tblcstvsboard.NEXTVAL, '홍길동' || MOD(i, 10), '1234'
      , '홍길동' || MOD(i, 10) || '@naver.com'
      , '더미...' || MOD(i, 10), 0, '더미...' || MOD(i, 10) );
  END LOOP;
  COMMIT;
END;

BEGIN
 UPDATE tbl_cstvsboard
 SET writer = '조지훈'
 WHERE MOD(seq, 16) IN (3,5,8);
END;

--

BEGIN
 UPDATE tbl_cstvsboard
 SET title = '게시글 구현'
 WHERE MOD(seq, 16) IN (1,7,10);
END;

COMMIT;

select * from  tbl_cstvsboard;



--0709

SELECT seq, title, writer, email, writedate, readed
FROM tbl_cstvsboard
WHERE 1 = 1 AND REGEXP_LIKE( writer, '조지훈' , 'i') ORDER BY seq DESC;

SELECT CEIL(COUNT(*) / 10)
FROM tbl_cstvsboard;



SELECT COUNT(*)
FROM tbl_cstvsboard;



FROM(
SELECT 
ROW_NUMBER() OVER(ORDER BY seq DESC) no
,seq, title, writer, email, writedate, readed
				FROM tbl_cstvsboard)
                WHERE no BETWEEN 1 AND 10;
                
                

FROM(
SELECT 
ROW_NUMBER() OVER(ORDER BY seq DESC) no
,seq, title, writer, email, writedate, readed
				FROM tbl_cstvsboard)
                WHERE (writer,'지훈', 'i')
                
                
                
			