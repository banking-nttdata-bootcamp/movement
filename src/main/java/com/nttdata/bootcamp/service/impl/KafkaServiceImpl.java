package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Movement;
import com.nttdata.bootcamp.entity.dto.ChargeConsumptionKafkaDto;
import com.nttdata.bootcamp.entity.dto.DepositKafkaDto;
import com.nttdata.bootcamp.entity.dto.PaymentKafkaDto;
import com.nttdata.bootcamp.entity.dto.WithdrawalKafkaDto;
import com.nttdata.bootcamp.entity.dto.VirtualCoinKafkaDto;
import com.nttdata.bootcamp.entity.enums.EventType;
import com.nttdata.bootcamp.events.*;
import com.nttdata.bootcamp.repository.MovementRepository;
import com.nttdata.bootcamp.service.KafkaService;
import com.nttdata.bootcamp.service.MovementService;
import com.nttdata.bootcamp.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private MovementRepository movementRepository;
    @Autowired
    private MovementService movementService;

    @Autowired
    private KafkaTemplate<String, EventKafka<?>> producer;

    @Value("${topic.movement.name}")
    private String topicMovement;

    public void publish(Movement movement) {

        MovementCreatedEventKafka created = new MovementCreatedEventKafka();
        created.setData(movement);
        created.setId(UUID.randomUUID().toString());
        created.setType(EventType.CREATED);
        created.setDate(new Date());

        this.producer.send(topicMovement, created);
    }


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
            Long count=movementService.findByAccountNumber(rKafkaDto.getAccountNumber()).count().block();
            Movement movement = new Movement();
            if(count>Constant.COUNT_TRANSACTIONS)
                movement.setCommission(Constant.COMMISSION_TRANSACTIONS);
            else{
                movement.setCommission(rKafkaDto.getCommission());
            }
            movement.setDni(rKafkaDto.getDni());
            movement.setMovementNumber(rKafkaDto.getDepositNumber());
            movement.setAccountNumber(rKafkaDto.getAccountNumber());
            movement.setAmount(rKafkaDto.getAmount());

            movement.setTypeTransaction("DEPOSIT");
            movement.setCreationDate(new Date());
            movement.setModificationDate(new Date());
            movement.setStatus(Constant.STATUS);
            saveTopic(movement).subscribe();
        }
    }


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
            Long count=movementService.findByAccountNumber(KafkaDto.getAccountNumber()).count().block();
            Movement movement = new Movement();
            if(count>Constant.COUNT_TRANSACTIONS)
                movement.setCommission(Constant.COMMISSION_TRANSACTIONS);
            else{
                movement.setCommission(KafkaDto.getCommission());
            }
            movement.setDni(KafkaDto.getDni());
            movement.setMovementNumber(KafkaDto.getWithdrawalNumber());
            movement.setAccountNumber(KafkaDto.getAccountNumber());
            movement.setAmount(KafkaDto.getAmount()*-1);
            movement.setCommission(KafkaDto.getCommission());
            movement.setTypeTransaction("WITHDRAWAL");
            movement.setCreationDate(new Date());
            movement.setModificationDate(new Date());
            movement.setStatus(Constant.STATUS);


            saveTopic(movement).subscribe();
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
            movement.setDni(KafkaDto.getDni());
            movement.setMovementNumber(KafkaDto.getPaymentNumber());
            movement.setAccountNumber(KafkaDto.getAccountNumber());
            movement.setAmount(KafkaDto.getAmount());
            movement.setCommission(KafkaDto.getCommission());
            movement.setTypeTransaction("PAYMENT");
            movement.setCreationDate(new Date());
            movement.setModificationDate(new Date());
            movement.setStatus(Constant.STATUS);


            saveTopic(movement).subscribe();
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
            movement.setCreationDate(new Date());
            movement.setModificationDate(new Date());
            movement.setStatus(Constant.STATUS);

            saveTopic(movement).subscribe();
        }
    }

    @KafkaListener(
            topics = "${topic.customer.name:topic_virtualCoin}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1")
    public void consumerVirtualCoinSave(EventKafka<?> eventKafka) {
        if (eventKafka.getClass().isAssignableFrom(VirtualCoinCreatedEventKafka.class)) {
            VirtualCoinCreatedEventKafka customerCreatedEvent = (VirtualCoinCreatedEventKafka) eventKafka;
            log.info("Received Data created event .... with Id={}, data={}",
                    customerCreatedEvent.getId(),
                    customerCreatedEvent.getData().toString());
            VirtualCoinKafkaDto KafkaDto = ((VirtualCoinCreatedEventKafka) eventKafka).getData();
            if(KafkaDto.getFlagDebitCard()){
                Movement movement = new Movement();
                movement.setDni(KafkaDto.getDni());
                movement.setAccountNumber(KafkaDto.getNumberAccount());
                movement.setMovementNumber("");
                movement.setAmount(KafkaDto.getMount());
                movement.setCommission(0.00);
                movement.setTypeTransaction("virtualCoin");
                movement.setCreationDate(new Date());
                movement.setModificationDate(new Date());
                movement.setStatus(Constant.STATUS);
                saveTopic(movement).subscribe();
            }

        }
    }

    public Mono<Movement> saveTopic(Movement dataMovement){
        Mono<Movement> monoMovement = movementRepository.save(dataMovement);
        publish(monoMovement.block());
        return monoMovement;
    }

}
