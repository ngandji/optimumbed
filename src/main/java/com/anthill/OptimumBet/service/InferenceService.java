/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.anthill.OptimumBet.service;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import com.anthill.OptimumBet.basic.Mlp;
import com.anthill.OptimumBet.util.Utils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author truth
 */
@Service("inferenceService")
public final class InferenceService {

    private static final Logger logger = LoggerFactory.getLogger(InferenceService.class);

    public Classifications imageClassification() throws IOException, ModelException, TranslateException {
        
        Resource resource = new ClassPathResource(Utils.IMAGE_FILE);
        Path imageFile = Paths.get(resource.getURL().getPath());
        Image img = ImageFactory.getInstance().fromFile(imageFile);

        try (Model model = Model.newInstance(Utils.MODEL_NAME_MNIST)) {
            model.setBlock(new Mlp(28 * 28, 10, new int[] {128, 64}));

            // Assume you have run TrainMnist.java example, and saved model in build/model folder.
            model.load(Paths.get(Utils.MODEL_DIRE));

            List<String> classes = IntStream.
                    range(0, 10)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.toList());
            Translator<Image, Classifications> translator =
                    ImageClassificationTranslator.builder()
                            .addTransform(new ToTensor())
                            .optSynset(classes)
                            .build();

            try (Predictor<Image, Classifications> predictor = 
                    model.newPredictor(translator)) {
                return predictor.predict(img);
            }
        }
    }
}
