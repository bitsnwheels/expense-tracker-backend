package com.Adarsh.ExpenseTracker.Controller;

import com.Adarsh.ExpenseTracker.Exception.ExpenseNotFoundException;
import com.Adarsh.ExpenseTracker.Model.LoginRequest;
import com.Adarsh.ExpenseTracker.Model.RegisterRequest;
import com.Adarsh.ExpenseTracker.Service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    AuthService authService ;

    public AuthController(AuthService authService){
        this.authService=authService;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody RegisterRequest request){
       return authService.register(request);
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody LoginRequest request){
        return authService.login(request);
    }
}
