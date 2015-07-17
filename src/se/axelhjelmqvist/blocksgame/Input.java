package se.axelhjelmqvist.blocksgame;

import android.view.MotionEvent;

import java.util.TreeMap;
import java.util.ArrayList;
import android.graphics.Rect;

public class Input {
    private class ButtonRecord {
        public Rect area;
        public ArrayList<Integer> activePointers;
        public int state;
        public long downTick;
        public ButtonRecord() {
            activePointers = new ArrayList<Integer>();
        }
    }

    public class EventData {
        public int button, state;
        public volatile boolean consumed;
        public EventData(int id, int state) {
            button = id;
            this.state = state;
            consumed = false;
        }
        public void set(int id, int state) {
            button = id;
            this.state = state;
            consumed = false;
        }
    }

    Object inputLock;

    TreeMap<Integer, Integer> pointerToButton;

    ArrayList<ButtonRecord> buttons;

    ArrayList<EventData> events;

    ArrayList<EventData> buttonEvents;

    EventData lastEvent;

    long ticks;

    public Input() {
        inputLock = new Object();
        pointerToButton = new TreeMap<Integer, Integer>();
        buttons = new ArrayList<ButtonRecord>();
        buttonEvents = new ArrayList<EventData>();
        events = new ArrayList<EventData>();
        lastEvent = null;
        ticks = 0;
	}

	public int addButton(float x, float y, float width, float height) {
        ButtonRecord record = new ButtonRecord();
        record.area = new Rect((int)x, (int)y, (int)(x + width), (int)(y + height));
        record.state = 0;
        record.downTick = 0;
        int buttonsCount = 0;
        synchronized (inputLock) {
            buttons.add(record);
            buttonsCount = buttons.size();
        }
        return buttonsCount - 1;
    }

    public int checkButton(int id) {
        ButtonRecord result = null;
        synchronized (inputLock) {
            if (id < buttons.size()) {
                result = buttons.get(id);
                if (result.state == 1 && result.downTick < ticks) {
                    ++result.state;
                }
            }
        }
        return result == null ? -1 : result.state;
    }

    public EventData getEvent() {
        EventData result = null;
        synchronized(inputLock) {
            if (lastEvent != null) {
                lastEvent.consumed = true;
            }
            int eventsCount = buttonEvents.size();
            if (eventsCount > 0) {
                result = buttonEvents.remove(eventsCount - 1);
                events.add(result);
            }
        }
        lastEvent = result;
        return result;
    }

    public void updateState() {
        synchronized (inputLock) {
            ++ticks;
        }
    }

    private void createEvent(int button, int state) {
        EventData event = null;
        int counter = 0;
        while (event == null && counter < events.size()) {
            event = events.get(counter);
            if (!event.consumed) {
                event = null;
            } else {
                events.remove(counter);
            }
            ++counter;
        }
        if (event == null) {
            event = new EventData(button, state);
        } else {
            event.set(button, state);
        }
        buttonEvents.add(event);
    }

    private float getX(MotionEvent evt, int pointerId) {
        float x = -1;
        try {
            x = evt.getX(pointerId);
        } catch (IllegalArgumentException e) {
            // Ignored
        }
        return x;
    }

    private float getY(MotionEvent evt, int pointerId) {
        float y = -1;
        try {
            y = evt.getY(pointerId);
        } catch (IllegalArgumentException e) {
            // Ignored
        }
        return y;
    }

    private void register(MotionEvent evt) { 
        int actionInd = evt.getActionIndex();
        int pointerId= evt.getPointerId(actionInd);
        float pointX = getX(evt, pointerId);
        float pointY = getY(evt, pointerId);

        synchronized(inputLock) {
            // Find corresponding button - overlapping button/s is/are (an) error/s
            if (!pointerToButton.containsKey(pointerId))
                for (int i = 0; i < buttons.size(); ++i) {
                    ButtonRecord record = buttons.get(i);
                    if (record.area.contains((int)pointX, (int)pointY)) {
                        pointerToButton.put(pointerId, i);
                        record.activePointers.add(pointerId);
                        if (record.state != 2) {
                            record.state = 1;
                            record.downTick = ticks;
                            createEvent(i, 1);
                        }
                    }
                }
        }
    }

    private void update(MotionEvent evt) {
        int pointerCount = evt.getPointerCount();
        for (int i = 0; i < pointerCount; ++i) {
            float pointX = getX(evt, i);
            float pointY = getY(evt, i);
            int pointerId = evt.getPointerId(i);
            synchronized(inputLock) {
                if (pointerToButton.containsKey(pointerId)) {
                    ButtonRecord record = buttons.get(pointerToButton.get(pointerId));
                    if (!record.area.contains((int)pointX, (int)pointY)) {
                        removePointer(pointerId, record);
                    }
                }
            }
        }
    }

    private void removePointer(Integer pointerId, ButtonRecord record) {
        int foundId = record.activePointers.size();
        while ((--foundId + 1) >= 0) {
            if (record.activePointers.get(foundId) == pointerId) {
                record.activePointers.remove(foundId);
                foundId = -1;
            }
        }
        if (record.activePointers.size() == 0) {
            record.state = 0;
            createEvent(pointerToButton.get(pointerId), 0);
        }
        pointerToButton.remove(pointerId);
    }

    public boolean handleEvent(MotionEvent evt) {
        switch (evt.getActionMasked()) {
            case (MotionEvent.ACTION_DOWN): /* no other pointers should currently exist */
            case (MotionEvent.ACTION_POINTER_DOWN):
                register(evt);
                break;
            case (MotionEvent.ACTION_MOVE):
                update(evt);
                break;
            case (MotionEvent.ACTION_UP): /* should be the last pointer left on screen */
            case (MotionEvent.ACTION_POINTER_UP):
            case (MotionEvent.ACTION_CANCEL):
                synchronized(inputLock) {
                    int pointerId = evt.getPointerId(evt.getActionIndex());
                    if (pointerToButton.containsKey(pointerId)) {
                        removePointer(pointerId, buttons.get(pointerToButton.get(pointerId)));
                    }
                }
                break;
        }
        return false;
    }
}
