package com.flowbot.application.module.domain.numeros.api.filter;

public class GetNumerosFilter {
    private String terms;
    private boolean sortByNewest;
    private String status;

    public GetNumerosFilter(String terms, boolean sortByNewest, String status) {
        this.terms = terms;
        this.sortByNewest = sortByNewest;
        this.status = status;
    }

    public String getTerms() {
        return terms;
    }

    public boolean isSortByNewest() {
        return sortByNewest;
    }

    public String getStatus() {
        return status;
    }
}
