package com.gestetud.external;
import java.time.LocalDate;
public record ExternalExamView(Long internalExamId,String externalExamId,ExternalExamStatus mappedStatus,String externalRawStatus,
 ExternalSyncStatus syncStatus,String syncError,String title,LocalDate date){}
