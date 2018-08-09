package tech.mangosoft.autolinkedin.controller.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatisticResponse {

    @JsonProperty
    private String assignmentName;

    @JsonProperty
    private String status;

    @JsonProperty
    private String errorMessage;

    @JsonProperty
    private long processed;

    @JsonProperty
    private long saved;

    @JsonProperty
    private long successed;

    @JsonProperty
    private long failed;

    @JsonProperty
    private int page;

    public StatisticResponse() {
    }


    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getProcessed() {
        return processed;
    }

    public void setProcessed(long processed) {
        this.processed = processed;
    }

    public long getSaved() {
        return saved;
    }

    public void setSaved(long saved) {
        this.saved = saved;
    }

    public long getSuccessed() {
        return successed;
    }

    public void setSuccessed(long successed) {
        this.successed = successed;
    }

    public long getFailed() {
        return failed;
    }

    public void setFailed(long failed) {
        this.failed = failed;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
