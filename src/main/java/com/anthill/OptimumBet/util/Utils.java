/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.anthill.OptimumBet.util;

/**
 *
 * @author truth
 */
public final class Utils {

    public static final int INPUT_MALICIOUS_URL = 26;
    public static final int OUTPUT_MALICIOUS_URL = 2;
    
    public static final int MAX_GPUS = 1;
    public static final int EPOCH = 2;
    public static final int BATCH_SIZE = 32;
    public static final int LIMIT = 10;

    public static final String MODEL_NAME_MNIST = "mlp_mnist";
    public static final String MODEL_NAME_MALICIOUS_URL = "mlp_malicious_url";
    
    public static final String MODEL_DIRE = "build/model";
    public static final String OUTPUT_DIRE = "mlp";

    public static final String QUESTION_MALICIOUS_URL = "url";
    public static final String ANSWER_MALICIOUS_URL = "isMalicious";
    
    public static final String IMAGE_FILE = "/0.png";
    public static final String MALICIOUS_URL_DATA_IN_PATH_RESOURCES = "/malicious_url.csv";
    public static final String MALICIOUS_URL_DATA_IN_PATH_SYSTEM = "/home/truth/uploads/djl-dataset/malicious_url.csv";

    private Utils() {}

}
