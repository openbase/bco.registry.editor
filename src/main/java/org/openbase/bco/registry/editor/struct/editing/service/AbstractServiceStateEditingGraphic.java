package org.openbase.bco.registry.editor.struct.editing.service;

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

import com.google.protobuf.Message;
import javafx.scene.Node;
import org.openbase.bco.registry.editor.struct.editing.ServiceStateAttributeEditingGraphic;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractServiceStateEditingGraphic<STATE extends Message, GRAPHIC extends Node> implements ServiceStateEditingGraphic<STATE> {

    private static final String METHOD_NAME_DEFAULT_INSTANCE = "getDefaultInstance";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final GRAPHIC graphic;
    private final Class<STATE> serviceStateClass;

    AbstractServiceStateEditingGraphic(final GRAPHIC graphic, final Class<STATE> serviceStateClass) {
        this.graphic = graphic;
        this.serviceStateClass = serviceStateClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(STATE state) {
        if (state == null) {
            try {
                final Method getDefaultInstance = serviceStateClass.getMethod(METHOD_NAME_DEFAULT_INSTANCE);
                state = (STATE) getDefaultInstance.invoke(null);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                ExceptionPrinter.printHistory("Could not call method[" + METHOD_NAME_DEFAULT_INSTANCE + "] on class[" + serviceStateClass.getSimpleName() + "]", ex, logger);
                return;
            }
        }
        internalInit(state);
    }

    abstract void internalInit(STATE state);

    @Override
    public GRAPHIC getGraphic() {
        return graphic;
    }

    @Override
    public void addCommitEditEventHandler(final ServiceStateAttributeEditingGraphic editingGraphic) {
        // do nothing but allow to be overwritten by implementations
    }
}
