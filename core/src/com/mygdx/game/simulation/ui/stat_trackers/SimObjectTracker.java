package com.mygdx.game.simulation.ui.stat_trackers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.mygdx.game.simulation.SimulationObject;
import com.mygdx.game.simulation.ui.windows.StatsWindow;

/**
 * Created by fraayala19 on 4/19/18.
 */
public class SimObjectTracker {
    private final SimulationObject simulationObject;

    private final VisLabel nameLabel;
    private VisValidatableTextField nameField;
    private VisTextButton nameSetButton;
    private SimpleFormValidator nameValidator;


    public SimObjectTracker(final SimulationObject simulationObject) {
        this.simulationObject = simulationObject;



        nameLabel = new VisLabel("Name: "+simulationObject.getName());
        nameField = new VisValidatableTextField();
        nameSetButton = new VisTextButton("Set");
        nameValidator = new SimpleFormValidator(nameSetButton);
        nameValidator.notEmpty(nameField,"Name empty");
        nameSetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                simulationObject.setName(nameField.getText());
            }
        });
    }


    public void addToTable(VisTable table){
        table.add(nameLabel).padRight(3f);
        table.add(nameField).expand().fill().padRight(3f);
        table.add(nameSetButton).expand().fill();
        table.row();
    }

    public void update(){
        nameLabel.setText("Name: "+simulationObject.getName());
    }
}
