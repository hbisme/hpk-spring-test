package com.hb.queue;

public class JobElement {

    /**
     * 版本号id
     */
    private String jobId;

    private String script;

    private int hostGroupId;

    private Integer priorityLevel;
    /**
     * 内存中的创建时间
     */
    private Long gmtCreated;
    /**
     * 内存中的修改时间
     */
    private Long gmtModified;

    private Long triggerTime;


    public boolean equals(JobElement jobElement) {
        if (!jobElement.getJobId().equals(jobId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "JobElement{" +
                "jobId='" + jobId + '\'' +
                ", priorityLevel=" + priorityLevel +
                '}';
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public int getHostGroupId() {
        return hostGroupId;
    }

    public void setHostGroupId(int hostGroupId) {
        this.hostGroupId = hostGroupId;
    }

    public Integer getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(Integer priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public Long getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Long gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Long getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Long gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Long getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Long triggerTime) {
        this.triggerTime = triggerTime;
    }
}
