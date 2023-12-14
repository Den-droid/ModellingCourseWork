package org.example;

import org.example.elements.Element;
import org.example.elements.Process;

import java.util.ArrayList;

public class Model {
    private final ArrayList<Element> list;
    double tnext, tcurr;
    Element event;
    int eventCount;

    public Model(ArrayList<Element> elements) {
        list = elements;
        tnext = 0.0;
        tcurr = tnext;
    }

    public void simulate(double time) {
        while (tcurr < time) {
            eventCount++;

            tnext = Double.MAX_VALUE;
            for (Element e : list) {
                if (e.getTnext() < tnext) {
                    tnext = e.getTnext();
                    event = e;
                }
            }
            System.out.println("\nIt's time for event in " +
                    event.getName() +
                    ", time = " + tnext);
            for (Element e : list) {
                e.doStatistics(tnext - tcurr);
            }
            tcurr = tnext;
            for (Element e : list) {
                e.setTcurr(tcurr);
            }
            event.outAct();
            for (Element e : list) {
                if (e.getTnext() == tcurr) {
                    eventCount++;

                    e.outAct();
                }
            }
            printInfo();
        }
        System.out.println("Event count: " + eventCount);
        printResult();
    }

    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }

    public void printResult() {
        int totalSuccess = 0;
        int totalFailure = 0;
        int cashierCount = 0;
        double totalMeanQueue = 0;
        double totalMeanLoad = 0;
        for (Element e : list) {
            if (e instanceof Process p) {
                cashierCount++;
                totalSuccess += p.getQuantity();
                totalFailure += p.getFailure();
                totalMeanLoad += p.getMeanLoad();
                totalMeanQueue += p.getMeanQueue();
            }
        }
        System.out.println("\n-------------RESULTS-------------");
        System.out.println("Average time in queue: " + (totalMeanQueue / totalSuccess / cashierCount));
        System.out.println("Average people in queue: " + (totalMeanQueue / tcurr / cashierCount));
        System.out.println("Average load: " + (totalMeanLoad / tcurr / cashierCount));
        System.out.println("Percent of served people: " +
                (100.0 * totalSuccess / (totalSuccess + totalFailure)));
        System.out.println("Percent of not served people: " +
                (100.0 * totalFailure / (totalSuccess + totalFailure)));
    }
}
