package com.mygdx.game.simulation.ui.windows;

import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.mygdx.game.simulation.SimulationScreen;
import com.mygdx.game.simulation.logic.algorithms.NBodyAlgorithm;
import com.mygdx.game.simulation.renderer.Camera;

/**
 * Created by Fran on 4/20/2018.
 */
public class SimInfoWindow extends VisWindow{
    private NBodyAlgorithm nBodyAlgorithm;
    private SimulationScreen simulationScreen;
    private VisLabel calcPerSecLabel;
    private VisLabel numObjectsLabel;
    private VisLabel cameraSpeedLabel;

    private Camera camera;

    public SimInfoWindow(SimulationScreen simulationScreen, Camera camera) {
        super("Simulation Information");
        this.nBodyAlgorithm = simulationScreen.getAlgorithm();
        this.simulationScreen = simulationScreen;
        this.camera = camera;
        TableUtils.setSpacingDefaults(this);
        addWidgets();
        pack();
    }

    private void addWidgets(){
        VisTable mainTable = new VisTable();

        calcPerSecLabel = new VisLabel();
        cameraSpeedLabel = new VisLabel();
        numObjectsLabel = new VisLabel();


        mainTable.add(calcPerSecLabel).width(300f);
        mainTable.row();
        mainTable.add(numObjectsLabel).width(300f);
        mainTable.row();
        mainTable.add(cameraSpeedLabel).width(300f);
        mainTable.row();

        add(mainTable);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        calcPerSecLabel.setText("Calculations per second: " + nBodyAlgorithm.getCalcSec());
        numObjectsLabel.setText("Number of objects: " + simulationScreen.getNumObjects());
        cameraSpeedLabel.setText("Camera speed(km/s): "+camera.getMovementSpeed());
    }
}
