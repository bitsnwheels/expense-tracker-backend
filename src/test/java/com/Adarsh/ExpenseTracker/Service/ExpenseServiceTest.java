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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ExpenseService expenseService;

    private User mockUser;

        @BeforeEach
        void setUp() {
            mockUser = new User();
            mockUser.setId(1L);
            mockUser.setUsername("testuser");

            Authentication auth = mock(Authentication.class);
            lenient().when(auth.getName()).thenReturn("testuser");

            SecurityContext securityContext = mock(SecurityContext.class);
            lenient().when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            // the above lines are written so that when we invoke SecurityContextHolder.getContext().getAuthentication().getName(),
            // it walks through this exact chain of fakes and gets "testuser" back, just like it would in a real authenticated request.

            lenient().when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        }


        @Test
        void getExpenseByIdService_shouldReturnExpense_whenExpenseExistsAndBelongsToUser() {
            // Arrange: set up the fake data this test needs
            Expense expense = new Expense();
            expense.setId(10L);
            expense.setAmount(BigDecimal.valueOf(100));
            expense.setUser(mockUser);

            when(expenseRepository.findById(10L)).thenReturn(Optional.of(expense));

            // Act: call the actual method being tested
            Expense result = expenseService.getExpenseByIdService(10L);

            // Assert: check the result is what we expect
            assertEquals(10L, result.getId());
            assertEquals(BigDecimal.valueOf(100), result.getAmount());
        }

        @Test
        void getExpenseByIdService_shouldThrowException_whenExpenseDoesNotExist(){
            // Arrange
            Expense expense = new Expense();
            expense.setId(10L);
            expense.setAmount(BigDecimal.valueOf(100));
            expense.setUser(mockUser);

            when(expenseRepository.findById(99L)).thenReturn(Optional.empty());



            // Act & Assert
            assertThrows(ExpenseNotFoundException.class,()->{
                expenseService.getExpenseByIdService(99L);
            });


        }

    @Test
    void expenseExists_butBelongsToDifferentUser_shouldThrowExpenseNotFoundException(){
        // Arrange
        // making sure the user is differnt from the mockUser
        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setUsername("second");

        Expense expense = new Expense();
        expense.setId(10L);
        expense.setAmount(BigDecimal.valueOf(100));
        expense.setUser(secondUser);

        when(expenseRepository.findById(10L)).thenReturn(Optional.of(expense));

        // Act & Assert
        assertThrows(ExpenseNotFoundException.class,()->{
            expenseService.getExpenseByIdService(10L);
        });


    }


    @Test
    void createExpenseService_shouldSaveExpense_whenCategoryExists() {

            //Arrange

        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(BigDecimal.valueOf(200));
        request.setCategoryId(5L);
        request.setDate(LocalDate.of(2026, 7, 20));
        request.setNote("groceries");

        Category category = new Category();
        category.setId(5L);
        category.setName("Food");

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));

        // mockUser has no budget limit set (null by default, or you can explicitly set BigDecimal.ZERO)
        mockUser.setMonthlyBudgetLimit(BigDecimal.ZERO);

        // Mockito needs to know what save() returns — it doesn't do this automatically
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // Act
        Expense result = expenseService.createExpenseService(request);

        //Assert
        assertEquals(BigDecimal.valueOf(200), result.getAmount());
        assertEquals("Food", result.getCategory().getName());
        assertEquals(mockUser, result.getUser());


        // Verify the event was NEVER published, since budget is zero (no limit set)
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void createExpenseService_shouldThrowException_whenCategoryDoesNotExist() {
        // Arrange
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(BigDecimal.valueOf(200));
        request.setCategoryId(5L);
        request.setDate(LocalDate.of(2026, 7, 20));
        request.setNote("groceries");

        when(categoryRepository.findById(5L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ExpenseNotFoundException.class, () -> {
            expenseService.createExpenseService(request);
        });
    }

    @Test
    void createExpenseService_shouldPublishEvent_whenBudgetExceeded() {
        // Arrange
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(BigDecimal.valueOf(200));
        request.setCategoryId(5L);
        request.setDate(LocalDate.of(2026, 7, 20));
        request.setNote("groceries");

        Category category = new Category();
        category.setId(5L);
        category.setName("Food");

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Set a budget limit of 100
        mockUser.setMonthlyBudgetLimit(BigDecimal.valueOf(100));

        // Simulate that the user already has expenses this month totaling 250 (already over budget)
        Expense existingExpense = new Expense();
        existingExpense.setAmount(BigDecimal.valueOf(250));

        when(expenseRepository.findByUser_IdAndDateBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(existingExpense));

        // Act
        expenseService.createExpenseService(request);

        // Assert: the event SHOULD have been published this time
        verify(eventPublisher, times(1)).publishEvent(any(BudgetExceededEvent.class));
    }




}
