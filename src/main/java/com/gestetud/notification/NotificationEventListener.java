package com.gestetud.notification;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class NotificationEventListener {
    private final NotificationDispatcher dispatcher;
    public NotificationEventListener(NotificationDispatcher dispatcher){this.dispatcher=dispatcher;}
    @Async
    @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
    public void onNotificationRequested(NotificationRequested event){dispatcher.dispatch(event);}
}
