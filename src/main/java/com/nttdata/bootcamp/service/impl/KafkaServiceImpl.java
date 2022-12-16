package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Movement;
import com.nttdata.bootcamp.entity.dto.ChargeConsumptionKafkaDto;
import com.nttdata.bootcamp.entity.dto.DepositKafkaDto;
import com.nttdata.bootcamp.entity.dto.PaymentKafkaDto;
import com.nttdata.bootcamp.entity.dto.WithdrawalKafkaDto;
import com.nttdata.bootcamp.events.EventKafka;
import com.nttdata.bootcamp.events.DepositCreatedEventKafka;
import com.nttdata.bootcamp.events.WithdrawalCreatedEventKafka;
import com.nttdata.bootcamp.events.PaymentCreatedEventKafka;
import com.nttdata.bootcamp.events.ChargeConsumptionCreatedEventKafka;
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
            movement.setMovementNumber(rKafkaDto.getDepositNumber());
            movement.setAccountNumber(rKafkaDto.getAccountNumber());
            movement.setAmount(rKafkaDto.getAmount());
            movement.setCommission(rKafkaDto.getCommission());
            movement.setTypeTransaction("DEPOSIT");

            this.movementRepository.save(movement).subscribe();
        }
    }

    @KafkaListener(
            topics = "${topic.customer.name:topic_payment}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1")


    @KafkaListener(
            topics = "${topic.customer.name:topic_withdrawal}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1")
    public void consumerWithdrawalSave(EventKafka<?> eventKafka) {
        if (eventKafka.getClass().isAssignableFrom(WithdrawalCreatedEventKafka.class)) {
            WithdrawalCreatedEventKafka customerCreatedEvent = (WithdrawalCreatedEventKafka) eventKafka;
            log.info("Received Data created event .... with Id={}, data={}",
                    customerCreatedEvent.getId(),
                    customerCreatedEvent.getData().toString());
            WithdrawalKafkaDto KafkaDto = ((WithdrawalCreatedEventKafka) eventKafka).getData();
            Movement movement = new Movement();
            movement.setDni(KafkaDto.getDni());
            movement.setMovementNumber(KafkaDto.getWithdrawalNumber());
            movement.setAccountNumber(KafkaDto.getAccountNumber());
            movement.setAmount(KafkaDto.getAmount()*-1);
            movement.setCommission(KafkaDto.getCommission());
            movement.setTypeTransaction("WITHDRAWAL");


            this.movementRepository.save(movement).subscribe();
        }
    }

    public void consumerPaymentSave(EventKafka<?> eventKafka) {
        if (eventKafka.getClass().isAssignableFrom(PaymentCreatedEventKafka.class)) {
            PaymentCreatedEventKafka customerCreatedEvent = (PaymentCreatedEventKafka) eventKafka;
            log.info("Received Data created event .... with Id={}, data={}",
                    customerCreatedEvent.getId(),
                    customerCreatedEvent.getData().toString());
            PaymentKafkaDto KafkaDto = ((PaymentCreatedEventKafka) eventKafka).getData();
            Movement movement = new Movement();
            movement.setDni(KafkaDto.getDni());
            movement.setMovementNumber(KafkaDto.getPaymentNumber());
            movement.setAccountNumber(KafkaDto.getAccountNumber());
            movement.setAmount(KafkaDto.getAmount());
            movement.setCommission(KafkaDto.getCommission());
            movement.setTypeTransaction("PAYMENT");


            this.movementRepository.save(movement).subscribe();
        }
    }


    @KafkaListener(
            topics = "${topic.customer.name:topic_charge}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1")
    public void consumerChargeSave(EventKafka<?> eventKafka) {
        if (eventKafka.getClass().isAssignableFrom(ChargeConsumptionCreatedEventKafka.class)) {
            ChargeConsumptionCreatedEventKafka customerCreatedEvent = (ChargeConsumptionCreatedEventKafka) eventKafka;
            log.info("Received Data created event .... with Id={}, data={}",
                    customerCreatedEvent.getId(),
                    customerCreatedEvent.getData().toString());
            ChargeConsumptionKafkaDto KafkaDto = ((ChargeConsumptionCreatedEventKafka) eventKafka).getData();
            Movement movement = new Movement();
            movement.setDni(KafkaDto.getDni());
            movement.setMovementNumber(KafkaDto.getChargeNumber());
            movement.setAccountNumber(KafkaDto.getAccountNumber());
            movement.setAmount(KafkaDto.getAmount()*-1);
            movement.setCommission(KafkaDto.getCommission());
            movement.setTypeTransaction("CHARGE");

            this.movementRepository.save(movement).subscribe();
        }
    }

}
