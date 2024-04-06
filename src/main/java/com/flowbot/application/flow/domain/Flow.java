package com.flowbot.application.flow.domain;

public class Flow {
    private String flowJson;

    public String getFlowJson() {
        return flowJson;
    }

    public void setFlowJson(String flowJson) {
        this.flowJson = flowJson;
    }

    public Flow(String flowJson) {
        this.flowJson = flowJson;
    }

    public Flow() {}
}
