package org.openbase.bco.registry.editor.visual;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2020 openbase.org
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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.jul.schedule.GlobalCachedExecutorService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Search bar managing the search through a tree table view with {@link GenericTreeItem} nodes.
 * Since a search can take quite long because the tree structure has to be completely generated the first time
 * it will be done in another thread. Furthermore it will only start once enter is pressed.
 * After the search has finished the first tree item found will be selected and scrolled to.
 * Pressing enter again will jump to the next tree item found. If the last result is reached it will be jumped
 * back to the first result.
 * TODO: display how many results where found and allow directly jumping to them.
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class SearchBar extends HBox {

    private final TextField searchTextField;
    private final TreeTableView<ValueType> treeTableView;
    private final ProgressIndicator progressIndicator;


    private Future searchTask;
    private List<TreeItem<ValueType>> searchResult = new ArrayList<>();
    private String lastSearch = "";
    private int index = -1;

    /**
     * Create a new search bar searching through a tree table view.
     *
     * @param treeTableView the tree table view searched.
     */
    public SearchBar(final TreeTableView<ValueType> treeTableView) {
        this.treeTableView = treeTableView;

        // init visual components
        this.searchTextField = new TextField();
        this.searchTextField.setPromptText("search");
        this.progressIndicator = new ProgressIndicator();
        this.progressIndicator.setMaxHeight(24);
        this.setSpacing(5);
        this.getChildren().addAll(searchTextField);

        this.searchTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(searchTextField::selectAll);
            }
        });
        // handle enter key released
        this.searchTextField.setOnKeyReleased(event -> {
            // always reset color to black
            searchTextField.setStyle("-fx-text-inner-color: black;");
            // make ctrl + f focus the search text field
            if (event.getCode() == KeyCode.F && event.isControlDown()) {
                Platform.runLater(() -> searchTextField.requestFocus());
            }

            // if something else than enter is pressed do nothing
            if (event.getCode() != KeyCode.ENTER) {
                return;
            }

            if (searchTask == null || !searchTask.isDone() || !lastSearch.equals(searchTextField.getText())) {
                // if the search task is still running or the search text has changed start a new search

                // set the progress indicator while the search is running
                getChildren().add(0, progressIndicator);
                // if still running cancel the current task
                if (searchTask != null && !searchTask.isDone()) {
                    searchTask.cancel(true);
                }

                //TODO: enable and fix, somehow this causes an error in the tree table view
                if (!searchResult.isEmpty()) {
                    expandToAndSelectTreeItem(false, searchResult.get(index));
                }
                // reset search values
                lastSearch = searchTextField.getText();
                index = 0;
                searchResult.clear();
                // start a new search task
                searchTask = GlobalCachedExecutorService.submit(() -> {
                    // the search
                    ((GenericTreeItem) treeTableView.getRoot()).search(searchTextField.getText(), searchResult);
                    // update visual component according to result
                    Platform.runLater(() -> {
                        // remove progress indicator
                        getChildren().remove(progressIndicator);
                        if (!searchResult.isEmpty()) {
                            // if something is found expand to and select the first result
                            expandToAndSelectTreeItem(true, searchResult.get(index));
                        } else {
                            // nothing found so make the text red
                            searchTextField.setStyle("-fx-text-inner-color: red");
                        }
                    });
                });
            } else {
                if (searchResult.isEmpty()) {
                    // search text has not changed and nothing was found so make text red again
                    searchTextField.setStyle("-fx-text-inner-color: red");
                    return;
                }

                // collapse the last found item
                expandToAndSelectTreeItem(false, searchResult.get(index));
                // increase and wrap the index
                index++;
                if (index == searchResult.size()) {
                    index = 0;
                }
                // expand to and select the next result
                expandToAndSelectTreeItem(true, searchResult.get(index));
            }
        });
    }

    /**
     * Expand the tree recursively from a tree item to the root node. This guarantees that it will be visible.
     * Additionally select and scroll to the row the tree item is displayed in.
     *
     * @param expandAndSelect if true the tree will be expanded and the tree item selected, if false the tree will be
     *                        collapsed.
     * @param treeItem        the tree item from which the tree will be expanded or collapsed. If the tree is expanded it will
     *                        also be selected and scrolled to.
     */
    private void expandToAndSelectTreeItem(final boolean expandAndSelect, final TreeItem<ValueType> treeItem) {
        // get the parent of the tree item
        TreeItem parent = treeItem.getParent();
        // iterate as long as a parent exists
        while (parent != null) {
            // expand the parent
            parent.setExpanded(expandAndSelect);
            // get the parent of the current parent
            parent = parent.getParent();
        }
        if (expandAndSelect) {
            // get the row of the found tree item
            int row = treeTableView.getRow(treeItem);
            // select the row which will highlight it
            treeTableView.getSelectionModel().select(row);
            // scroll to the row so that it is on screen
            treeTableView.scrollTo(row);
        }
    }

    /**
     * Get the search text field.
     *
     * @return the search text field.
     */
    public TextField getSearchTextField() {
        return searchTextField;
    }
}
