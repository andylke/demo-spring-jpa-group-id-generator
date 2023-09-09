package com.github.andylke.demo.account;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Account {

  @Id
  @GeneratedValue(generator = "AccountNumberGenerator")
  @GenericGenerator(name = "AccountNumberGenerator", type = AccountNumberGenerator.class)
  private Long accountNumber;

  private Long customerNumber;

  private BigDecimal accountBalance;
}
