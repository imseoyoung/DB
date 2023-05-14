package BonusCal;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    // JOB - PRESIDENT ANALYST 제외
    public static boolean isAvailableBonus(String job) {
        return !job.equals("PRESIDENT") && !job.equals("ANALYST");
    }
    
    public static void main(String[] args) throws SQLException {
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT ENAME, EMPNO, JOB, SAL, COMM FROM EMP");
        PreparedStatement pstmt2 = conn.prepareStatement("SELECT MGR_EMPNO FROM CUSTOMER");

        List<Emp> empInfo = new ArrayList<Emp>();
        List<Bonus> bonusInfo = new ArrayList<Bonus>();

        // 시간 측정
        long startTime = System.currentTimeMillis();

        // EMP - ENAME, EMPNO, KOB, SAL, COMM
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            empInfo.add(new Emp(rs.getString("ENAME"), Integer.parseInt(rs.getString("EMPNO")),
                    rs.getString("JOB"), rs.getString("SAL"), rs.getString("COMM")));
        }

        // CUSTOMER - MGR_EMPNO       
        ResultSet rs2 = pstmt2.executeQuery();
        Map<String, Integer> customers = new HashMap<>();

        // MGR_EMPNO의 개수를 담을 변수 초기화
        int count;
        // count > 10000
        List<String> highCntMgrs = new ArrayList<>();
        // count < 10000
        List<String> lowCntMgrs = new ArrayList<>();

        while (rs2.next()) {
            String mgrEmpno = rs2.getString("MGR_EMPNO");

            if (customers.containsKey(mgrEmpno)) {
                count = customers.get(mgrEmpno) + 1;
            } else {
                count = 1;
            }
            // Map에 값 저장
            customers.put(mgrEmpno, count);
            // 각 List에 값 저장
            if (count > 100000) {
                highCntMgrs.add(mgrEmpno);
            } else {
                lowCntMgrs.add(mgrEmpno);
            }
        }
        
        // BONUS 계산하기
        for (Emp emp : empInfo) {
            int commValue = emp.getComm() == null ? 0 : Integer.parseInt(emp.getComm());
            int bonusValue = 0;
            int empno = emp.getEmpno();
            
            // JOB 제외
            if (isAvailableBonus(emp.getJob())) {
                if (highCntMgrs.contains(Integer.toString(empno))) {
                    bonusValue = 2000;
                } else if (lowCntMgrs.contains(Integer.toString(empno))) {
                    bonusValue = 1000;
                }                
            }
            
            // bonusInfo에 계산한 값 넣기
            bonusInfo.add(new Bonus(emp.getEname(), emp.getJob(), emp.getSal(),
                    String.valueOf(commValue + bonusValue)));
        }

        // BONUS 테이블에 bonusInfo 값 INSERT
        for (Bonus bonus : bonusInfo) {
            String ename = bonus.getEname();
            String job = bonus.getJob();
            String sal = bonus.getSal();
            String comm = bonus.getComm();

            String sql = "INSERT INTO bonus (ename, job, sal, comm) VALUES (?, ?, ?, ?)";

            PreparedStatement pstmt3 = conn.prepareStatement(sql);
            pstmt3.setString(1, ename);
            pstmt3.setString(2, job);
            pstmt3.setString(3, sal);
            pstmt3.setString(4, comm);

            pstmt3.executeUpdate();
        }
        
        // 시간 측정
        long endTime = System.currentTimeMillis();
        double time = (endTime - startTime) / 1000.0;
        System.out.println("걸린 시간 : " + time + "초");
    }
}