package com.navid.springstatemachine.service;

import com.navid.springstatemachine.domain.Payment;
import com.navid.springstatemachine.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;


@SpringBootTest
class PaymentServiceImplTest {

    Payment payment;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentService paymentService;

    @BeforeEach
    void initialize(){
        payment = Payment.builder().amount(BigDecimal.valueOf(12)).build();
    }
    @Test
    void preAuthTest(){
        Payment savedPayment = paymentService.newPayment(this.payment);
        paymentService.preAuth(savedPayment.getId());
        System.out.println("test");
    }
}