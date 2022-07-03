package hk.polyu.eie.eie3109;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

import hk.polyu.eie.eie3109.animationassignment2020.R;


public class Panel extends SurfaceView implements SurfaceHolder.Callback{
    private Bitmap bmp;
    private GameThread thread;

    private ArrayList<GraphicObject> graphics = new ArrayList<GraphicObject>();

    public Panel(Context context){
        super(context);
        getHolder().addCallback(this);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        thread = new GameThread(getHolder(),this);
        setFocusable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Coordinates coords;
        int x, y;
        canvas.drawColor(Color.BLACK);
        for(GraphicObject graphic: graphics) {
            coords = graphic.getCoordinates();
            x = coords.getX();
            y = coords.getY();
            canvas.drawBitmap(bmp, x, y, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        synchronized (thread.getSurfaceHolder()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                boolean check = true;
                int x = (int) event.getX();
                int y = (int) event.getY();
                Coordinates coords;
                int Pos = 0;
                for(GraphicObject graphic: graphics) {
                    coords = graphic.getCoordinates();
                    int Gx = coords.getX();
                    int Gy = coords.getY();
                    int Xlowerlimit = Gx;
                    int Xupperlimit = Gx+bmp.getWidth();
                    int Yupperlimit = Gy+bmp.getHeight();
                    int YLowerlimit  = Gy;
                    if(x>=Xlowerlimit && x<=Xupperlimit && y<=Yupperlimit && y>=YLowerlimit){
                        graphics.remove(Pos);
                        check = false;
                        break;
                    }
                    Pos++;
                }

                if(check == true) {
                    GraphicObject graphic = new GraphicObject(bmp);
                    int bmpW = graphic.getGraphic().getWidth();
                    int bmpH = graphic.getGraphic().getHeight();
                    graphic.getCoordinates().setX(x - bmpW / 2);
                    graphic.getCoordinates().setY(y - bmpH / 2);
                    Random r = new Random();
                    graphic.getMovement().setXYSpeed(r.nextInt(10 - 1) + 1, r.nextInt(10 - 1) + 1);
                    graphics.add(graphic);
                }
            }

            return true;
        }
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
    thread.setRunning(true);
    thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
    boolean retry = true;
    thread.setRunning(false);
    while(retry){
        try{
            thread.join();
            retry = false;
        }catch (InterruptedException e){
            //keep trying here

        }
    }
    }

    public void updateMovement() {
        Coordinates coord;
        Movement movement;

        int x, y;

        for(GraphicObject graphic: graphics) {
            coord = graphic.getCoordinates();
            movement = graphic.getMovement();

            x = (movement.getXDirection()==Movement.X_DIRECTION_RIGHT)?coord.getX()+
                    movement.getXSpeed():coord.getX()-movement.getXSpeed();
            //check x if reaches border
            if(x < 0) {
                movement.toggleXDirection();
                coord.setX(-x);
            } else if(x + graphic.getGraphic().getWidth() > getWidth()) {
                movement.toggleXDirection();
                coord.setX(x + getWidth() - (x+graphic.getGraphic().getWidth()));
            } else {
                coord.setX(x);
            }

            y = (movement.getYDirection()==Movement.Y_DIRECTION_DOWN)?coord.getY()+
                    movement.getYSpeed():coord.getY()-movement.getYSpeed();
            //check y if reaches border
            if(y < 0) {
                movement.toggleYDirection();
                coord.setY(-y);
            } else if(y + graphic.getGraphic().getHeight() > getHeight()) {
                movement.toggleYDirection();
                coord.setY(y + getHeight() - (y+graphic.getGraphic().getHeight()));
            } else {
                coord.setY(y);
            }
        }
    }




}
