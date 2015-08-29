package listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ValueChangeEventSource {

    private List<MathToolValueChangeListener> listeners = new ArrayList();

    public synchronized void addEventListener(MathToolValueChangeListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeEventListener(MathToolValueChangeListener listener) {
        listeners.remove(listener);
    }

    private synchronized void fireEvent() {
        ValueChangeEvent event = new ValueChangeEvent(this);
        Iterator<MathToolValueChangeListener> i = listeners.iterator();
        while (i.hasNext()) {
            i.next().valueChange(event);
        }
    }

}
