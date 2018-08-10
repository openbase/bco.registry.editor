package org.openbase.bco.registry.editor.visual;

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

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LoginPanel extends HBox {

    private final Label errorLabel, userNameLabel;
    private final TextField userNameTextField;
    private final PasswordField passwordField;
    private final Button loginButton, logoutButton;

    public LoginPanel() {
        this.setAlignment(Pos.CENTER_RIGHT);

        this.errorLabel = new Label("");
        this.errorLabel.setStyle("-fx-text-background-color: rgb(255,0,0); -fx-font-weight: bold;");

        this.userNameLabel = new Label("");

        this.userNameTextField = new TextField("username");
        this.userNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                clearError();
                userNameTextField.selectAll();
            }
        });
        this.userNameTextField.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ENTER:
                    login();
            }
        });

        this.passwordField = new PasswordField();
        this.passwordField.setPromptText("password");
        this.passwordField.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                clearError();
                passwordField.selectAll();
            }
        }));
        this.passwordField.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ENTER:
                    login();
            }
        });

        this.loginButton = new Button("Login");
        this.loginButton.setOnAction(event -> login());

        this.logoutButton = new Button("Logout");
        this.logoutButton.setOnAction(event -> logout());

        this.setSpacing(5);
        this.getChildren().addAll(errorLabel, userNameTextField, passwordField, loginButton);
    }

    private void login() {
        try {
            clearError();
            final String userId = Registries.getUnitRegistry().getUserUnitIdByUserName(userNameTextField.getText());
            SessionManager.getInstance().login(userId, passwordField.getText());

            passwordField.clear();
            userNameTextField.clear();

            userNameLabel.setText(Registries.getUnitRegistry().getUnitConfigById(userId).getUserConfig().getUserName());
            this.getChildren().clear();
            this.setSpacing(20);
            this.getChildren().addAll(userNameLabel, logoutButton);
        } catch (CouldNotPerformException ex) {
            errorLabel.setText("Login failed: " + ex.getMessage());
            ExceptionPrinter.printHistory(ex, LoggerFactory.getLogger(LoginPanel.class));
        }
    }

    private void clearError() {
        errorLabel.setText("");
    }

    private void logout() {
        SessionManager.getInstance().logout();
        userNameLabel.setText("");

        this.getChildren().clear();
        this.setSpacing(5);
        this.getChildren().addAll(errorLabel, userNameTextField, passwordField, loginButton);
    }
}
