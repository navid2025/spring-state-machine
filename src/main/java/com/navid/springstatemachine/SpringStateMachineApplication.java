package com.navid.springstatemachine;

import com.navid.springstatemachine.service.PaymentServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringStateMachineApplication {

    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(SpringStateMachineApplication.class, args);
        PaymentServiceImpl ps = (PaymentServiceImpl) ctx.getBean("paymentServiceImpl");
        System.out.println("test");
    }

}
