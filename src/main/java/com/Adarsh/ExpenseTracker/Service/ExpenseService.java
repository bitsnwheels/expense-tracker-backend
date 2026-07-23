package com.Adarsh.ExpenseTracker.Service;

import com.Adarsh.ExpenseTracker.Event.BudgetExceededEvent;
import com.Adarsh.ExpenseTracker.Exception.ExpenseNotFoundException;
import com.Adarsh.ExpenseTracker.Model.Category;
import com.Adarsh.ExpenseTracker.Model.Expense;
import com.Adarsh.ExpenseTracker.Model.ExpenseRequest;
import com.Adarsh.ExpenseTracker.Model.User;
import com.Adarsh.ExpenseTracker.Repository.CategoryRepository;
import com.Adarsh.ExpenseTracker.Repository.ExpenseRepository;
import com.Adarsh.ExpenseTracker.Repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repo;
    private final CategoryRepository catRepo;
    private final UserRepository userRepo;
    private final ApplicationEventPublisher eventPublisher;
    public ExpenseService(ExpenseRepository repo, CategoryRepository catRepo,UserRepository userRepo,ApplicationEventPublisher eventPublisher){
        this.repo = repo;
        this.catRepo = catRepo;
        this.userRepo=userRepo;
        this.eventPublisher=eventPublisher;
    }



    public List<Expense> getAllExpensesService() {
        return repo.findByUser(getCurrentUser());
    }


    public Page<Expense> getAllExpensesPaginatedService(Pageable pageable){
        return repo.findByUser(getCurrentUser(), pageable);
    }

    public List<Expense>getExpensesByCategoryService(String category){
        return repo.findByUser_IdAndCategory_Name(getCurrentUser().getId(), category);
    }

    public List<Expense>getExpensesByMonthYearService(int month, int year){
        // 1. compute the start date: first day of the given month/year
        // 2. compute the end date: first day of the NEXT month (exclusive upper bound is safer than last-day-of-month)
        // 3. call repo.findByDateBetween(start, end)

        LocalDate startDate=LocalDate.of(year,month,1);
        LocalDate endDate = startDate.plusMonths(1);

        return repo.findByUser_IdAndDateBetween(getCurrentUser().getId(),startDate,endDate.minusDays(1));
    }

    public Expense getExpenseByIdService(Long id) {
        Expense exp = repo.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id " + id + " !"));

        if (!exp.getUser().getId().equals(getCurrentUser().getId())) {
            throw new ExpenseNotFoundException("Expense not found with id " + id + " !");
        }

        return exp;
    }

    @CacheEvict(value = "categoryTotals", allEntries = true)
    public Expense createExpenseService(ExpenseRequest e) {
       Expense exp = new Expense();
       Category c1=catRepo.findById(e.getCategoryId()).orElseThrow(() -> new ExpenseNotFoundException("Category not found with id: "+ e.getCategoryId()));
       exp.setCategory(c1);
       exp.setAmount(e.getAmount());
       exp.setDate(e.getDate());
       exp.setNote(e.getNote());
       exp.setUser(getCurrentUser());

       Expense saved=  repo.save(exp);

       User user = getCurrentUser();
       if(user.getMonthlyBudgetLimit() != null && user.getMonthlyBudgetLimit().compareTo(BigDecimal.ZERO) > 0){
           LocalDate now  = LocalDate.now();
           LocalDate startOfMonth = now.withDayOfMonth(1);
           LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

           List<Expense> monthExpenses = repo.findByUser_IdAndDateBetween(user.getId(),startOfMonth,endOfMonth);
           BigDecimal totalThisMonth = monthExpenses.stream()
                   .map(Expense::getAmount)
                   .reduce(BigDecimal.ZERO,BigDecimal::add);

           if(totalThisMonth.compareTo(user.getMonthlyBudgetLimit()) > 0){
               eventPublisher.publishEvent(new BudgetExceededEvent(user.getUsername(),totalThisMonth,user.getMonthlyBudgetLimit()));
           }
       }

       return  saved;

    }


    @CacheEvict(value = "categoryTotals", allEntries = true)
    public Expense updateExpenseService(Long id, ExpenseRequest e) {

        Expense exp = repo.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id " + id + " !"));

        if (!exp.getUser().getId().equals(getCurrentUser().getId())) {
            throw new ExpenseNotFoundException("Expense not found with id " + id + " !");
        }


        Category c1=catRepo.findById(e.getCategoryId()).orElseThrow(() -> new ExpenseNotFoundException("Category not found with id: "+ e.getCategoryId()));

        exp.setCategory(c1);
        exp.setAmount(e.getAmount());
        exp.setDate(e.getDate());
        exp.setNote(e.getNote());

        return repo.save(exp);

    }

    @CacheEvict(value = "categoryTotals", allEntries = true)
    public void deleteExpenseService(Long id) {

        Expense exp = repo.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id " + id + " !"));

        if (!exp.getUser().getId().equals(getCurrentUser().getId())) {
            throw new ExpenseNotFoundException("Expense not found with id " + id + " !");
        }

        repo.deleteById(id);
    }

    @Cacheable(value = "categoryTotals", key = "#category + '-' + #root.target.getCurrentUser().getId()")
    public BigDecimal getTotalByCategoryService(String category) {
        System.out.println("Using the expenseService not the redis");
        return repo.sumAmountByCategoryAndUser(getCurrentUser().getId(), category);
    }

    public User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepo.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Authenticated user not found in the database"));
    }

}
