package BonusCal;

import java.sql.*;

public class Main5 {
    public static void main(String[] args) throws SQLException {
        // DBMS 연결
        Connection conn = JDBCUtil.getConnection();

        // 시간 측정
        long startTime = System.currentTimeMillis();
        String sql = "INSERT INTO BONUS " + "SELECT e.ename, e.job, e.sal, " + "CASE "
                + "WHEN e.job IN ('PRESIDENT', 'ANALYST') THEN 0 "
                + "WHEN COUNT(c.mgr_empno) > 100000 THEN 2000 " + "ELSE 1000 " + "END AS comm "
                + "FROM emp e " + "LEFT JOIN customer c ON e.empno = c.mgr_empno "
                + "GROUP BY e.ename, e.job, e.sal " + "ORDER BY e.ename";

        Statement statement = conn.createStatement();
        statement.executeUpdate(sql);
        System.out.println("Data inserted successfully.");

        // 시간 측정
        long endTime = System.currentTimeMillis();
        double time = (endTime - startTime) / 1000.0;
        System.out.println("걸린 시간 : " + time + "초");
    }
}
