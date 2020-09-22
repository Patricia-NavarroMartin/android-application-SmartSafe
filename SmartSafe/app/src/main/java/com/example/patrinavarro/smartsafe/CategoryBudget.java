package com.example.patrinavarro.smartsafe;

/**
 * Created by Patri Navarro on 27/05/2018.
 */

public class CategoryBudget {

    String categoryName;
    Double limit;
    Float current;
    float percentage;

    public CategoryBudget(String categoryName) {
        this.categoryName=categoryName;
    }
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Double getLimit() {
        return limit;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }

    public Float getCurrent() {
        return current;
    }

    public void setCurrent(Float current) {
        this.current = current;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }
}
