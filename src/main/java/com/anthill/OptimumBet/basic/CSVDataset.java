/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.anthill.OptimumBet.basic;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.training.dataset.Record;
import ai.djl.util.Progress;
import com.anthill.OptimumBet.util.Utils;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author truth
 */
public class CSVDataset extends RandomAccessDataset {

    private final List<CSVRecord> csvRecords;

    private CSVDataset(Builder builder) {
        super(builder);
        this.csvRecords = builder.dataset;
    }

    @Override
    public Record get(NDManager manager, long index) {
        CSVRecord record = csvRecords.get(Math.toIntExact(index));
        NDArray datum = manager.create(
                encode(record.get(Utils.QUESTION_MALICIOUS_URL)));
        NDArray label = manager.create(
                Float.parseFloat(record.get(Utils.ANSWER_MALICIOUS_URL)));

        return new Record(new NDList(datum), new NDList(label));
    }

    @Override
    protected long availableSize() {
        return this.csvRecords.size();
    }

    private int[] encode(String url) {
        url = url.toLowerCase();
        int[] encoding = new int[26];
        for (char ch : url.toCharArray()) {
            int index = ch - 'a';
            if (index < 26 && index >= 0) {
                encoding[ch - 'a']++;
            }
        }
        return encoding;
    }
    
    @Override
    public void prepare(Progress prgrs) {}

    public static Builder builder(String csvFilePath) {
        return new Builder(csvFilePath);
    }

    public static final class Builder extends BaseBuilder<Builder> {

        List<CSVRecord> dataset;
        private final String csvFilePath;
        private Usage usage;

        Builder(String csvFilePath) {
            this.csvFilePath = csvFilePath;
            this.usage = Usage.TRAIN;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Builder optUsage(Usage usage) {
            this.usage = usage;
            return this;
        }

        public CSVDataset build() throws IOException {

            try ( Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
                    CSVParser csvParser = new CSVParser(
                    reader,
                    CSVFormat.DEFAULT.builder()
                        .setHeader(Utils.QUESTION_MALICIOUS_URL, Utils.ANSWER_MALICIOUS_URL)
                        .setSkipHeaderRecord(true)
                        .setIgnoreHeaderCase(true)
                        .setTrim(true)
                        .build())) {
                List<CSVRecord> csvRecords = csvParser.getRecords();
                int index = (int) (csvRecords.size() * 0.8);
                switch (usage) {
                    case TRAIN: {
                        dataset = csvRecords.subList(0, index);
                        break;
                    }
                    case TEST: {
                        dataset = csvRecords.subList(index, csvRecords.size());
                        break;
                    }
                    default:{
                        dataset = csvRecords;
                        break;
                    }
                }
            }
            return new CSVDataset(this);
        }
    }
}
