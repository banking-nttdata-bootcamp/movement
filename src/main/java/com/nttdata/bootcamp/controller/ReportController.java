package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Movement;
import com.nttdata.bootcamp.entity.dto.MovementDto;
import com.nttdata.bootcamp.service.MovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/movement")
public class ReportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MovementController.class);
    @Autowired
    private MovementService movementService;


    //Transfer search
    @GetMapping("/findTopMovements/{accountNumber}")
    public Flux<MovementDto> findTopMovements(@PathVariable("accountNumber") String accountNumber) {
        ArrayList<MovementDto> movementDtoArrayList = new ArrayList<MovementDto>();
        Flux<Movement> movementsFlux = movementService.findByAccountNumber(accountNumber);
        movementsFlux
                .toStream()
                .forEach( x -> movementDtoArrayList.add(new MovementDto(x.getMovementNumber(), x.getAmount(), x.getStatus())));

        return Flux.fromStream(movementDtoArrayList.stream().limit(10));
    }

}
