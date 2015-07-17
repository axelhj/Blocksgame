package se.axelhjelmqvist.blocksgame;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BlocksgameSurfaceView
        extends SurfaceView
        implements SurfaceHolder.Callback, Runnable {
    private Blocksgame blocksgame = null;

    private volatile boolean running = false;

    private Object threadLock;

    private Thread thread;

    private volatile boolean surfaceCreated;

    public BlocksgameSurfaceView(Context context) {
        super(context);
        threadLock = new Object();
        thread = null;
        surfaceCreated = false;
        getHolder().addCallback(this);
    }

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

    public void surfaceCreated(SurfaceHolder holder) {
        surfaceCreated = true;
        startThread();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        System.out.println("Surface changed; " + format + "; " + width + "; " + height);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceCreated = false;
        stopThread();
    }
    public void startThread() {
        synchronized(threadLock) {
            if (thread == null) {
                thread = new Thread(this);
                thread.start();
            }
        }
    }

    public void stopThread() {
        synchronized(threadLock) {
            if (thread == null) {
                return;
            }
            running = false;
            thread = null;
        }
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        if (changed && blocksgame == null) {
            blocksgame = new Blocksgame(getContext(), right - left, bottom - top);
            blocksgame.loadResources(getContext());
            setOnTouchListener(blocksgame);
        }
    }
}