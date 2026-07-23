package com.Adarsh.ExpenseTracker.Service;


import com.Adarsh.ExpenseTracker.Exception.IncorrectCredentialsException;
import com.Adarsh.ExpenseTracker.Exception.UsernameAlreadyExistsException;
import com.Adarsh.ExpenseTracker.Model.LoginRequest;
import com.Adarsh.ExpenseTracker.Model.RegisterRequest;
import com.Adarsh.ExpenseTracker.Model.User;
import com.Adarsh.ExpenseTracker.Repository.UserRepository;
import com.Adarsh.ExpenseTracker.Security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class AuthService {

    UserRepository userRepo ;
    PasswordEncoder passwordEncoder;
    JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder,JwtUtil jwtUtil){

        this.userRepo=userRepo;
        this.passwordEncoder=passwordEncoder;
        this.jwtUtil=jwtUtil;

    }

    public String register(RegisterRequest request){


        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("The username already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepo.save(user);


        return "User Registration successful";
    }

    public String login(LoginRequest request){

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(()->new IncorrectCredentialsException("Incorrect username or password"));

        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new IncorrectCredentialsException("Incorrect username or password");
        }

        return jwtUtil.generateToken(request.getUsername());
    }
}
