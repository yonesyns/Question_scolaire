package com.gestetud.external;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
@Component
public class ExternalExamSyncListener {
 private final ExternalExamIntegrationService integration;
 public ExternalExamSyncListener(ExternalExamIntegrationService integration){this.integration=integration;}
 @Async @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
 public void onSyncRequested(ExamSyncRequested event){integration.sync(event.internalExamId());}
}
