/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.anthill.OptimumBet.service;

import ai.djl.Model;
import ai.djl.basicdataset.cv.classification.Mnist;
import ai.djl.engine.Engine;
import ai.djl.metric.Metrics;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingResult;
import ai.djl.training.dataset.Dataset;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.SaveModelTrainingListener;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import com.anthill.OptimumBet.basic.CSVDataset;
import com.anthill.OptimumBet.basic.Mlp;
import com.anthill.OptimumBet.util.Utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;

/**
 *
 * @author truth
 */
@Service("tarinModelService")
public final class TrainModelService {

    public TrainingResult trainMnist() throws IOException, TranslateException {
        try ( Model model = Model.newInstance(Utils.MODEL_NAME_MNIST)) {
            model.setBlock(
                    new Mlp(
                            Mnist.IMAGE_HEIGHT * Mnist.IMAGE_WIDTH,
                            Mnist.NUM_CLASSES,
                            new int[]{128, 64}));

            RandomAccessDataset trainingSet = this.getDataSet(
                    Dataset.Usage.TRAIN,
                    Utils.BATCH_SIZE,
                    Utils.LIMIT);
            RandomAccessDataset validateSet = this.getDataSet(
                    Dataset.Usage.TEST,
                    Utils.BATCH_SIZE,
                    Utils.LIMIT);

            try ( Trainer trainer = model.newTrainer(
                    this.setupTrainingConfig(
                            Utils.MODEL_NAME_MNIST,
                            this.getGpus()))) {
                
                trainer.setMetrics(new Metrics());
                trainer.initialize(
                        new Shape(1, Mnist.IMAGE_HEIGHT * Mnist.IMAGE_WIDTH));
                EasyTrain.fit(trainer, Utils.EPOCH, trainingSet, validateSet);
                
                Path modelDire = Paths.get(Utils.MODEL_DIRE);
                Files.createDirectories(modelDire);

                model.setProperty("Epoch", String.valueOf(Utils.EPOCH));
                model.save(modelDire, Utils.MODEL_NAME_MNIST);
                
                return trainer.getTrainingResult();
            }
        }
    }
    
    public TrainingResult trainCSVDataset() throws IOException, TranslateException {

        try ( Model model = Model.newInstance(Utils.MODEL_NAME_MALICIOUS_URL)) {
            model.setBlock(
                    new Mlp(
                            Utils.INPUT_MALICIOUS_URL,
                            Utils.OUTPUT_MALICIOUS_URL,
                            new int[]{128, 64}));

            //get training and validation dataset
            
            RandomAccessDataset trainingSet = this.getCSVDataSet(
                    Dataset.Usage.TRAIN,
                    Utils.BATCH_SIZE,
                    Utils.LIMIT);
            RandomAccessDataset validateSet = this.getCSVDataSet(
                    Dataset.Usage.TEST,
                    Utils.BATCH_SIZE,
                    Utils.LIMIT);
            
            //set training configuration
            try ( Trainer trainer = model.newTrainer(
                    this.setupTrainingConfig(Utils.MODEL_NAME_MALICIOUS_URL,
                            this.getGpus()))) {
                            
                trainer.setMetrics(new Metrics());

                trainer.initialize(new Shape(1, Utils.INPUT_MALICIOUS_URL));
                System.out.println("trainingDataset size :"+ trainingSet.size());

                EasyTrain.fit(trainer, Utils.EPOCH, trainingSet, validateSet);

                Path modelDire = Paths.get(Utils.MODEL_DIRE);
                Files.createDirectories(modelDire);

                model.setProperty("Epoch", String.valueOf(Utils.EPOCH));
                model.save(modelDire, Utils.MODEL_NAME_MALICIOUS_URL);

                return trainer.getTrainingResult();
            }

        }
    }
    
    private DefaultTrainingConfig setupTrainingConfig(
            String outputDire,
            int maxGpus) {
        
        SaveModelTrainingListener listener = 
                new SaveModelTrainingListener(outputDire);
        listener.setSaveModelCallback(
                trainer -> {
                    TrainingResult result = trainer.getTrainingResult();
                    Model model = trainer.getModel();
                    model.setProperty(
                            "Accuracy",
                            String.format(
                                    "%.5f",
                                    result.getTrainEvaluation("Accuracy")));
                    model.setProperty(
                            "Loss",
                            String.format("%.5f", result.getValidateLoss()));
                });

        return new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
                .addEvaluator(new Accuracy())
                .optDevices(Engine.getInstance().getDevices(maxGpus))
                .addTrainingListeners(
                        TrainingListener.Defaults.logging(outputDire))
                .addTrainingListeners(listener);
    }
    
    private RandomAccessDataset getDataSet(
            Dataset.Usage usage,
            int batchSize,
            int limit) throws IOException {
        
        Mnist mnist = Mnist.builder()
                .optUsage(usage)
                .setSampling(batchSize, true)
                .optLimit(limit)
                .build();
        mnist.prepare(new ProgressBar());
        return mnist;
    }
    
    private RandomAccessDataset getCSVDataSet(Dataset.Usage usage, int batchSize, int limit) throws IOException, TranslateException {
        
        RandomAccessDataset csvDataset = CSVDataset.builder(Utils.MALICIOUS_URL_DATA_IN_PATH_SYSTEM)
                .optUsage(usage)
                .setSampling(batchSize, true)
                .optLimit(limit)
                .build();

//        csvDataset.prepare(new ProgressBar());
        return csvDataset;
    }
    
    private int getGpus() {
        return Engine.getInstance().getGpuCount();
    }
}
