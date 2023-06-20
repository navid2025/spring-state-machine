package com.navid.springstatemachine.config;

import com.navid.springstatemachine.domain.PaymentEvent;
import com.navid.springstatemachine.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
@Slf4j
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTH)
                .end(PaymentState.AUTH_ERROR)
                .end(PaymentState.PRE_AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE)/*.action(preAuthentication())
                .guard(paymentIdGuard())*/
                .and()
                .withExternal()
                .source(PaymentState.NEW).event(PaymentEvent.PRE_AUTH_APPROVED).target(PaymentState.PRE_AUTH)
                .and()
                .withExternal()
                .source(PaymentState.NEW).event(PaymentEvent.PRE_AUTH_DECLINED).target(PaymentState.PRE_AUTH_ERROR)
                .and()
                .withExternal()
                .source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH).event(PaymentEvent.AUTHORIZE)
                .and()
                .withExternal()
                .source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APPROVED)
                .and()
                .withExternal()
                .source(PaymentState.PRE_AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {

        StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info(String.format("state changed from: %s, to: %s", from, to));
            }
        };
        config.withConfiguration()
                .listener(adapter);
    }

//    public Action<PaymentState, PaymentEvent> preAuthentication() {
//        return context -> {
//            System.out.println("action is called");
//
//            if(new Random(10).nextInt() > 8 ) {
//                context.getStateMachine().sendEvent(Mono.just(MessageBuilder
//                        .withPayload(PaymentEvent.PRE_AUTH_APPROVED)
//                        .setHeader(PaymentServiceImpl.PAYMENT_ID, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID)).build()));
//            }
//            else {
//                 context.getStateMachine().sendEvent(Mono.just(MessageBuilder
//                         .withPayload(PaymentEvent.PRE_AUTH_DECLINED)
//                         .setHeader(PaymentServiceImpl.PAYMENT_ID, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID)).build()));
//            }
//        };
//    }

//    public Guard<PaymentState, PaymentEvent> paymentIdGuard(){
//        return context ->  {
//            return context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID) != null;
//        };
//    }
}
