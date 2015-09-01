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
import de.citec.jps.core.JPService;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.InitializationException;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.extension.rsb.com.RSBRemoteService;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.scm.remote.SceneRegistryRemote;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import rst.homeautomation.control.agent.AgentConfigType.AgentConfig;
import rst.homeautomation.control.app.AppConfigType.AppConfig;
import rst.homeautomation.control.scene.SceneConfigType.SceneConfig;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class RemotePool {

    private static RemotePool remotePool;

    private final DeviceRegistryRemote deviceRemote;
    private final LocationRegistryRemote locationRemote;
    private final SceneRegistryRemote sceneRemote;
    private final AgentRegistryRemote agentRemote;
    private final AppRegistryRemote appRemote;

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
    }

    public void init() throws InitializationException {
        deviceRemote.init(JPService.getProperty(JPDeviceRegistryScope.class).getValue());
        locationRemote.init(JPService.getProperty(JPLocationRegistryScope.class).getValue());
        sceneRemote.init(JPService.getProperty(JPSceneRegistryScope.class).getValue());
        agentRemote.init(JPService.getProperty(JPAgentRegistryScope.class).getValue());
        appRemote.init(JPService.getProperty(JPAppRegistryScope.class).getValue());
    }

    public void shutdown() {
        deviceRemote.shutdown();
        locationRemote.shutdown();
        sceneRemote.shutdown();
        agentRemote.shutdown();
        appRemote.shutdown();
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

    public boolean isSendableMessage(Message.Builder builder) {
        for (RSBRemoteService remote : getRemotes()) {
            try {
                remote.getClass().getMethod(getMethodName("get", "s", builder));
                return true;
            } catch (NoSuchMethodException | SecurityException ex) {
            }
        }
        return false;
    }

    public GeneratedMessage.Builder getById(String id, Message msg) throws CouldNotPerformException {
        String methodName = getMethodName("get", "ById", msg);
        try {
            RSBRemoteService remote = getRemoteByMessage(msg);
            Method method = remote.getClass().getMethod(methodName, String.class);
            return (GeneratedMessage.Builder) method.invoke(remote, id);
        } catch (CouldNotPerformException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException(ex);
        }
    }

    public <M extends Message> List<M> getMessageList(Message msg) throws CouldNotPerformException {
        String methodName = getMethodName("get", "s", msg);
        try {
            RSBRemoteService remote = getRemoteByMessage(msg);
            Method method = remote.getClass().getMethod(methodName);
            return (List<M>) (GeneratedMessage.Builder) method.invoke(remote);
        } catch (CouldNotPerformException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException(ex);
        }
    }

    private String getMethodName(String prefix, String suffix, Message msg) {
        return prefix + msg.getClass().getSimpleName() + suffix;
    }

    private String getMethodName(String prefix, String suffix, Message.Builder msg) {
        return prefix + msg.getClass().getName().split("\\$")[1] + suffix;
    }

    public RSBRemoteService getRemoteByMessage(Message msg) throws CouldNotPerformException {
        if (msg instanceof DeviceClass || msg instanceof DeviceConfig || msg instanceof UnitTemplate) {
            return deviceRemote;
        } else if (msg instanceof LocationConfig) {
            return locationRemote;
        } else if (msg instanceof AgentConfig) {
            return agentRemote;
        } else if (msg instanceof SceneConfig) {
            return sceneRemote;
        } else if (msg instanceof AppConfig) {
            return appRemote;
        } else {
            throw new CouldNotPerformException("No matching remote found");
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

    public List<RSBRemoteService> getRemotes() {
        List<RSBRemoteService> remotes = new ArrayList<>();
        remotes.add(appRemote);
        remotes.add(agentRemote);
        remotes.add(deviceRemote);
        remotes.add(locationRemote);
        remotes.add(sceneRemote);
        return remotes;
    }
}
