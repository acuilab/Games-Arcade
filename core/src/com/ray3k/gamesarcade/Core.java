package com.ray3k.gamesarcade;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.io.File;
import java.io.FilenameFilter;

public class Core extends ApplicationAdapter {
    private Skin skin;
    private Stage stage;
    private TextureAtlas atlas;
    
    @Override
    public void create() {
        PixmapPacker pixmapPacker = new PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 5, true, new PixmapPacker.GuillotineStrategy());
        
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jar");
            }
        };
        FileHandle gameDirectory = Gdx.files.local("games_arcade_data/games/");
        Array<FileHandle> games = new Array<FileHandle>(gameDirectory.list(filter));
        
        for (FileHandle game : games) {
            FileHandle thumbFile = game.sibling(game.nameWithoutExtension() + "_thumb.jpg");
            if (thumbFile.exists()) {
                pixmapPacker.pack(game.nameWithoutExtension(), new Pixmap(thumbFile));
            }
        }
        atlas = pixmapPacker.generateTextureAtlas(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear, false);
        pixmapPacker.dispose();
        
        skin = new Skin(Gdx.files.local("games_arcade_data/ui/games-arcade.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        Label label = new Label("Games Arcade", skin, "title");
        root.add(label).padTop(15.0f).space(5.0f);
        
        root.row();
        Table table = new Table(skin);
        table.setBackground("table-bg");
        root.add(table).grow().pad(10.0f);
        
        HorizontalGroup hGroup = new HorizontalGroup();
        hGroup.center().top();
        hGroup.wrap(true);
        hGroup.space(5.0f);
        hGroup.wrapSpace(5.0f);
        hGroup.rowAlign(Align.left);
        ScrollPane scrollPane = new ScrollPane(hGroup, skin);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);
        table.add(scrollPane).grow();
        stage.setScrollFocus(scrollPane);
        
        for (FileHandle game : games) {
            ImageButton imageButton = new ImageButton(new ImageButton.ImageButtonStyle(skin.get(ImageButton.ImageButtonStyle.class)));
            TextureRegion region = atlas.findRegion(game.nameWithoutExtension());
            System.out.println(region);
            if (region != null) {
                imageButton.getStyle().imageUp = new TextureRegionDrawable(region);
            }
            hGroup.addActor(imageButton);
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(171.0f / 255.0f, 87.0f / 255.0f, 255.0f / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act();
        stage.draw();
        
        if (Gdx.input.isKeyJustPressed(Keys.F5)) {
            dispose();
            create();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
    }
}
