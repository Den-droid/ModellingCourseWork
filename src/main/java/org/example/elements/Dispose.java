package org.example.elements;

import org.example.models.Client;
import org.example.enums.ChooseRouteBy;

public class Dispose extends Element {
    double totalClientsTime;
    private Client nextClient;

    public Dispose() {
        super(0, Integer.MAX_VALUE, ChooseRouteBy.PROBABILITY);
    }

    public void inAct(Client client) {
        nextClient = client;

        outAct();
    }

    @Override
    public void outAct() {
        super.increaseQuantity();

        totalClientsTime += getTcurr() - nextClient.getTimeStart();
    }

    public double getTotalClientsTime() {
        return totalClientsTime;
    }
}
