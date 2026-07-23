package com.Adarsh.ExpenseTracker.Model;

import java.math.BigDecimal;

public class BudgetRequest {


    BigDecimal newMonthlyBudget ;

    public BigDecimal getNewMonthlyBudget() {
        return newMonthlyBudget;
    }

    public void setNewMonthlyBudget(BigDecimal newMonthlyBudget) {
        this.newMonthlyBudget = newMonthlyBudget;
    }
}
