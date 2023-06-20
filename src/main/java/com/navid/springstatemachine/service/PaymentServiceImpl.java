package com.navid.springstatemachine.service;

import com.navid.springstatemachine.domain.Payment;
import com.navid.springstatemachine.domain.PaymentEvent;
import com.navid.springstatemachine.domain.PaymentState;
import com.navid.springstatemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    public static final String PAYMENT_ID = "payment_id";

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;

    private final PaymentServiceInterceptor interceptor;


    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine sm  = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.PRE_AUTH_APPROVED);
        return sm;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.AUTHORIZE);
        return sm;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.AUTH_DECLINED);
        return build(paymentId);
    }

    private StateMachine sendEvent(Long paymentId,
                                   StateMachine<PaymentState, PaymentEvent> sm,
                                   PaymentEvent event) {
        Message<PaymentEvent> msg = MessageBuilder.withPayload(event).setHeader(PAYMENT_ID, paymentId).build();
        sm.sendEvent((msg));
        return sm;

    }

    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId){

        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(paymentId));
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                sma.addStateMachineInterceptor(interceptor);
                        sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(),null,null,null));
                                }
                );
        sm.start();
        return sm;
    }

}
