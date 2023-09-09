package com.github.andylke.demo.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountRestController {

  @Autowired private AccountRepository accountRepository;

  @GetMapping
  public List<Account> findAllByCustomerNumber(@RequestParam long customerNumber) {
    return accountRepository.findAllByCustomerNumber(customerNumber);
  }

  @PostMapping
  public Account newAccount(@RequestBody Account account) {
    return accountRepository.save(account);
  }
}
