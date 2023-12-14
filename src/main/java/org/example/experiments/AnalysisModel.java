package org.example.experiments;

import org.example.elements.Element;
import org.example.elements.Process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class AnalysisModel {
    private final ArrayList<Element> list;
    private final Map<Double, Double> statisticMap;
    double tnext, tcurr;
    Element event;
    int printStatisticsFrequency, printStatisticsNextTime;
    boolean printProtocol;

    public AnalysisModel(ArrayList<Element> elements) {
        statisticMap = new TreeMap<>();
        list = elements;
        tnext = 0.0;
        tcurr = tnext;
        printStatisticsFrequency = 25;
        printStatisticsNextTime = printStatisticsFrequency;
        printProtocol = true;
    }

    public void simulate(double time) {
        while (tcurr < time) {
            tnext = Double.MAX_VALUE;
            for (Element e : list) {
                if (e.getTnext() < tnext) {
                    tnext = e.getTnext();
                    event = e;
                }
            }
            for (Element e : list) {
                e.doStatistics(tnext - tcurr);
            }

            if (tcurr > printStatisticsNextTime) {
                statisticMap.put(tcurr, calculateStatistic());
                printStatisticsNextTime += printStatisticsFrequency;
            }
            tcurr = tnext;
            for (Element e : list) {
                e.setTcurr(tcurr);
            }
            event.outAct();
            for (Element e : list) {
                if (e.getTnext() == tcurr) {
                    e.outAct();
                }
            }
        }
        statisticMap.put(tcurr, calculateStatistic());

        if (printProtocol)
            printStatisticList();
    }

    public double calculateStatistic() {
        int totalSuccess = 0;
        int cashierCount = 0;
        double totalMeanQueue = 0;
        for (Element e : list) {
            if (e instanceof Process p) {
                cashierCount++;
                totalSuccess += p.getQuantity();
                totalMeanQueue += p.getMeanQueue();
            }
        }
        return totalMeanQueue / totalSuccess / cashierCount;
    }

    public void printStatisticList() {
        System.out.println("List length: " + statisticMap.size());
        System.out.print("Values: ");
        Iterator<Map.Entry<Double, Double>> iterator = statisticMap.entrySet().iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Map.Entry<Double, Double> keyValue = iterator.next();
            if (i % 7 == 0)
                System.out.println();
            System.out.printf("%.0f -> %.4f; ", keyValue.getKey(), keyValue.getValue());
        }
        System.out.println();
    }

    public void setPrintStatisticsFrequency(int printStatisticsFrequency) {
        this.printStatisticsFrequency = printStatisticsFrequency;
        this.printStatisticsNextTime = this.printStatisticsFrequency;
    }

    public void setPrintProtocol(boolean printProtocol) {
        this.printProtocol = printProtocol;
    }
}
