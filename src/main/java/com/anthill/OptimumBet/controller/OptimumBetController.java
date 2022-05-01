/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.anthill.OptimumBet.controller;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;
import com.anthill.OptimumBet.service.InferenceService;
import com.anthill.OptimumBet.service.OptimumBetService;
import com.anthill.OptimumBet.service.TrainModelService;
import com.anthill.OptimumBet.util.Message;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author truth
 */
@RestController
@RequestMapping("api/optimum")
@CrossOrigin(origins = "*")
public class OptimumBetController {
    @Autowired
    private TrainModelService trainModelService;
    
    @Autowired
    private InferenceService inferenceService;

    @Autowired
    private OptimumBetService betService;

    @GetMapping("train_mnist")
    public ResponseEntity<Message> trainMnist() throws IOException, TranslateException {
        this.trainModelService.trainMnist();
        return ResponseEntity.ok(new Message("Train Mnist is sucesful !"));
    }

    @GetMapping("train_csv")
    public ResponseEntity<Message> trainCSVDataSet() throws IOException, TranslateException {
        trainModelService.trainCSVDataset();
        return ResponseEntity.ok(new Message("Train CSV Dataset is sucesful !"));
    }

    @GetMapping("image_classification")
    public ResponseEntity<Message> predict() throws IOException, ModelException, TranslateException {
        Classifications prediction = this.inferenceService.imageClassification();
        return ResponseEntity.ok(new Message(prediction.toJson()));
    }

    @GetMapping("test")
    public ResponseEntity<Message> testAccessToDataset(
            @RequestParam(name="path") boolean isToPathSystem) throws IOException {
        return ResponseEntity.ok(new Message(this.betService.readMaliciousUrl(isToPathSystem)));
    }

    @GetMapping("hello")
    public ResponseEntity<Message> hello() {
        return ResponseEntity.ok(new Message("Hello"));
    }
}
