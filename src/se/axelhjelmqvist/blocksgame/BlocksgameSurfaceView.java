package se.axelhjelmqvist.blocksgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * A class that is responsible for invoking the update and draw-methods of the game.
 * The game is drawn and updated using a special thread that is not the dedicated
 * main "ui"-thread. This is so the app stays fast and responsible at all times.
 * It handles the state of a SurfaceView using a SurfaceHolder object that supplies
 * a canvas for drawing the game.
 */
public class BlocksgameSurfaceView
        extends SurfaceView
        implements SurfaceHolder.Callback, Runnable {
    private Blocksgame blocksgame = null;

    private volatile boolean running = false;

    private Object threadLock;

    private Thread thread;

    private volatile boolean surfaceCreated;

    /**
     * Constructor of the surface view. This constructor is required to
     * be able to inflate the SurfaceView object.
     */
    public BlocksgameSurfaceView(Context context, AttributeSet attributes, int defstyle) {
        super(context, attributes);
        init();
    }

    /**
     * Constructor of the surface view. This constructor is required to
     * be able to inflate the SurfaceView object.
     */
    public BlocksgameSurfaceView(Context context, AttributeSet attributes) {
        super(context, attributes);
        init();
    }

    /**
     * Constructor of the surface view. This constructor is used to initialize
     * the game and is run before the SurfaceView is set as the application
     * content view. 
     */
    public BlocksgameSurfaceView(Context context) {
        super(context);
        init();
    }

    /**
     * Initalization code that is invoked from the game constructor.
     */
    private void init() {
        threadLock = new Object();
        thread = null;
        surfaceCreated = false;
        getHolder().addCallback(this);
    }

    /**
     * The run method of the Runnable interface. This is runned from a
     * thread that is started/stopped using the corresponding methods
     * on this class.
     */
    @Override
    public void run() {
        running = true;
        Canvas canvas;
        while (running) {
            if (surfaceCreated && blocksgame != null) {
                blocksgame.update();
                canvas = null;
                SurfaceHolder holder = getHolder();
                try {
                    synchronized(holder) {
                        while (canvas == null) {
                            canvas = holder.lockCanvas(null);
                        }
                        blocksgame.draw(canvas);
                    }
                } catch (Exception e) {
                    System.out.println("could not draw game");
                } finally {
                    holder.unlockCanvasAndPost(canvas);
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    // Ignored
                }
            }
        }
    }

    /**
     * System callback of the SurfaceHolder.Callback interface.
     * Invoked when the surface is created. Starts the update thread
     * that updates and draws the game.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceCreated = true;
        startThread();
    }

    /**
     * System callback of the SurfaceHolder.Callback interface.
     * Invoked when the surface is changed. Writes a log entry.
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        System.out.println("Surface changed; " + format + "; " + width + "; " + height);
    }

    /**
     * System callback of the SurfaceHolder.Callback interface.
     * Invoked when the surface is destroyed. Stops the update thread
     * that updates and draws the game.
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceCreated = false;
        stopThread();
    }

    /**
     * Start the thread that locks and uses the surfaceholder object.
     */
    public void startThread() {
        synchronized(threadLock) {
            if (thread == null) {
                thread = new Thread(this);
                thread.start();
            }
        }
    }

    /**
     * Stop the thread that locks and uses the surfaceholder object so that it
     * will not do those things anymore.
     */
    public void stopThread() {
        synchronized(threadLock) {
            if (thread == null) {
                return;
            }
            running = false;
            thread = null;
        }
    }

    /**
     * Callback of the view that is invoked when the layout is layed out.
     * This is used to determine the size of the game. The game cannot be
     * initialized before this is known.
     */
    @SuppressLint("ClickableViewAccessibility") @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        if (changed && blocksgame == null) {
            blocksgame = new Blocksgame(getContext(), right - left, bottom - top);
            blocksgame.loadResources(getContext());
            setOnTouchListener(blocksgame);
        }
    }
}