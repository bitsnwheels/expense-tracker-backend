package com.Adarsh.ExpenseTracker.Repository;

import com.Adarsh.ExpenseTracker.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
