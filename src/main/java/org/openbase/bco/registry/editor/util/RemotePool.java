package org.openbase.bco.registry.editor.util;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2017 openbase.org
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import org.openbase.bco.registry.agent.remote.AgentRegistryRemote;
import org.openbase.bco.registry.app.remote.AppRegistryRemote;
import org.openbase.bco.registry.device.remote.DeviceRegistryRemote;
import org.openbase.bco.registry.location.remote.LocationRegistryRemote;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.bco.registry.scene.remote.SceneRegistryRemote;
import org.openbase.bco.registry.unit.remote.UnitRegistryRemote;
import org.openbase.bco.registry.user.remote.UserRegistryRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.rsb.com.RSBRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate;
import rst.domotic.unit.agent.AgentClassType.AgentClass;
import rst.domotic.unit.agent.AgentConfigType.AgentConfig;
import rst.domotic.unit.app.AppClassType.AppClass;
import rst.domotic.unit.app.AppConfigType.AppConfig;
import rst.domotic.unit.authorizationgroup.AuthorizationGroupConfigType.AuthorizationGroupConfig;
import rst.domotic.unit.connection.ConnectionConfigType.ConnectionConfig;
import rst.domotic.unit.device.DeviceClassType.DeviceClass;
import rst.domotic.unit.device.DeviceConfigType.DeviceConfig;
import rst.domotic.unit.location.LocationConfigType.LocationConfig;
import rst.domotic.unit.scene.SceneConfigType.SceneConfig;
import rst.domotic.unit.unitgroup.UnitGroupConfigType.UnitGroupConfig;
import rst.domotic.unit.user.UserConfigType.UserConfig;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RemotePool {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemotePool.class);

    private static RemotePool remotePool;

    private final DeviceRegistryRemote deviceRemote;
    private final LocationRegistryRemote locationRemote;
    private final SceneRegistryRemote sceneRemote;
    private final AgentRegistryRemote agentRemote;
    private final AppRegistryRemote appRemote;
    private final UserRegistryRemote userRemote;
    private final UnitRegistryRemote unitRemote;

    private final List<RSBRemoteService> remotes = new ArrayList();

    public static RemotePool getInstance() throws InstantiationException, InterruptedException {
        if (remotePool == null) {
            remotePool = new RemotePool();
        }
        return remotePool;
    }

    public RemotePool() throws InstantiationException, InterruptedException {
        try {
            this.unitRemote = Registries.getUnitRegistry();
            this.unitRemote.waitForData();
            this.locationRemote = Registries.getLocationRegistry();
            this.sceneRemote = Registries.getSceneRegistry();
            this.agentRemote = Registries.getAgentRegistry();
            this.appRemote = Registries.getAppRegistry();
            this.userRemote = Registries.getUserRegistry();
            this.deviceRemote = Registries.getDeviceRegistry();
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(RemotePool.class, ex);
        }
        remotes.add(unitRemote);
        remotes.add(appRemote);
        remotes.add(agentRemote);
        remotes.add(locationRemote);
        remotes.add(deviceRemote);
        remotes.add(sceneRemote);
        remotes.add(userRemote);
    }

    public <M extends Message> Future<M> register(Message msg) throws CouldNotPerformException {
        try {
            return (Future<M>) invokeMethod("register", msg);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not register [" + msg + "]", ex);
        }
    }

    public <M extends Message> Future<M> update(Message msg) throws CouldNotPerformException {
        try {
            return (Future<M>) invokeMethod("update", msg);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not update [" + msg + "]", ex);
        }
    }

    public Boolean contains(Message msg) throws CouldNotPerformException {
        try {
            return (Boolean) invokeMethod("contains", msg);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not test if [" + msg + "] is contained", ex);
        }
    }

    public Boolean containsById(Message.Builder msg, String id) throws CouldNotPerformException {
        String methodName = getMethodName("contains", "ById", msg);
        try {
            RSBRemoteService remote = getRemoteByMessage(msg);
            Method method = remote.getClass().getMethod(methodName, String.class);
            return (Boolean) method.invoke(remote, id);
        } catch (CouldNotPerformException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException(ex);
        }
    }

    public <M extends Message> Future<M> remove(Message msg) throws CouldNotPerformException {
        try {
            return (Future<M>) invokeMethod("remove", msg);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not remove [" + msg + "]", ex);
        }
    }

    private Object invokeMethod(String methodPrefix, Message msg) throws CouldNotPerformException {
        String methodName = getMethodName(methodPrefix, "", msg);
        try {
            RSBRemoteService remote = getRemoteByMessage(msg);
            Method method = remote.getClass().getMethod(methodName, msg.getClass());
            return method.invoke(remote, msg);
        } catch (CouldNotPerformException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException(ex);
        }
    }

    public Message getById(String id) throws NotAvailableException {
        for (SendableType sendableType : SendableType.values()) {
            try {
                return getById(id, sendableType.getDefaultInstanceForType());
            } catch (CouldNotPerformException ex) {

            }
        }
        throw new NotAvailableException("Could not find message with id [" + id + "]");
    }

    public Message getById(String id, Message msg) throws CouldNotPerformException {
        return getById(id, msg.toBuilder());
    }

    public Message getById(String id, Message.Builder builder) throws CouldNotPerformException {
        String methodName = getMethodName("get", "ById", builder);
        try {
            RSBRemoteService remote = getRemoteByMessage(builder);
            Method method = remote.getClass().getMethod(methodName, String.class);
            return (Message) method.invoke(remote, id);
        } catch (CouldNotPerformException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException(ex);
        }
    }

    public <M extends Message> List<M> getMessageList(Message msg) throws CouldNotPerformException {
        String methodName = getMethodName("get", "s", msg);
        if (msg instanceof DeviceClass || msg instanceof AgentClass || msg instanceof AppClass) {
            methodName = getMethodName("get", "es", msg);
        }
        try {
            RSBRemoteService remote = getRemoteByMessage(msg);
            Method method = remote.getClass().getMethod(methodName);
            return (List<M>) method.invoke(remote);
        } catch (CouldNotPerformException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException(ex);
        }
    }

    public Boolean isReadOnly(Message msg) {
        String methodName = getMethodName("is", "RegistryReadOnly", msg);
        try {
            RSBRemoteService remote = getRemoteByMessage(msg);

            if (remote.isConnected() && remote.isDataAvailable()) {
                
            }
            
            Method method = remote.getClass().getMethod(methodName);
            return (Boolean) method.invoke(remote);
        } catch (CouldNotPerformException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ExceptionPrinter.printHistory("Could not check read only state! Fallback is read only mode.", ex, LOGGER);
            return true;
        }
    }

    public Boolean isConsistent(Message msg) throws CouldNotPerformException {
        String methodName = getMethodName("is", "RegistryConsistent", msg);
        try {
            RSBRemoteService remote = getRemoteByMessage(msg);
            Method method = remote.getClass().getMethod(methodName);
            return (Boolean) method.invoke(remote);
        } catch (CouldNotPerformException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException(ex);
        }
    }

    private String getMethodName(String prefix, String suffix, Message msg) {
        return getMethodName(prefix, suffix, msg.toBuilder());
    }

    private String getMethodName(String prefix, String suffix, Message.Builder msg) {
        return prefix + getMethodNameForBuilder(msg) + suffix;
    }

    private String getMethodNameForBuilder(Message.Builder builder) {
        if (builder instanceof UnitConfig.Builder) {
            switch (((UnitConfig.Builder) builder).getType()) {
                case AGENT:
                    return AgentConfig.class.getSimpleName();
                case APP:
                    return AppConfig.class.getSimpleName();
                case DEVICE:
                    return DeviceConfig.class.getSimpleName();
                case LOCATION:
                    return LocationConfig.class.getSimpleName();
                case CONNECTION:
                    return ConnectionConfig.class.getSimpleName();
                case SCENE:
                    return SceneConfig.class.getSimpleName();
                case USER:
                    return UserConfig.class.getSimpleName();
                case AUTHORIZATION_GROUP:
                    return AuthorizationGroupConfig.class.getSimpleName();
                case UNIT_GROUP:
                    return UnitGroupConfig.class.getSimpleName();
                default:
                    return UnitConfig.class.getSimpleName();
            }
        } else {
            return builder.getDescriptorForType().getName();
        }
    }

    public RSBRemoteService getRemoteByMessage(Message msg) throws CouldNotPerformException {
        return getRemoteByMessage(msg.toBuilder());
    }

    public RSBRemoteService getRemoteByMessage(Message.Builder builder) throws CouldNotPerformException {
        if (builder instanceof DeviceClass.Builder) {
            return deviceRemote;
        } else if (builder instanceof AgentClass.Builder) {
            return agentRemote;
        } else if (builder instanceof AppClass.Builder) {
            return appRemote;
        } else if (builder instanceof UnitTemplate.Builder) {
            return unitRemote;
        } else if (builder instanceof ServiceTemplate.Builder) {
            return unitRemote;
        } else if (builder instanceof UnitConfig.Builder) {
            switch (((UnitConfig.Builder) builder).getType()) {
                case AGENT:
                    return agentRemote;
                case APP:
                    return appRemote;
                case DEVICE:
                    return deviceRemote;
                case LOCATION:
                    return locationRemote;
                case CONNECTION:
                    return locationRemote;
                case SCENE:
                    return sceneRemote;
                case USER:
                    return userRemote;
                case AUTHORIZATION_GROUP:
                    return userRemote;
                default:
                    return unitRemote;
            }
        } else {
            throw new CouldNotPerformException("No matching remote for type [" + builder.getDescriptorForType().getName() + "]found");
        }
    }

    public DeviceRegistryRemote getDeviceRemote() {
        return deviceRemote;
    }

    public LocationRegistryRemote getLocationRemote() {
        return locationRemote;
    }

    public SceneRegistryRemote getSceneRemote() {
        return sceneRemote;
    }

    public AgentRegistryRemote getAgentRemote() {
        return agentRemote;
    }

    public AppRegistryRemote getAppRemote() {
        return appRemote;
    }

    public UserRegistryRemote getUserRemote() {
        return userRemote;
    }

    public UnitRegistryRemote getUnitRemote() {
        return unitRemote;
    }

    public Collection<RSBRemoteService> getRemotes() {
        return Collections.unmodifiableCollection(remotes);
    }
}
