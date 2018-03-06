package simulation;

import com.sun.prism.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.scene.shape.Shape;
import physics.*;

public class Simulation {
    private Box outer;
    private Ball ball;
    private Box inner_1;
    private Box inner_2;
    private Lock lock;
    private int score1;
    private int score2;
    private Box score_1;
    private Box score_2;
    
    public Simulation(int width,int height,int dX,int dY)
    {
        outer = new Box(0,0,width,height,false);
        ball = new Ball(width/2,height/2,dX,dY);
        inner_1 = new Box(width - 60, height - 40, 40, 20,true);
        inner_1.setColor();
        score_2.setColor();
        inner_2 = new Box(width - 60, height - 40, 80, 40, true);
        lock = new ReentrantLock();
        score_1 = new Box(width/2 - 60, 0, 120, 40, true);
        score_2 = new Box(width/2 - 60, 0, 120, 40, true);       
    }
    
    public void evolve(double time)
    {
        lock.lock();
        Ray newLoc = inner_1.bounceRay(ball.getRay(), time);
        if(newLoc != null)
            ball.setRay(newLoc);
        else {
            newLoc = outer.bounceRay(ball.getRay(), time);
            if(newLoc != null)
                ball.setRay(newLoc);
            else
                ball.move(time);
        } 
        lock.unlock();
    }
    
    public void moveInner(int deltaX,int deltaY)
    {
        lock.lock();
        int dX = deltaX;
        int dY = deltaY;
        if(inner_1.x + deltaX < 0)
          dX = -inner_1.x;
        if(inner_1.x + inner_1.width + deltaX > outer.width)
          dX = outer.width - inner_1.width - inner_1.x;
       
        if(inner_1.y + deltaY < 0)
           dY = -inner_1.y;
        if(inner_1.y + inner_1.height + deltaY > outer.height)
           dY = outer.height - inner_1.height - inner_1.y;
        
        inner_1.move(dX,dY);
        if(inner_1.contains(ball.getRay().origin)) {
            // If we have discovered that the box has just jumped on top of
            // the ball, we nudge them apart until the box no longer
            // contains the ball.
            int bumpX = -1;
            if(dX < 0) bumpX = 1;
            int bumpY = -1;
            if(dY < 0) bumpY = 1;
            do {
            inner_1.move(bumpX, bumpY);
            ball.getRay().origin.x += -bumpX;
            ball.getRay().origin.y += -bumpY;
            } while(inner_1.contains(ball.getRay().origin));
        }
        lock.unlock();
    }
    
    public void moveInner_2(int deltaX, int deltaY)
    {
        lock.lock();
        int dX = deltaX;
        int dY = deltaY;
        if(inner_2.x + deltaX < 0)
          dX = -inner_2.x;
        if(inner_2.x + inner_2.width + deltaX > outer.width)
          dX = outer.width - inner_2.width - inner_2.x;
       
        if(inner_2.y + deltaY < 0)
           dY = -inner_2.y;
        if(inner_2.y + inner_2.height + deltaY > outer.height)
           dY = outer.height - inner_2.height - inner_2.y;
        
        inner_2.move(dX,dY);
        if(inner_2.contains(ball.getRay().origin)) {
            // If we have discovered that the box has just jumped on top of
            // the ball, we nudge them apart until the box no longer
            // contains the ball.
            int bumpX = -1;
            if(dX < 0) bumpX = 1;
            int bumpY = -1;
            if(dY < 0) bumpY = 1;
            do {
            inner_2.move(bumpX, bumpY);
            ball.getRay().origin.x += -bumpX;
            ball.getRay().origin.y += -bumpY;
            } while(inner_2.contains(ball.getRay().origin));
        }
        lock.unlock();
    }        
    
    public List<Shape> setUpShapes()
    {
        ArrayList<Shape> newShapes = new ArrayList<Shape>();
        newShapes.add(outer.getShape());
        newShapes.add(score_1.getShape());
        newShapes.add(score_2.getShape());
        newShapes.add(inner_1.getShape());
        newShapes.add(inner_2.getShape());
        newShapes.add(ball.getShape());
        return newShapes;
        
    }
    
    public void updateShapes()
    {
        inner_1.updateShape();
        inner_2.updateShape();
        ball.updateShape();
    }
    
    public void getScore(double time){
        if(score_1.Intersection(ball.getRay(), time) == true){
            score1++;
        }else if(score_2.Intersection(ball.getRay(),time)==false){
            score2++;
        }
    }
    
}

