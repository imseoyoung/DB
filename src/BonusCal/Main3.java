package BonusCal;

import java.sql.*;

public class Main3 {
    public static void main(String[] args) throws SQLException {
        // DBMS 연결
        Connection conn = JDBCUtil.getConnection();

        // PL/SQL 코드 실행
        String plsqlBlock = "DECLARE\n" + "  -- EMP 데이터를 검색하기 위한 커서\n"
                + "  CURSOR emp_cur IS\n"
                + "    SELECT ENAME, EMPNO, JOB, SAL, NVL(COMM, 0) AS COMM FROM EMP;\n"
                + "  -- CUSTOMER 데이터를 검색하기 위한 커서\n" + "  CURSOR customer_cur IS\n"
                + "    SELECT MGR_EMPNO FROM CUSTOMER;\n"
                + "  -- MGR_EMPNO 개수를 저장하기 위한 변수\n" + "  count_val NUMBER;\n"
                + "  -- 높은 개수와 낮은 개수의 MGR_EMPNO를 저장하기 위한 리스트\n"
                + "  high_cnt_mgrs SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST();\n"
                + "  low_cnt_mgrs SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST();\n"
                + "  -- 보너스 계산을 위한 변수\n" + "  bonus_value NUMBER := 0;\n"
                + "  comm_value NUMBER := 0;\n" + "  empno NUMBER := 0;\n"
                + "  -- 보너스 삽입을 위한 변수\n" + "  ename EMP.ENAME%TYPE;\n"
                + "  job EMP.JOB%TYPE;\n" + "  sal EMP.SAL%TYPE := 0;\n" + "  comm EMP.COMM%TYPE;\n"
                + "  -- 시간 측정을 위한 변수\n" + "  start_time TIMESTAMP;\n"
                + "  end_time TIMESTAMP;\n" + "  time_taken NUMBER;\n"
                + "  -- 고객 개수를 저장하기 위한 변수\n" + "  customer_count NUMBER;\n"
                + "BEGIN\n" + "  -- 시간 측정 시작\n" + "  start_time := systimestamp;\n"
                + "  -- EMP 데이터 검색\n" + "  FOR emp_rec IN emp_cur LOOP\n"
                + "    empno := emp_rec.EMPNO;\n" + "    -- COMM 값을 검색하고 NULL 처리\n"
                + "    comm_value := emp_rec.COMM;\n" + "    -- count_val 초기화\n"
                + "    count_val := 0;\n"
                + "    -- empno가 높은 개수나 낮은 개수의 MGR_EMPNO에 있는지 확인\n"
                + "    FOR customer_rec IN customer_cur LOOP\n"
                + "      IF customer_rec.MGR_EMPNO = empno THEN\n"
                + "        count_val := count_val + 1;\n" + "      END IF;\n" + "    END LOOP;\n"
                + "    IF count_val >= 100000 THEN\n" + "      high_cnt_mgrs.EXTEND();\n"
                + "      high_cnt_mgrs(high_cnt_mgrs.LAST) := TO_CHAR(empno);\n"
                + "      bonus_value := 2000;\n" + "    ELSE\n" + "      low_cnt_mgrs.EXTEND();\n"
                + "low_cnt_mgrs(low_cnt_mgrs.LAST) := TO_CHAR(empno);\n" + "bonus_value := 1000;\n"
                + " END IF;\n" + " -- 보너스 테이블에 삽입\n"
                + "IF emp_rec.JOB != 'PRESIDENT' AND emp_rec.JOB != 'ANALYST' THEN\n"
                + "comm := TO_CHAR(comm_value + bonus_value);\n" + "ELSE\n"
                + "comm := TO_CHAR(comm_value);\n" + "END IF;\n"
                + "INSERT INTO BONUS (ename, job, sal, comm)\n"
                + "VALUES (emp_rec.ENAME, emp_rec.JOB, emp_rec.SAL, comm);\n" + "END LOOP;\n"
                + "-- 시간 측정 종료\n" + "end_time := systimestamp;\n"
                + "time_taken := extract(second from end_time - start_time);\n"
                + "-- 고객 수 확인하기\n"
                + "SELECT COUNT(*) INTO customer_count FROM CUSTOMER;\n"
                + "-- 걸린 시간과 검색한 고객 수 출력\n"
                + "dbms_output.put_line('걸린 시간: ' || time_taken || '초');\n"
                + "dbms_output.put_line('Customer Count: ' || customer_count);\n" + "END";
        
        // DBMS_OUTPUT.ENABLE 프로시저를 호출하여 서버 출력을 활성화
        CallableStatement callableStatement = conn.prepareCall("{CALL DBMS_OUTPUT.ENABLE}");
        callableStatement.execute();

        // PL/SQL 블록을 실행하기 위해 plsqlBlock 문자열을 사용하여 CallableStatement를 준비
        callableStatement = conn.prepareCall("{CALL " + plsqlBlock + "}");
        // CallableStatement를 실행하여 PL/SQL 블록을 실행
        callableStatement.execute();

        // DBMS_OUTPUT.GET_LINES 프로시저를 호출하여 서버 출력 결과 가져오기
        callableStatement = conn.prepareCall("{CALL DBMS_OUTPUT.GET_LINES(?, ?)}");
        // DBMSOUTPUT_LINESARRAY 형식의 배열을 저장할 파라미터를 등록
        callableStatement.registerOutParameter(1, Types.ARRAY, "DBMSOUTPUT_LINESARRAY");
        callableStatement.registerOutParameter(2, Types.INTEGER);
        callableStatement.execute();
        // 반복문을 통해 출력 라인을 순회하고 출력
        Array outputArray = callableStatement.getArray(1);
        String[] outputLines = (String[]) outputArray.getArray();
        for (String line : outputLines) {
            System.out.println(line);
        }
        callableStatement.close();
        conn.close();
    }
}
