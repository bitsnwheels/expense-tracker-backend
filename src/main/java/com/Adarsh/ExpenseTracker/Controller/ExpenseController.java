package com.Adarsh.ExpenseTracker.Controller;

import com.Adarsh.ExpenseTracker.Model.Expense;
import com.Adarsh.ExpenseTracker.Model.ExpenseRequest;
import com.Adarsh.ExpenseTracker.Service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service){
        this.service=service;
    }

    @GetMapping("/")
    public String home(){
        return "Welcome to Expense Tracker made by Adarsh Raj";
    }


    @GetMapping("/expenses")
    public List<Expense> getAllExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {

        if (category != null) {
            return service.getExpensesByCategoryService(category);
        } else if (month != null && year != null) {
            return service.getExpensesByMonthYearService(month, year);
        } else {
            return service.getAllExpensesService();
        }
    }

    @GetMapping("/expenses/{id}")
    public Expense getExpenseById(@PathVariable Long id){
        return service.getExpenseByIdService(id);
    }

    @GetMapping("/expenses/total")
    public BigDecimal getTotalByCategory(@RequestParam String category){
        return service.getTotalByCategoryService(category);
    }

    @GetMapping("/expenses/paginated")
    public Page<Expense> getAllExpensesPaginated(Pageable pageable){
        return service.getAllExpensesPaginatedService(pageable);
    }


    @PostMapping("/expenses")
    public Expense createExpense(@Valid @RequestBody ExpenseRequest e){
        return service.createExpenseService(e);
    }

    @PutMapping("/expenses/{id}")
    public Expense updateExpense( @PathVariable Long id, @Valid @RequestBody ExpenseRequest e) {
        return service.updateExpenseService(id, e);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id){
        service.deleteExpenseService(id);
        return ResponseEntity.ok("Expense with id " + id + " deleted successfully");
    }


}
