package com.navid.springstatemachine.service;

import com.navid.springstatemachine.domain.Payment;
import com.navid.springstatemachine.domain.PaymentEvent;
import com.navid.springstatemachine.domain.PaymentState;
import com.navid.springstatemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentServiceInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent>{


    private final PaymentRepository paymentRepository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state,
                               Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition,
                               StateMachine<PaymentState, PaymentEvent> stateMachine,
                               StateMachine<PaymentState, PaymentEvent> rootStateMachine) {

        Optional.of(message).ifPresent(paymentEventMessage -> {
            Optional.of(Long.class.cast(paymentEventMessage.getHeaders()
                            .getOrDefault(PaymentServiceImpl.PAYMENT_ID, -1L)))
                    .ifPresent(paymentId -> {
                        Payment payment = paymentRepository.findById(paymentId).get();
                        payment.setState(state.getId());
                        paymentRepository.save(payment);
                    });
        });
    }
}
