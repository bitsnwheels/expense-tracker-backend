package com.Adarsh.ExpenseTracker.Repository;

import com.Adarsh.ExpenseTracker.Model.Expense;
import com.Adarsh.ExpenseTracker.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long> {



    List<Expense> findByUser(User user);
    List<Expense> findByUser_IdAndCategory_Name(Long userId, String categoryName);
    List<Expense> findByUser_IdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND e.category.name = :categoryName")
    BigDecimal sumAmountByCategoryAndUser(@Param("userId") Long userId, @Param("categoryName") String categoryName);

    Page<Expense> findByUser(User user, Pageable pageable);

}
