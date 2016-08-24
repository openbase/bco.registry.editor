package org.openbase.bco.registry.editor.util;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 openbase.org
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
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.openbase.bco.registry.agent.lib.jp.JPAgentRegistryScope;
import org.openbase.bco.registry.agent.remote.AgentRegistryRemote;
import org.openbase.bco.registry.app.lib.jp.JPAppRegistryScope;
import org.openbase.bco.registry.app.remote.AppRegistryRemote;
import org.openbase.bco.registry.device.lib.jp.JPDeviceRegistryScope;
import org.openbase.bco.registry.device.remote.DeviceRegistryRemote;
import org.openbase.bco.registry.location.lib.jp.JPLocationRegistryScope;
import org.openbase.bco.registry.location.remote.LocationRegistryRemote;
import org.openbase.bco.registry.scene.lib.jp.JPSceneRegistryScope;
import org.openbase.bco.registry.scene.remote.SceneRegistryRemote;
import org.openbase.bco.registry.user.lib.jp.JPUserRegistryScope;
import org.openbase.bco.registry.user.remote.UserRegistryRemote;
import org.openbase.jps.core.JPService;
import org.openbase.jps.exception.JPServiceException;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.extension.rsb.com.RSBRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.authorization.UserConfigType.UserConfig;
import rst.authorization.UserGroupConfigType.UserGroupConfig;
import rst.homeautomation.control.agent.AgentClassType.AgentClass;
import rst.homeautomation.control.agent.AgentConfigType.AgentConfig;
import rst.homeautomation.control.app.AppClassType.AppClass;
import rst.homeautomation.control.app.AppConfigType.AppConfig;
import rst.homeautomation.control.scene.SceneConfigType.SceneConfig;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.homeautomation.unit.UnitGroupConfigType.UnitGroupConfig;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate;
import rst.spatial.ConnectionConfigType.ConnectionConfig;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class RemotePool {

    private static final Logger logger = LoggerFactory.getLogger(RemotePool.class);

    private static RemotePool remotePool;

    private final DeviceRegistryRemote deviceRemote;
    private final LocationRegistryRemote locationRemote;
    private final SceneRegistryRemote sceneRemote;
    private final AgentRegistryRemote agentRemote;
    private final AppRegistryRemote appRemote;
    private final UserRegistryRemote userRemote;

    public static RemotePool getInstance() throws InstantiationException, InterruptedException {
        if (remotePool == null) {
            remotePool = new RemotePool();
        }
        return remotePool;
    }

    public RemotePool() throws InstantiationException, InterruptedException {
        this.deviceRemote = new DeviceRegistryRemote();
        this.locationRemote = new LocationRegistryRemote();
        this.sceneRemote = new SceneRegistryRemote();
        this.agentRemote = new AgentRegistryRemote();
        this.appRemote = new AppRegistryRemote();
        this.userRemote = new UserRegistryRemote();
    }

    public void init() throws InitializationException, InterruptedException {
        try {
            deviceRemote.init(JPService.getProperty(JPDeviceRegistryScope.class).getValue());
            locationRemote.init(JPService.getProperty(JPLocationRegistryScope.class).getValue());
            sceneRemote.init(JPService.getProperty(JPSceneRegistryScope.class).getValue());
            agentRemote.init(JPService.getProperty(JPAgentRegistryScope.class).getValue());
            appRemote.init(JPService.getProperty(JPAppRegistryScope.class).getValue());
            userRemote.init(JPService.getProperty(JPUserRegistryScope.class).getValue());
        } catch (JPServiceException ex) {
            throw new InitializationException(this, ex);
        }
    }

    public void shutdown() {
        deviceRemote.shutdown();
        locationRemote.shutdown();
        sceneRemote.shutdown();
        agentRemote.shutdown();
        appRemote.shutdown();
        userRemote.shutdown();
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

    public GeneratedMessage.Builder getById(String id, Message msg) throws CouldNotPerformException {
        String methodName = getMethodName("get", "ById", msg);
        try {
            RSBRemoteService remote = getRemoteByMessage(msg);
            Method method = remote.getClass().getMethod(methodName, String.class);
            Object result = method.invoke(remote, id);
            Method toBuilder = result.getClass().getMethod("toBuilder");
            return (GeneratedMessage.Builder) toBuilder.invoke(result);
        } catch (CouldNotPerformException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException(ex);
        }
    }

    public GeneratedMessage.Builder getById(String id, Message.Builder builder) throws CouldNotPerformException {
        String methodName = getMethodName("get", "ById", builder);
        try {
            RSBRemoteService remote = getRemoteByMessageBuilder(builder);
            Method method = remote.getClass().getMethod(methodName, String.class);
            Object result = method.invoke(remote, id);
            Method toBuilder = result.getClass().getMethod("toBuilder");
            return (GeneratedMessage.Builder) toBuilder.invoke(result);
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

    public boolean isReadOnly(SendableType type) throws CouldNotPerformException {
        String methodName = getMethodName("is", "RegistryReadOnly", type.getDefaultInstanceForType());
        try {
            RSBRemoteService remote = getRemoteByMessage(type.getDefaultInstanceForType());
            Method method = remote.getClass().getMethod(methodName);
            return (Boolean) method.invoke(remote);
        } catch (CouldNotPerformException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException(ex);
        }
    }

    private String getMethodName(String prefix, String suffix, Message msg) {
        return prefix + msg.getClass().getSimpleName() + suffix;
    }

    private String getMethodName(String prefix, String suffix, Message.Builder msg) {
        return prefix + msg.getDescriptorForType().getName() + suffix;
    }

    public RSBRemoteService getRemoteByMessage(Message msg) throws CouldNotPerformException {
        return getRemoteByMessageBuilder(msg.toBuilder());
    }

    public RSBRemoteService getRemoteByMessageBuilder(Message.Builder builder) throws CouldNotPerformException {
        if (builder instanceof DeviceClass.Builder || builder instanceof DeviceConfig.Builder || builder instanceof UnitTemplate.Builder || builder instanceof UnitGroupConfig.Builder || builder instanceof UnitConfig.Builder) {
            return deviceRemote;
        } else if (builder instanceof LocationConfig.Builder || builder instanceof ConnectionConfig.Builder) {
            return locationRemote;
        } else if (builder instanceof AgentConfig.Builder || builder instanceof AgentClass.Builder) {
            return agentRemote;
        } else if (builder instanceof SceneConfig.Builder) {
            return sceneRemote;
        } else if (builder instanceof AppConfig.Builder || builder instanceof AppClass.Builder) {
            return appRemote;
        } else if (builder instanceof UserConfig.Builder || builder instanceof UserGroupConfig.Builder) {
            return userRemote;
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

    public List<RSBRemoteService> getRemotes() {
        List<RSBRemoteService> remotes = new ArrayList<>();
        remotes.add(appRemote);
        remotes.add(agentRemote);
        remotes.add(locationRemote);
        remotes.add(deviceRemote);
        remotes.add(sceneRemote);
        remotes.add(userRemote);
        return remotes;
    }
}
