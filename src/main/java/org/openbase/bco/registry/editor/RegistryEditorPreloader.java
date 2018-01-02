package org.openbase.bco.registry.editor;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openbase.jul.schedule.GlobalCachedExecutorService;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class RegistryEditorPreloader extends Preloader {

    private Stage preloaderStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;
        preloaderStage.initStyle(StageStyle.TRANSPARENT);
        final ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxWidth(100);
        progressIndicator.setMaxHeight(100);
        progressIndicator.setBackground(Background.EMPTY);
        final Scene scene = new Scene(progressIndicator);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setWidth(100);
        primaryStage.setHeight(100);
        primaryStage.centerOnScreen();
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);
        Rectangle2D primScreenBounds = Screen.getPrimary().getBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
        primaryStage.show();
        
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == Type.BEFORE_START) {
            GlobalCachedExecutorService.submit(() -> {
                Platform.runLater(() -> {
                    preloaderStage.setAlwaysOnTop(false);
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    // hide in any case
                }
                Platform.runLater(() -> {
                    preloaderStage.hide();
                });
            });
        }
    }
}
