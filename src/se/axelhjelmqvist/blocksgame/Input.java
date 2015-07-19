package se.axelhjelmqvist.blocksgame;

import android.view.MotionEvent;
import java.util.TreeMap;
import java.util.ArrayList;
import android.graphics.Rect;

/**
 * Class that receives and tracks MotionEvents in relation to rectangles.
 * It invokes callbacks to indicate if a logical button is depressed or
 * not. Handles the input of the game.
 */
public class Input {
    /**
     * Private class that tracks the state and properties (such as pointers)
     * of a logical input button on the touchscreen.
     */
    private class ButtonRecord {
        public Rect area;

        public ArrayList<Integer> activePointers;

        public int state;

        public long downTick;

        /**
         * Constructor that initializes the ButtonRecord values.
         */
        public ButtonRecord() {
            activePointers = new ArrayList<Integer>();
        }
    }

    /**
     * Public nested class of the Input class. This represents
     * an object that can be queried to know of the current
     * state of a logical button. This is how the "outside world" knows
     * of the current state of the Input on the screen. This could
     * be passed through an interface callback method for a more asynchronous
     * style of touch event tracking.
     */
    public class EventData {
        public int button, state;

        public volatile boolean consumed;

        /**
         * Constructor of the EventData class. Initializes
         * the state according to the arguments.
         */
        public EventData(int id, int state) {
            set(id, state);
        }
        
        /**
         * Convenience method that is used to set the state of the
         * EventData instance. This is handy because the objects
         * are retained and reused to limit GC-freezes of the application.
         */
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

    /**
     * Constructor of the (touch-) Input handler class. This initializes
     * the fields of the class to default values and creates the useful
     * datastructures that are retained as class members.
     */
    public Input() {
        inputLock = new Object();
        pointerToButton = new TreeMap<Integer, Integer>();
        buttons = new ArrayList<ButtonRecord>();
        buttonEvents = new ArrayList<EventData>();
        events = new ArrayList<EventData>();
        lastEvent = null;
        ticks = 0;
	}

    /**
     * Adds a logical button that is tracked by the Input handler class. The
     * button is represented as a logical rectangle. The button can be tracked
     * from the outside using the returned integer id.
     */
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

	/**
	 * Method that is used to track the state of a certain button by
	 * continuously polling for the button state. The state can be
	 * just depressed, continuously depressed or not depressed at all.
	 * It can also be not known, represented as -1. 
	 */
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

    /**
     * Method that is used to pop a new event out of the event queue.
     * If no events are available, null will be returned.
     * @return
     */
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

    /**
     * Updates the Input state by each update of the game.
     * This is required so that a button press can be distinguished
     * from a continuously pressed button.
     */
    public void updateState() {
        synchronized (inputLock) {
            ++ticks;
        }
    }

    /**
     * Creates an updated button event and puts it on the stack.
     * Handles logic to reuse old EventData object instances.
     * If a lot of events happens in a sufficiently short amount of
     * time, the Input handler class can hog a lot of memory. This
     * is tolerated because the alternative is either introducing
     * more advanced logic to trim the reusable event queue or
     * instantiating a new object for each and every touch input.
     * The latter case would likely introduce a significant amount of
     * stutter (of a duration on the order of several tens of milliseconds)
     * due to garbage collection freezes.
     */
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

    /**
     * Register a new pointer on the touchscreen. If the new pointer position
     * is contained within a logical button, further events such as pointer
     * movements and removing of the pointer from the touch surface will not be
     * considered.
     */
    private void register(MotionEvent event) { 
        int actionIndex = event.getActionIndex();
        int pointerId = event.getPointerId(actionIndex);
        float pointX = event.getX(actionIndex);
        float pointY = event.getY(actionIndex);
        synchronized(inputLock) {
            // Find corresponding button - overlapping button/s is/are (an) error/s
            if (!pointerToButton.containsKey(pointerId)) {
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
    }

    /**
     * Helper method that is used to update the tracked state of a pointer
     * on the screen. If a pointer doesn't stay within the logical button
     * rectangle, the pointer is no longer tracked (it is removed). Further
     * motion events for that pointer will not be tracked.
     */
    private void update(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; ++i) {
            float pointX = event.getX(i);
            float pointY = event.getY(i);
            int pointerId = event.getPointerId(i);
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

    /**
     * Helper method to remove a logical touch pointer that should
     * no longer be tracked (because a finger has been lifted...)
     */
    private void removePointer(Integer pointerId, ButtonRecord record) {
        int foundId = record.activePointers.size();
        while (--foundId + 1 >= 0) {
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

    /**
     * Method that is used to handle a new MotionEvent touch
     * input events. This class only handles the logic of such events,
     * and is not interested from where these events may come,
     * so therefore does not setup this itself (this class
     * is not tightly coupled to the logic of the game itself).
     */
    public boolean handleEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case (MotionEvent.ACTION_DOWN): /* no other pointers should currently exist */
            case (MotionEvent.ACTION_POINTER_DOWN):
                register(event);
                break;
            case (MotionEvent.ACTION_MOVE):
                update(event);
                break;
            case (MotionEvent.ACTION_UP): /* should be the last pointer left on screen */
            case (MotionEvent.ACTION_POINTER_UP):
            case (MotionEvent.ACTION_CANCEL):
                synchronized(inputLock) {
                    int pointerId = event.getPointerId(event.getActionIndex());
                    if (pointerToButton.containsKey(pointerId)) {
                        removePointer(pointerId, buttons.get(pointerToButton.get(pointerId)));
                    }
                }
                break;
        }
        return false;
    }
}
