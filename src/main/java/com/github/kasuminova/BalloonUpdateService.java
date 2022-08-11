package com.github.kasuminova;

import com.github.balloonupdate.BalloonUpdateMain;
import com.github.balloonupdate.logging.LogSys;
import com.github.balloonupdate.util.FileObject;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;

import java.nio.file.Path;
import java.util.*;
import java.util.List;

import cpw.mods.modlauncher.api.TypesafeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class BalloonUpdateService implements ITransformationService {
    public static final Logger LOGGER = LogManager.getLogger("BalloonUpdaterService");
    public static String mcLocation;

    public BalloonUpdateService() {
    }

    @NotNull
    public String name() {
        return "balloonupdaterservice";
    }

    public void initialize(IEnvironment environment) {
        mcLocation = ((Path)environment.getProperty((TypesafeMap.Key) IEnvironment.Keys.GAMEDIR.get()).get()).toString();
        FileObject externalConfig = new FileObject(mcLocation + "/config/balloon-update.yml");
        new BalloonUpdateMain().run(true, false, externalConfig, false);
        LogSys.INSTANCE.info("finished!", true);
    }

    public void beginScanning(IEnvironment environment) {
    }

    public void onLoad(IEnvironment env, Set<String> otherServices) {
    }

    public List<ITransformer> transformers() {
        return Collections.emptyList();
    }
}
