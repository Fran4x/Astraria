package com.mygdx.game.logic.algorithms.threads;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 12/13/17   ==
* =====================================================================
* ==      Project: orbit-simulator-2    ==
* =====================================================================

*/

import com.mygdx.game.logic.Body;
import com.mygdx.game.logic.algorithms.MultiThreadAlgorithm;
import com.mygdx.game.logic.algorithms.helpers.Units;

import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

public class VelocityVerlet implements Runnable {
    private CountDownLatch latch;
    private int thisBody;
    private double temporary, ax, ay, az;
    private MultiThreadAlgorithm multiThreadParent;

    public VelocityVerlet(CountDownLatch latch, MultiThreadAlgorithm multiThreadParent, int thisBody) {
        this.latch = latch;
        this.thisBody = thisBody;
        this.multiThreadParent = multiThreadParent;
    }

    @Override
    public void run() {
        Body current = multiThreadParent.getBodies().elementAt(thisBody);

        //Verlet Algorithm

        double delta = 0; //provisional delta

        //Get time
            ax = 0;
            ay = 0;
            az = 0;

            double pX = current.getX();
            double pY = current.getY();
            double pZ = current.getZ(); //Units


            Vector<Body> bodies = multiThreadParent.getBodies();


            //First time procedure?
            if (!current.getAccelInit()) {


                //No indexing for safe list
                //Find other way of initializing accelerations

                for (int l = 0; l < bodies.indexOf(current); l++) {
                    parseAcceleration(bodies.get(l), pX, pY, pZ);
                }
                for (int l = bodies.indexOf(current) + 1; l < bodies.size(); l++) {
                    parseAcceleration(bodies.get(l), pX, pY, pZ);
                }


                    ax *= Units.GRAVCONSTANT;
                    ay *= Units.GRAVCONSTANT;
                    az *= Units.GRAVCONSTANT;

                    multiThreadParent.getAx()[thisBody] = ax;
                    multiThreadParent.getAy()[thisBody] = ay;
                    multiThreadParent.getAz()[thisBody] = az;

                    current.setAccelInitTrue();

            }

            pX = pX + (current.getX() * delta) + (0.5*current.getCurrAccelX() *square(delta));
            pY = pY + (current.getY() * delta) + (0.5*current.getCurrAccelY() *square(delta));
            pZ = pZ + (current.getZ() * delta) + (0.5*current.getCurrAccelZ() *square(delta));

            multiThreadParent.getX()[thisBody] = pX;
            multiThreadParent.getY()[thisBody] = pY;
            multiThreadParent.getZ()[thisBody] = pZ;

            //new positions saved into arrays, bodies still hold old positions

            ax = 0;
            ay = 0;
            az = 0;

            for (int k = 0; k < bodies.indexOf(current); k++) {
                parseAcceleration(bodies.get(k), pX, pY, pZ);
            }
            for (int k = bodies.indexOf(current) + 1; k < bodies.size(); k++) {
                parseAcceleration(bodies.get(k), pX, pY, pZ);
            }
            ax *= Units.GRAVCONSTANT;
            ay *= Units.GRAVCONSTANT;
            az *= Units.GRAVCONSTANT;

            multiThreadParent.getVx()[thisBody] += (((multiThreadParent.getAx()[thisBody] + ax)/2)*delta);
            multiThreadParent.getVx()[thisBody] += (((multiThreadParent.getAx()[thisBody] + ax)/2)*delta);
            multiThreadParent.getVx()[thisBody] += (((multiThreadParent.getAx()[thisBody] + ax)/2)*delta);


            multiThreadParent.getAx()[thisBody] = ax;
            multiThreadParent.getAy()[thisBody] = ay;
            multiThreadParent.getAz()[thisBody] = az;


        latch.countDown();
    }

    private void parseAcceleration(Body other, double pX, double pY, double pZ) {
        double oX = other.getX();
        double oY = other.getY();
        double oZ = other.getZ();

        temporary = other.getMass() / cubed(Math.sqrt(square(oX - pX) + square(oY - pY) + square(oZ - pZ)));


        ax += (oX - pX) * temporary;
        ay += (oY - pY) * temporary;
        az += (oZ - pZ) * temporary;
    }

    private double cubed(double number) {
        return number * number * number;
    }

    private double square(double number) {
        return number * number;
    }


}
