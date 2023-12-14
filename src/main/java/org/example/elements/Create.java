package org.example.elements;

import org.example.models.Client;
import org.example.enums.ChooseRouteBy;

import java.util.Iterator;
import java.util.Map;

public class Create extends Element {
    public Create(double delay, ChooseRouteBy chooseRouteBy) {
        super(delay, 1, chooseRouteBy);
        super.addTimeNext(0.0);
    }

    @Override
    public void outAct() {
        super.outAct();
        super.removeTimeNext();
        super.addTimeNext(super.getTcurr() + super.getDelay());

        Client newClient = new Client(super.getTcurr());

        Process nextProcess = chooseNextElementByPriority();

        if (nextProcess != null) {
            nextProcess.inAct(newClient);
        }
    }

    private Process chooseNextElementByPriority() {
        if (!nextElements.isEmpty()) {
            Iterator<Map.Entry<Integer, Process>> iterator =
                    nextProcessesPriorities.entrySet().iterator();
            Map.Entry<Integer, Process> keyValue = iterator.next();
            int queue = keyValue.getValue().getQueue();
            Process nextElement = keyValue.getValue();
            while (iterator.hasNext()) {
                Process current = iterator.next().getValue();
                if (current.getQueue() < queue) {
                    queue = current.getQueue();
                    nextElement = current;
                }
            }
            return nextElement;
        }
        return null;
    }
}
