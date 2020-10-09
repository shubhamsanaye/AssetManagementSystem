package com.db.awmd.challenge.web;


import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;
  private static final Logger log = LoggerFactory.getLogger(AccountsController.class);
	
  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}"+ account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id "+ accountId);
    return this.accountsService.getAccount(accountId);
  }
  
  @PutMapping(path="/moneytransfer/{accountFrom}-{accountTo}/{amount}")
  public ResponseEntity<Object> moneytransferRequest(@PathVariable String accountFrom,@PathVariable String accountTo,@PathVariable BigDecimal amount) {
	  try{
		  this.accountsService.moneyTransferRequest(accountFrom, accountTo, amount);
	  }catch(Exception e) {
		  return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); 
	  }
	  return new ResponseEntity<>(HttpStatus.OK);
  }
  
}
