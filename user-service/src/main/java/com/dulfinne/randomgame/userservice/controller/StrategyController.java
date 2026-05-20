package com.dulfinne.randomgame.userservice.controller;

import com.dulfinne.randomgame.userservice.service.impl.TransactionProcessServiceImpl;
import com.dulfinne.randomgame.userservice.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.STRATEGY_BASE_URL)
@RequiredArgsConstructor
public class StrategyController {

  private final TransactionProcessServiceImpl transactionProcessService;

  @PostMapping(ApiPaths.SWITCH_TRANSACTION)
  public void switchTransactionStrategy() {
    transactionProcessService.moveToNextStrategy();
  }
}
