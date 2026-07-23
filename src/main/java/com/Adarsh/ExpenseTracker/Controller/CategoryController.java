package com.Adarsh.ExpenseTracker.Controller;

import com.Adarsh.ExpenseTracker.Model.Category;
import com.Adarsh.ExpenseTracker.Service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService catService;

    public CategoryController(CategoryService catService) {
        this.catService = catService;
    }

    @GetMapping("/categories")
    public List<Category> getCategories(){
        return catService.getAllCategory();
    }

    @PostMapping("/categories")
    public Category addCategory(@RequestBody Category category){
        return catService.createCategory(category);
    }

}
