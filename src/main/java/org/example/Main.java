package org.example;

import org.example.elements.Create;
import org.example.elements.Dispose;
import org.example.elements.Element;
import org.example.elements.Process;
import org.example.enums.ChooseRouteBy;
import org.example.enums.Distribution;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final int CASHIER_COUNT = 5;
        final int MAX_QUEUE = 5;

        Create create = new Create(1, ChooseRouteBy.PRIORITY);
        create.setName("Create");
        create.setDistribution(Distribution.EXPONENTIAL);

        Dispose dispose = new Dispose();
        dispose.setName("Dispose");

        List<Process> cashiers = new ArrayList<>();
        for (int i = 0; i < CASHIER_COUNT; i++) {
            Process cashier = new Process(4.5, 1, ChooseRouteBy.PROBABILITY);
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

        Model model = new Model(elements);
        model.simulate(1_000.0);
    }
}
