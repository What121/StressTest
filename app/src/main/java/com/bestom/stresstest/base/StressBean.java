package com.bestom.stresstest.base;

public class StressBean {
    private int project;                //项目代码
    private String projectname="";      //项目名称

    private int maxtimes;               //设定测试次数
    private int curtimes;               //完成测试次数
    private String unit="";             //单位
    private String others="";           //其他参数

    private String time ="";            //测试上传时间

    public StressBean() {
    }

    public StressBean(int project, String projectname, int maxtimes, int curtimes, String unit, String others, String time) {
        this.project = project;
        this.projectname = projectname;
        this.maxtimes = maxtimes;
        this.curtimes = curtimes;
        this.unit = unit;
        this.others = others;
        this.time = time;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public int getMaxtimes() {
        return maxtimes;
    }

    public void setMaxtimes(int maxtimes) {
        this.maxtimes = maxtimes;
    }

    public int getCurtimes() {
        return curtimes;
    }

    public void setCurtimes(int curtimes) {
        this.curtimes = curtimes;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }
}
