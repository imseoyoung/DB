package BonusCal;

import java.sql.*;

public class Main4 {
    public static void main(String[] args) throws SQLException {
        // DBMS 연결
        Connection conn = JDBCUtil.getConnection();

        // PL/SQL 코드
        String plsqlBlock = "DECLARE " +
                "CURSOR emp_cur IS " +
                "SELECT ENAME, EMPNO, JOB, SAL, NVL(COMM, 0) AS COMM FROM EMP; " +
                "CURSOR customer_cur IS " +
                "SELECT MGR_EMPNO FROM CUSTOMER; " +
                "count_val NUMBER; " +
                "high_cnt_mgrs SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST(); " +
                "low_cnt_mgrs SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST(); " +
                "bonus_value NUMBER := 0; " +
                "comm_value NUMBER := 0; " +
                "empno NUMBER := 0; " +
                "ename EMP.ENAME%TYPE; " +
                "job EMP.JOB%TYPE; " +
                "sal EMP.SAL%TYPE := 0; " +
                "comm EMP.COMM%TYPE; " +
                "start_time TIMESTAMP; " +
                "end_time TIMESTAMP; " +
                "time_taken NUMBER; " +
                "customer_count NUMBER; " +
                "TYPE emp_cur_type IS TABLE OF emp_cur%ROWTYPE; " +
                "emp_records emp_cur_type; " +
                "BEGIN " +
                "BEGIN " +
                "start_time := systimestamp; " +
                "OPEN emp_cur; " +
                "FETCH emp_cur BULK COLLECT INTO emp_records LIMIT 10000; " +
                "CLOSE emp_cur; " +
                "FOR i IN 1..emp_records.COUNT LOOP " +
                "empno := emp_records(i).EMPNO; " +
                "comm_value := emp_records(i).COMM; " +
                "count_val := 0; " +
                "FOR customer_rec IN customer_cur LOOP " +
                "IF customer_rec.MGR_EMPNO = empno THEN " +
                "count_val := count_val + 1; " +
                "END IF; " +
                "END LOOP; " +
                "IF count_val >= 100000 THEN " +
                "high_cnt_mgrs.EXTEND(); " +
                "high_cnt_mgrs(high_cnt_mgrs.LAST) := TO_CHAR(empno); " +
                "bonus_value := 2000; " +
                "ELSE " +
                "low_cnt_mgrs.EXTEND(); " +
                "low_cnt_mgrs(low_cnt_mgrs.LAST) := TO_CHAR(empno); " +
                "bonus_value := 1000; " +
                "END IF; " +
                "IF emp_records(i).JOB != 'PRESIDENT' AND emp_records(i).JOB != 'ANALYST' THEN " +
                "comm := TO_CHAR(comm_value + bonus_value); " +
                "ELSE " +
                "comm := TO_CHAR(comm_value); " +
                "END IF; " +
                "INSERT INTO BONUS (ename, job, sal, comm) " +
                "VALUES (emp_records(i).ENAME, emp_records(i).JOB, emp_records(i).SAL, comm); " +
                "END LOOP; " +
                "end_time := systimestamp; " +
                "time_taken := extract(second from end_time - start_time); " +
                "SELECT COUNT(*) INTO customer_count FROM CUSTOMER; " +
                "dbms_output.put_line('걸린 시간: ' || time_taken || '초'); " +
                "dbms_output.put_line('고객 개수: ' || customer_count); " +
                "COMMIT; " +
                "EXCEPTION " +
                "WHEN OTHERS THEN " +
                "dbms_output.put_line('오류 발생: ' || SQLERRM); " +
                "ROLLBACK; " +
                "END;";

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

    }
}
