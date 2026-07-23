package com.Adarsh.ExpenseTracker.Service;

import com.Adarsh.ExpenseTracker.Model.Category;
import com.Adarsh.ExpenseTracker.Repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository catRepo;

    public CategoryService(CategoryRepository catRepo) {
        this.catRepo = catRepo;
    }

    public List<Category> getAllCategory(){
        return catRepo.findAll();
    }

    public Category createCategory(Category category){
        return catRepo.save(category);
    }
}
