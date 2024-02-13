package org.openbase.bco.registry.editor.struct

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.HBox
import org.openbase.bco.registry.editor.visual.RequiredFieldAlert
import org.openbase.bco.registry.remote.Registries
import org.openbase.jul.exception.CouldNotPerformException
import org.openbase.jul.exception.ExceptionProcessor.getInitialCause
import org.openbase.jul.exception.InitializationException
import org.openbase.jul.exception.NotAvailableException
import org.openbase.jul.exception.printer.ExceptionPrinter
import org.openbase.jul.exception.printer.LogLevel
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor
import org.openbase.jul.extension.type.processing.LabelProcessor
import org.openbase.jul.iface.Identifiable
import org.openbase.jul.schedule.GlobalCachedExecutorService
import org.openbase.type.language.LabelType
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import kotlin.collections.HashSet

/**
 * @author [Tamino Huxohl](mailto:pleminoq@openbase.org)
 */
open class RegistryMessageTreeItem<MB : Message.Builder>(
    fieldDescriptor: Descriptors.FieldDescriptor,
    builder: MB,
    editable: Boolean?,
    labelProvider: (() -> String)? = null
) : BuilderTreeItem<MB>(fieldDescriptor, builder, editable) {
    private var idField: Descriptors.FieldDescriptor? = null //, labelField;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2024 openbase.org
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
    private val labelProvider: () -> String = labelProvider ?: {
        try {
            ProtoBufFieldProcessor.getFieldDescriptor(
                builder,
                LabelType.Label::class.java.getSimpleName().lowercase()
            ).let { defaultLabelField ->
                LabelProcessor.getBestMatch(
                    getBuilder().getField(defaultLabelField) as LabelType.Label,
                    super.createDescriptionText()
                )
                    ?: super.createDescriptionText()
            }
        } catch (e: NotAvailableException) {
            super.createDescriptionText()
        }
    }

    private var inUpdate = false
    var isChanged: Boolean = false
    private var registryTask: Future<Message>? = null

    constructor(
        fieldDescriptor: Descriptors.FieldDescriptor,
        builder: MB,
        editable: Boolean?
    ) : this(fieldDescriptor, builder, editable, null)

    init {
        try {
            idField = ProtoBufFieldProcessor.getFieldDescriptor(builder, Identifiable.TYPE_FIELD_ID)
            isChanged = false
            inUpdate = false

            this.addEventHandler(
                valueChangedEvent(),
                EventHandler<TreeModificationEvent<Any?>> { event: TreeModificationEvent<Any?> ->
                    // this is triggered when the value of this node or one of its children changes
                    updateDescriptionGraphic()

                    if (!inUpdate && event.source != this@RegistryMessageTreeItem) {
                        logger.trace("Set changed")
                        isChanged = true
                    }
                    updateValueGraphic()
                })
        } catch (ex: CouldNotPerformException) {
            throw InitializationException(this, ex)
        }
    }

    val id: String
        get() = builder!!.getField(idField) as String

    /**
     * Match a builder by comparing their ids.
     *
     * @param builder {@inheritDoc}
     * @return {@inheritDoc}
     */
    override fun matchesBuilder(builder: MB): Boolean {
        val id1 = getBuilder()!!.getField(idField)
        val id2 = builder!!.getField(idField)
        return id1 == id2
    }

    override fun getUneditableFields(): Set<Int> {
        val uneditableFieldSet: MutableSet<Int> = HashSet()
        uneditableFieldSet.add(idField!!.number)
        return uneditableFieldSet
    }

    override fun createDescriptionText(): String = labelProvider()

    override fun createValueGraphic(): Node? {
        // task is not done yet so display loading graphic
        if (registryTask != null && !registryTask!!.isDone) {
            val label = Label("Waiting for registry update...")
            val progressIndicator = ProgressIndicator()
            progressIndicator.maxHeight = 16.0

            val hBox = HBox()
            hBox.spacing = 5.0
            hBox.children.addAll(progressIndicator, label)
            return hBox
        }

        val errorLabel = Label()
        // task is done but not reset meaning it failed, so generate an error label
        if (registryTask != null) {
            errorLabel.style = "-fx-text-background-color: rgb(255,0,0); -fx-font-weight: bold;"
            // done but failed
            try {
                registryTask!!.get()
            } catch (ex: InterruptedException) {
                // this should not because the task should already be done
            } catch (ex: ExecutionException) {
                errorLabel.text = getInitialCause(ex).message
            }
        }

        if (isChanged) {
            logger.trace("Create buttons")
            val buttonLayout: HBox
            val applyButton = Button("Apply")
            applyButton.onAction = EventHandler { event: ActionEvent? -> handleApplyEvent() }
            val cancelButton = Button("Cancel")
            cancelButton.onAction = EventHandler { event: ActionEvent? -> handleCancelEvent() }
            buttonLayout = HBox(applyButton, cancelButton)
            if (registryTask != null) {
                // if task failed add error to button layout
                buttonLayout.children.add(errorLabel)
            }
            return buttonLayout
        }

        if (registryTask != null) {
            return errorLabel
        }

        return super.createValueGraphic()
    }

    @Throws(CouldNotPerformException::class)
    override fun update(value: MB) {
        //TODO handle local changes but global update

        inUpdate = true
        try {
            isChanged = false
            if (registryTask != null) {
                registryTask = null
            }

            super.update(value)
        } finally {
            inUpdate = false
        }
    }

    private fun handleCancelEvent() {
        val id = builder!!.getField(idField) as String
        if (id.isEmpty()) {
            parent.children.remove(this@RegistryMessageTreeItem)
        } else {
            try {
                val oldBuilder = Registries.getById(id, builder).toBuilder() as MB
                try {
                    update(oldBuilder)
                } catch (ex: CouldNotPerformException) {
                    logger.error("Could not update tree item with old builder from registry", ex)
                }
            } catch (ex: CouldNotPerformException) {
                ExceptionPrinter.printHistory(
                    "Could not retrieve message with id[" + id + "] for type[" + builder.javaClass.name + "] from registry",
                    ex,
                    logger,
                    LogLevel.WARN
                )
            }
        }
    }

    private fun handleApplyEvent() {
        logger.info("Apply button pressed")
        handleRequiredFields()

        try {
            val message: Message
            try {
                message = builder!!.build()
            } catch (ex: Throwable) {
                logger.info("Build failed", ex)
                throw ex
            }

            if (Registries.contains(message)) {
                // save original value from model
                val original = Registries.getById(ProtoBufFieldProcessor.getId(message))
                isChanged = false
                registryTask = Registries.update(message)
                GlobalCachedExecutorService.submit {
                    val update: Message?
                    try {
                        update = registryTask?.get()

                        // check if update and original are the same, then the changed values where reset and a registry update is not triggered
                        if (original == update) {
                            // reset the container to its old value
                            Platform.runLater {
                                try {
                                    this@RegistryMessageTreeItem.update(original.toBuilder() as MB)
                                } catch (e: CouldNotPerformException) {
                                    logger.error("Could not reset tree item", e)
                                }
                            }
                        }
                    } catch (e: InterruptedException) {
                        // just let the thread finish
                    } catch (e: ExecutionException) {
                        isChanged = true
                        // update the current value which will trigger an update of the displayed graphics,
                        // this will display the exception to the user
                        setValue(valueCasted.createNew(builder))
                    }
                }
            } else {
                registryTask = Registries.register(message)
                GlobalCachedExecutorService.submit {
                    try {
                        registryTask?.get()
                        Platform.runLater { parent.children.remove(this) }
                    } catch (e: InterruptedException) {
                        // just let the thread finish
                    } catch (e: ExecutionException) {
                        // update the current value which will trigger an update of the displayed graphics,
                        // this will display the exception to the user
                        setValue(valueCasted.createNew(builder))
                    }
                }
            }
            setValue(valueCasted.createNew(builder))
        } catch (ex: CouldNotPerformException) {
            logger.error("Error while applying event", ex)
        }
    }

    private fun handleRequiredFields() {
        if (!builder!!.isInitialized) {
            if (ProtoBufFieldProcessor.checkIfSomeButNotAllRequiredFieldsAreSet(builder)) {
                RequiredFieldAlert(builder)
            } else {
                ProtoBufFieldProcessor.clearRequiredFields(builder)
            }
        }
    }

    fun handleRemoveEvent() {
        val id = builder!!.getField(idField) as String
        try {
            logger.debug("Removing message with Id [$id]")
            if ("" != id && Registries.containsById(id, builder)) {
                val alert = Alert(AlertType.CONFIRMATION)
                alert.title = "Remove Action"
                alert.headerText = "Attention! Irreversible Action!"
                alert.contentText = "Do you really want to remove $descriptionText?"

                val result = alert.showAndWait()
                if (result.isPresent && result.get() != ButtonType.OK) {
                    return
                }

                // always remove by id to ignore not initialized fields of the builder
                registryTask = Registries.remove(Registries.getById(id, builder))
                value = valueCasted.createNew(builder)
                GlobalCachedExecutorService.submit {
                    try {
                        registryTask?.get()
                    } catch (e: InterruptedException) {
                        // just let the thread finish
                    } catch (e: ExecutionException) {
                        // update the current value which will trigger an update of the displayed graphics,
                        // this will display the exception to the user
                        value = valueCasted.createNew(builder)
                    }
                }
            } else {
                Platform.runLater { parent.children.remove(this@RegistryMessageTreeItem) }
            }
        } catch (ex: CouldNotPerformException) {
            //TODO: handle better
            ExceptionPrinter.printHistory(ex, logger)
        }
    }
}
