package com.Adarsh.ExpenseTracker.Model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequest {

    @Positive
    private BigDecimal amount;

    @NotNull
    private Long categoryId;

    @NotNull
    private LocalDate date;

    private String note;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "ExpenseRequest{" +
                "amount=" + amount +
                ", categoryId=" + categoryId +
                ", date=" + date +
                ", note='" + note + '\'' +
                '}';
    }
}
