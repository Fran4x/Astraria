package com.mygdx.game.playback.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.mygdx.game.Boot;
import com.mygdx.game.playback.PlayBackBody;
import com.mygdx.game.playback.PlayBackScreen;

/**
 * Created by Fran on 1/14/2018.
 */
public class ProgressWindow extends VisWindow{

    VisSlider slider;
    PlayBackScreen playBackScreen;
    VisLabel elapsedTime;

    Boolean dragged = false, nowDragging = false;

    Vector2 indicatorPos;

    Texture indicatorTexture;

    public ProgressWindow(PlayBackScreen playBackScreen) {
        super("" );

        this.playBackScreen = playBackScreen;

        TableUtils.setSpacingDefaults(this);
        addWidgets();
        pack();

        indicatorTexture = Boot.manager.get("indicator.png", Texture.class);
    }

    private void addWidgets(){


        elapsedTime = new VisLabel("99999s");
        add(elapsedTime);

        final VisSlider mySlider = new VisSlider(0f, 1f, 0.01f, false);
        slider = mySlider;

        add(slider).width(300);
        final PlayBackScreen myPlayBackScreen = playBackScreen;
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(mySlider.isDragging()) {

                    nowDragging = true;

                    dragged = true;
                } else {
                    nowDragging = false;
                }
            }
        });

        padRight(3f);

        final VisTextButton pauseButton = new VisTextButton("Play");

        pauseButton.addListener(new ChangeListener() {
            private boolean lastState = true;
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if (lastState){
                    pauseButton.setText("Pause");
                }else {
                    pauseButton.setText("Play");
                }

                lastState=!lastState;
                myPlayBackScreen.setPaused(lastState);


            }
        });

        add(pauseButton).padRight(10);

        final VisTextButton doubleSpeed = new VisTextButton("2x");

        doubleSpeed.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                myPlayBackScreen.setTimeMultiplier(2);
            }
        });

        final VisTextButton normalSpeed = new VisTextButton("1x");

        normalSpeed.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                myPlayBackScreen.setTimeMultiplier(1);
            }
        });

        final VisTextButton halfSpeed = new VisTextButton("0.5x");

        halfSpeed.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                myPlayBackScreen.setTimeMultiplier(0.5f);
            }
        });

        add(halfSpeed);
        add(normalSpeed);
        add(doubleSpeed);

    }

    @Override
    public void act(float delta) {

        if(!nowDragging&&!dragged) {
            float newValue = (float) playBackScreen.getCurrFrame() / playBackScreen.getTotalFrames();
            if (newValue < 1 && !slider.isDragging()) {
                slider.setValue(newValue);
            }
        }

        if(dragged&&!nowDragging){
            playBackScreen.setCurrFrame((int)(playBackScreen.getTotalFrames() * slider.getValue()));

            dragged=false;
        }

        elapsedTime.setText((int)(playBackScreen.getCurrFrame()/60f)+"s");

        indicatorPos = calcIndicatorPos(playBackScreen.getPlayBackLoader().getLastFrame(),playBackScreen.getTotalFrames());

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(indicatorTexture,indicatorPos.x,indicatorPos.y,indicatorTexture.getWidth()*0.25f,
                indicatorTexture.getHeight()*0.25f);

    }

    private Vector2 calcIndicatorPos(int frameUpTo, int totalFrames){
        frameUpTo=0;
        System.out.println(totalFrames);
        float p = (float)frameUpTo/totalFrames;
        p = Math.max(p,0);
        p = Math.min(1,p);
        float x = p * slider.getWidth();

        float y = 0;

        y-=10;
        x-=8;
        return slider.localToStageCoordinates(new Vector2(x,y));

    }

}
