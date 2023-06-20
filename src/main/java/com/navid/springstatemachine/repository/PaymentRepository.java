package com.navid.springstatemachine.repository;

import com.navid.springstatemachine.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
