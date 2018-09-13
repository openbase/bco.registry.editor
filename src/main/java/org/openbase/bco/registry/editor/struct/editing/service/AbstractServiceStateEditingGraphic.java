package org.openbase.bco.registry.editor.struct.editing.service;

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
