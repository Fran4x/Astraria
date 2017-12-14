package com.mygdx.game.logic.algorithms;

import com.mygdx.game.logic.Body;

import java.util.Vector;

/**
 * Created by fraayala19 on 12/12/17.
 */
public abstract class NBodyAlgorithm implements Runnable{

    protected  Vector<Body> bodies;
    private boolean terminate = false;

    public NBodyAlgorithm(Vector<Body> bodies) {
        this.bodies = bodies;
    }

    @Override
    public void run() {
        if(!terminate){
            runAlgorithm();
        } else {
            endThreads();
        }
    }

    protected abstract void runAlgorithm();

    private void terminate(){
        terminate = true;
    }

    protected abstract void endThreads();
}
