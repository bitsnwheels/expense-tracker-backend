package com.Adarsh.ExpenseTracker.Controller;


import com.Adarsh.ExpenseTracker.Model.BudgetRequest;
import com.Adarsh.ExpenseTracker.Model.User;
import com.Adarsh.ExpenseTracker.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/budget")
    public User updateBudget(@RequestBody BudgetRequest request){
       return userService.updateBudgetService(request.getNewMonthlyBudget());
    }

}
