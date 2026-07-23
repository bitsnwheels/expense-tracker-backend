package com.Adarsh.ExpenseTracker.Event;

import java.math.BigDecimal;

public class BudgetExceededEvent {

    private final String username;
    private final BigDecimal totalSpent;
    private final BigDecimal budgetLimit;

    public BudgetExceededEvent(String username, BigDecimal totalSpent, BigDecimal budgetLimit) {
        this.username = username;
        this.totalSpent = totalSpent;
        this.budgetLimit = budgetLimit;
    }

    public String getUsername() {
        return username;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public BigDecimal getBudgetLimit() {
        return budgetLimit;
    }
}

