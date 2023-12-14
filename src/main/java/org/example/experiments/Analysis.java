package org.example.experiments;

import org.example.elements.Create;
import org.example.elements.Dispose;
import org.example.elements.Element;
import org.example.elements.Process;
import org.example.enums.ChooseRouteBy;
import org.example.enums.Distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Analysis {
    public static void main(String[] args) {
//        AnalysisModel model = getModel(5, 5, 1, 4.5);
//        model.setPrintStatisticsFrequency(10_000);
//        model.simulate(1_000_000.0);

        final int EXPERIMENTS_COUNT = 20;
        Map<Integer, List<Double>> cashierCounts = new HashMap<>();
        cashierCounts.put(1, null);
        cashierCounts.put(2, null);
        cashierCounts.put(3, null);
        cashierCounts.put(4, null);
        cashierCounts.put(5, null);

        runExperiments(EXPERIMENTS_COUNT, cashierCounts);
        analyseQueueTimeMean(cashierCounts);
    }

    public static void analyseQueueTimeMean(Map<Integer, List<Double>> testedValues) {
        Map<Integer, Double> averageValues = new HashMap<>();

        for (Map.Entry<Integer, List<Double>> testedValue : testedValues.entrySet()) {
            averageValues.put(testedValue.getKey(), MathUtil.getAverage(testedValue.getValue()));
        }

        averageValues.forEach((key, value) ->
                System.out.printf("Average time in queue (%d cashiers): %.4f\n", key, value));

        double overallAverage = MathUtil.getAverage(new ArrayList<>(averageValues.values()));

        int levelsCount = testedValues.size();
        int runTimes = new ArrayList<>(testedValues.values()).get(0).size();

        double factorS = 0, residualS = 0;

        for (Map.Entry<Integer, Double> averageValue : averageValues.entrySet()) {
            factorS += Math.pow(averageValue.getValue() - overallAverage, 2);
        }
        factorS *= runTimes;

        for (Map.Entry<Integer, List<Double>> testedValue : testedValues.entrySet()) {
            for (Double value : testedValue.getValue()) {
                residualS += Math.pow(value - averageValues.get(testedValue.getKey()), 2);
            }
        }

        double factorDispersion = factorS;
        double residualDispersion = residualS / (levelsCount * (runTimes - 1));

        double f = factorDispersion / residualDispersion;

        System.out.printf("F = %.4f", f);
    }

    private static void runExperiments(int runTimes, Map<Integer, List<Double>> testedValues) {
        for (Integer testedValue : testedValues.keySet()) {
            List<Double> runResults = new ArrayList<>();
            for (int i = 0; i < runTimes; i++) {
                AnalysisModel model = getModel(testedValue, 5, 1, 4.5);
                model.setPrintStatisticsFrequency(1_000_000);
                model.setPrintProtocol(false);
                model.simulate(1_000_000);

                runResults.add(model.calculateStatistic());
            }
            testedValues.put(testedValue, runResults);
        }
    }

    private static AnalysisModel getModel(int cashiersCount, int queueLength,
                                          double timeCreate, double timeServe) {
        final int CASHIER_COUNT = cashiersCount;
        final int MAX_QUEUE = queueLength;

        Create create = new Create(timeCreate, ChooseRouteBy.PRIORITY);
        create.setName("Create");
        create.setDistribution(Distribution.EXPONENTIAL);

        Dispose dispose = new Dispose();
        dispose.setName("Dispose");

        List<Process> cashiers = new ArrayList<>();
        for (int i = 0; i < CASHIER_COUNT; i++) {
            Process cashier = new Process(timeServe, 1, ChooseRouteBy.PROBABILITY);
            cashier.setName("Cashier " + (i + 1));
            cashier.setDistribution(Distribution.EXPONENTIAL);
            cashier.setMaxqueue(MAX_QUEUE);
            cashier.addNextElement(dispose, 1.0);

            create.addNextElement(cashier, i + 1);

            cashiers.add(cashier);
        }

        ArrayList<Element> elements = new ArrayList<>();
        elements.add(create);
        elements.addAll(cashiers);
        elements.add(dispose);

        return new AnalysisModel(elements);
    }
}
