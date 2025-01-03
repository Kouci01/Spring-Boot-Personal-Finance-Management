package com.finance.management.controller;

import com.finance.management.config.JwtHelper;
import com.finance.management.mapper.UserMapper;
import com.finance.management.model.Transaction;
import com.finance.management.model.User;
import com.finance.management.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {
    private final FinanceService financeService;
    private final UserMapper userMapper;

    @Autowired
    public FinanceController(FinanceService financeService, UserMapper userMapper){
        this.financeService = financeService;
        this.userMapper = userMapper;
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> addTransaction(@RequestBody Transaction transaction,
                                                 @CookieValue(value = "jwt", defaultValue = "None") String token) {
        String email = JwtHelper.extractUsername(token);
        Optional<User> user= userMapper.findByEmail(email);
        if(user.isPresent()){
            transaction.setUserId(user.get().getId());
            financeService.addTransaction(transaction);
            return ResponseEntity.ok("Transaction added successfully");
        }else{
            return ResponseEntity.badRequest().body("Internal Error");
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(Transaction transaction,
                                             @CookieValue(value = "jwt", defaultValue = "None") String token) {
        String email = JwtHelper.extractUsername(token);
        Optional<User> user = userMapper.findByEmail(email);
        if(user.isPresent()){
            transaction.setUserId(user.get().getId());
            return ResponseEntity.ok(financeService.getTransactions(transaction));
        }
        return ResponseEntity.ok(List.of());
    }
}
