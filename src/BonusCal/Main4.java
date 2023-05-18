package BonusCal;

import java.sql.*;

public class Main4 {
    public static void main(String[] args) throws SQLException {
        // DBMS 연결
        Connection conn = JDBCUtil.getConnection();

        String plsqlBlock = "DECLARE\n" + "CURSOR emp_cur IS\n"
                + "SELECT ENAME, EMPNO, JOB, SAL, NVL(COMM, 0) AS COMM FROM EMP;\n"
                + "CURSOR customer_cur IS\n" + "SELECT MGR_EMPNO FROM CUSTOMER;\n"
                + "count_val NUMBER;\n"
                + "high_cnt_mgrs SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST();\n"
                + "low_cnt_mgrs SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST();\n"
                + "bonus_value NUMBER := 0;\n" + "comm_value NUMBER := 0;\n"
                + "empno NUMBER := 0;\n" + "ename EMP.ENAME%TYPE;\n" + "job EMP.JOB%TYPE;\n"
                + "sal EMP.SAL%TYPE := 0;\n" + "comm EMP.COMM%TYPE;\n" + "start_time TIMESTAMP;\n"
                + "end_time TIMESTAMP;\n" + "time_taken NUMBER;\n" + "customer_count NUMBER;\n"
                + "TYPE emp_cur_type IS TABLE OF emp_cur%ROWTYPE;\n" + "emp_records emp_cur_type;\n"
                + "BEGIN\n" + "BEGIN\n" + "start_time := systimestamp;\n" + "OPEN emp_cur;\n"
                + "-- fetch size\n" +
                // fetch size
                "FETCH emp_cur BULK COLLECT INTO emp_records LIMIT 1000;\n" + "CLOSE emp_cur;\n"
                + "FOR i IN 1..emp_records.COUNT LOOP\n" + "empno := emp_records(i).EMPNO;\n"
                + "comm_value := emp_records(i).COMM;\n" + "count_val := 0;\n"
                + "FOR customer_rec IN customer_cur LOOP\n"
                + "IF customer_rec.MGR_EMPNO = empno THEN\n" + "count_val := count_val + 1;\n"
                + "END IF;\n" + "END LOOP;\n" + "IF count_val >= 100000 THEN\n"
                + "high_cnt_mgrs.EXTEND();\n"
                + "high_cnt_mgrs(high_cnt_mgrs.LAST) := TO_CHAR(empno);\n"
                + "bonus_value := 2000;\n" + "ELSE\n" + "low_cnt_mgrs.EXTEND();\n"
                + "low_cnt_mgrs(low_cnt_mgrs.LAST) := TO_CHAR(empno);\n" + "bonus_value := 1000;\n"
                + "END IF;\n"
                + "IF emp_records(i).JOB != 'PRESIDENT' AND emp_records(i).JOB != 'ANALYST' THEN\n"
                + "comm := TO_CHAR(comm_value + bonus_value);\n" + "ELSE\n"
                + "comm := TO_CHAR(comm_value);\n" + "END IF;\n"
                + "INSERT INTO BONUS (ename, job, sal, comm)\n"
                + "VALUES (emp_records(i).ENAME, emp_records(i).JOB, emp_records(i).SAL, comm);\n"
                + "END LOOP;\n" + "end_time := systimestamp;\n"
                + "time_taken := extract(second from end_time - start_time);\n"
                + "SELECT COUNT(*) INTO customer_count FROM CUSTOMER;\n"
                + "dbms_output.put_line('걸린 시간: ' || time_taken || '초');\n"
                + "dbms_output.put_line('고객 개수: ' || customer_count);\n" + "COMMIT;\n"
                + "EXCEPTION\n" + "WHEN OTHERS THEN\n"
                + "dbms_output.put_line('오류 발생: ' || SQLERRM);\n" + "ROLLBACK;\n" + "END;\n"
                + "END";

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
