package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.events.EventKafka;

public interface KafkaService {
    void consumerDepositSave(EventKafka<?> eventKafka);
    void consumerPaymentSave(EventKafka<?> eventKafka);
}