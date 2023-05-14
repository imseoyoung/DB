package BonusCal;

public class Emp {
    private String ename;
    private int empno;
    private String job;
    private String sal;
    private String comm;

    // 생성자
    public Emp(String ename, int empno, String job, String sal, String comm) {
        super();
        this.ename = ename;
        this.empno = empno;
        this.job = job;
        this.sal = sal;
        this.comm = comm;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public int getEmpno() {
        return empno;
    }

    public void setEmpno(int empno) {
        this.empno = empno;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getSal() {
        return sal;
    }

    public void setSal(String sal) {
        this.sal = sal;
    }

    public String getComm() {
        return comm;
    }

    public void setComm(String comm) {
        this.comm = comm;
    }
}
    
    