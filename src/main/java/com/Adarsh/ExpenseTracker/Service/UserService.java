package com.Adarsh.ExpenseTracker.Service;

import com.Adarsh.ExpenseTracker.Model.User;
import com.Adarsh.ExpenseTracker.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    public User updateBudgetService(BigDecimal newLimit){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Authenticated user not found!"));

        user.setMonthlyBudgetLimit(newLimit);

        return userRepository.save(user);
    }
}
