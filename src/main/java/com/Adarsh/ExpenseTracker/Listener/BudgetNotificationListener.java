package com.Adarsh.ExpenseTracker.Listener;

import com.Adarsh.ExpenseTracker.Event.BudgetExceededEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BudgetNotificationListener {

    @EventListener
    public void handleBudgetExceeded(BudgetExceededEvent event){
        System.out.println("⚠️ ALERT: User '" + event.getUsername() + "' has exceeded their monthly budget! "
                + "Spent: " + event.getTotalSpent() + ", Limit: " + event.getBudgetLimit());
    }
}
