/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.util;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import de.citec.agm.remote.AgentRegistryRemote;
import de.citec.apm.remote.AppRegistryRemote;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jp.JPAgentRegistryScope;
import de.citec.jp.JPAppRegistryScope;
import de.citec.jp.JPDeviceRegistryScope;
import de.citec.jp.JPLocationRegistryScope;
import de.citec.jp.JPSceneRegistryScope;
import de.citec.jp.JPUserRegistryScope;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.InitializationException;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.extension.rsb.com.RSBRemoteService;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.pem.remote.UserRegistryRemote;
import de.citec.scm.remote.SceneRegistryRemote;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.dc.jps.core.JPService;
import org.dc.jps.exception.JPServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.authorization.GroupConfigType.GroupConfig;
import rst.authorization.UserConfigType.UserConfig;
import rst.homeautomation.control.agent.AgentConfigType.AgentConfig;
import rst.homeautomation.control.app.AppConfigType.AppConfig;
import rst.homeautomation.control.scene.SceneConfigType.SceneConfig;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
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

    public static RemotePool getInstance() throws InstantiationException {
        if (remotePool == null) {
            remotePool = new RemotePool();
        }
        return remotePool;
    }

    public RemotePool() throws InstantiationException {
        this.deviceRemote = new DeviceRegistryRemote();
        this.locationRemote = new LocationRegistryRemote();
        this.sceneRemote = new SceneRegistryRemote();
        this.agentRemote = new AgentRegistryRemote();
        this.appRemote = new AppRegistryRemote();
        this.userRemote = new UserRegistryRemote();
    }

    public void init() throws InitializationException {
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

    public <M extends Message> M register(Message msg) throws CouldNotPerformException {
        try {
            return (M) invokeMethod("register", msg);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not register [" + msg + "]", ex);
        }
    }

    public <M extends Message> M update(Message msg) throws CouldNotPerformException {
        try {
            return (M) invokeMethod("update", msg);
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

    public <M extends Message> M remove(Message msg) throws CouldNotPerformException {
        try {
            return (M) invokeMethod("remove", msg);
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
        if (msg instanceof DeviceClass) {
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
            return ((Future<Boolean>) method.invoke(remote)).get();
        } catch (CouldNotPerformException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InterruptedException | ExecutionException ex) {
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
        if (builder instanceof DeviceClass.Builder || builder instanceof DeviceConfig.Builder || builder instanceof UnitTemplate.Builder) {
            return deviceRemote;
        } else if (builder instanceof LocationConfig.Builder || builder instanceof ConnectionConfig.Builder) {
            return locationRemote;
        } else if (builder instanceof AgentConfig.Builder) {
            return agentRemote;
        } else if (builder instanceof SceneConfig.Builder) {
            return sceneRemote;
        } else if (builder instanceof AppConfig.Builder) {
            return appRemote;
        } else if (builder instanceof UserConfig.Builder || builder instanceof GroupConfig.Builder) {
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
