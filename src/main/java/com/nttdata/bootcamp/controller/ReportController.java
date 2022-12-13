package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Movement;
import com.nttdata.bootcamp.entity.dto.MovementDto;
import com.nttdata.bootcamp.service.MovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/report")
public class ReportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MovementController.class);
    @Autowired
    private MovementService movementService;

    @GetMapping("/getCommissionsByAccount/{accountNumber}/{date1}/{date2}")
    public Flux<Movement> getCommissionsByAccount(@PathVariable("accountNumber") String accountNumber,
                                                  @PathVariable("date1") String date1,
                                                  @PathVariable("date2") String date2) {
        ArrayList<MovementDto> movementDtoArrayList = new ArrayList<MovementDto>();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

            Flux<Movement> movementsFlux = movementService.findCommissionByAccountNumber(accountNumber,"commission");
            movementsFlux
                    .toStream()
                    .filter( x -> {
                        try {
                            return x.getCreationDate().after(formato.parse(date1)) && x.getCreationDate().before(formato.parse(date2));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    });

        //return Flux.fromStream(movementDtoArrayList.stream());
        return movementsFlux;
    }
    //report general of product
    @GetMapping("/getReportByProduct/{accountNumber}/{date1}/{date2}")
    public Flux<Movement> getReportByProduct(@PathVariable("accountNumber") String accountNumber,
                                                  @PathVariable("date1") String date1,
                                                  @PathVariable("date2") String date2) {
        ArrayList<MovementDto> movementDtoArrayList = new ArrayList<MovementDto>();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        Flux<Movement> movementsFlux = movementService.findByAccountNumber(accountNumber);
        movementsFlux
                .toStream()
                .filter( x -> {
                    try {
                        return x.getCreationDate().after(formato.parse(date1)) && x.getCreationDate().before(formato.parse(date2));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                });

        //return Flux.fromStream(movementDtoArrayList.stream());
        return movementsFlux;
    }

    //Report Find top 10 movements of debit and credit card
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
