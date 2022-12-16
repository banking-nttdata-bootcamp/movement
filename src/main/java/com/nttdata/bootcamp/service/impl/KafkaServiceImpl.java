package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Movement;
import com.nttdata.bootcamp.entity.dto.DepositKafkaDto;
import com.nttdata.bootcamp.entity.dto.PaymentKafkaDto;
import com.nttdata.bootcamp.events.DepositCreatedEventKafka;
import com.nttdata.bootcamp.events.EventKafka;
import com.nttdata.bootcamp.events.PaymentCreatedEventKafka;
import com.nttdata.bootcamp.repository.MovementRepository;
import com.nttdata.bootcamp.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private MovementRepository movementRepository;

    @KafkaListener(
            topics = "${topic.customer.name:topic_deposit}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1")
    public void consumerDepositSave(EventKafka<?> eventKafka) {
        if (eventKafka.getClass().isAssignableFrom(DepositCreatedEventKafka.class)) {
            DepositCreatedEventKafka customerCreatedEvent = (DepositCreatedEventKafka) eventKafka;
            log.info("Received Data created event .... with Id={}, data={}",
                    customerCreatedEvent.getId(),
                    customerCreatedEvent.getData().toString());
            DepositKafkaDto rKafkaDto = ((DepositCreatedEventKafka) eventKafka).getData();
            Movement movement = new Movement();
            movement.setDni(rKafkaDto.getDni());


            this.movementRepository.save(movement).subscribe();
        }
    }

    @KafkaListener(
            topics = "${topic.customer.name:topic_payment}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1")
    public void consumerPaymentSave(EventKafka<?> eventKafka) {
        if (eventKafka.getClass().isAssignableFrom(PaymentCreatedEventKafka.class)) {
            PaymentCreatedEventKafka customerCreatedEvent = (PaymentCreatedEventKafka) eventKafka;
            log.info("Received Data created event .... with Id={}, data={}",
                    customerCreatedEvent.getId(),
                    customerCreatedEvent.getData().toString());
            PaymentKafkaDto KafkaDto = ((PaymentCreatedEventKafka) eventKafka).getData();
            Movement movement = new Movement();



            this.movementRepository.save(movement).subscribe();
        }
    }

}
