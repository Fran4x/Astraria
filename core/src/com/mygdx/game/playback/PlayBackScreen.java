package com.mygdx.game.playback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalMaterial;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.mygdx.game.BaseScreen;
import com.mygdx.game.Boot;
import com.mygdx.game.playback.ui.ProgressWindow;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Created by fraayala19 on 1/11/18.
 */
public class PlayBackScreen extends BaseScreen{

    private PerspectiveCamera cam;


    private ArrayList<ModelInstance> toRender;


    private FirstPersonCameraController camControl;

    private Model model;

    private ModelBatch modelBatch;

    private ArrayList<PlayBackBody> bodies;

    private int numberOfBodies;

    private float bodyScale, maxAccel, minAccel;


    private float currTime = 0;

    private int currFrame, totalFrames;

    private Stage uiStage;

    private InputListener UIListener;

    private Group uiGroup;

    private ProgressWindow progressWindow;

    private boolean paused;

    public PlayBackScreen(Boot boot, String arg) {
        super(boot);


        UIListener = new InputListener();

        uiStage = new Stage(new ScreenViewport());
        uiStage.addListener(UIListener);

        uiGroup = new Group();

        uiStage.addActor(uiGroup);

        ModelBuilder modelBuilder = new ModelBuilder();

        toRender = new ArrayList<ModelInstance>();

        final Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        final long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        model = modelBuilder.createSphere(1, 1, 1, 24, 24, material, attributes);

        bodies = new ArrayList<PlayBackBody>();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camControl = new FirstPersonCameraController(cam);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(camControl);

        Gdx.input.setInputProcessor(multiplexer);

        progressWindow = new ProgressWindow(this);

        setWindowPosition();

        loadRecording(arg);
    }

    private void setWindowPosition(){
        progressWindow.setX((Gdx.graphics.getWidth()/2)-210);
        progressWindow.setY(30);

    }

    @Override
    public void show() {


        cam.near = 0.001f;
        cam.far = 100f;
        cam.position.set(3,0,0);
        cam.lookAt(0,0,0);
        cam.update();

        modelBatch = new ModelBatch();

        uiGroup.addActor(progressWindow);

    }

    public int getCurrFrame() {
        return currFrame;
    }

    @Override
    public void render(float delta) {
        if(!paused) {
            currFrame = (int) (currTime * 60);

            currTime += delta;
        }

        setFrame(currFrame);

        uiStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        camControl.update(delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        cam.update();

        modelBatch.begin(cam);
        modelBatch.render(toRender);
        modelBatch.end();

        uiStage.draw();

    }

    @Override
    public void resize(int width, int height) {
        cam.viewportHeight = height;
        cam.viewportWidth = width;
        uiStage.getViewport().update(width,height, true);
        setWindowPosition();
    }

    @Override
    public void pause() {
        setPaused(true);
    }

    @Override
    public void resume() {
        setPaused(false);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        model.dispose();
        modelBatch.dispose();
    }

    private void setFrame(int frame){
        for(PlayBackBody body : bodies){
            body.setFrame(frame);
        }
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    private void loadRecording(String path){
        try {
            FileInputStream ifStream = new FileInputStream(path);
            ObjectInputStream stream = new ObjectInputStream(ifStream);

            short version = stream.readShort();

            if(version==1){
                numberOfBodies = stream.readInt();
                bodyScale = stream.readFloat();
                for(int i = 0; i < numberOfBodies; i++){
                    bodies.add(new PlayBackBody(model, bodyScale));
                    toRender.add(bodies.get(i).getModelInstance());
                }
                maxAccel = stream.readFloat();
                minAccel = stream.readFloat();
                while (ifStream.available()>0){
                    for(int i = 0; i < numberOfBodies; i++){
                        float x = stream.readFloat();
                        float y = stream.readFloat();
                        float z = stream.readFloat();
                        bodies.get(i).addPosition(new Vector3(x,y,z));
                        bodies.get(i).addAcceleration(stream.readFloat());
                    }
                }
            }

            stream.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        if(bodies.size()>0){
            totalFrames = bodies.get(0).getPositions().size();
        }
    }

    public void setCurrTime(float currTime) {
        this.currTime = currTime;
    }

    public void setCurrFrame(int currFrame) {
        this.currFrame = currFrame;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}