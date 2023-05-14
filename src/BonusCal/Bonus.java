package BonusCal;

public class Bonus {
    private String ename;
    private String job;
    private String sal;
    private String comm;

    // 생성자
    public Bonus(String ename, String job, String sal, String comm) {
        super();
        this.ename = ename;
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

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

}