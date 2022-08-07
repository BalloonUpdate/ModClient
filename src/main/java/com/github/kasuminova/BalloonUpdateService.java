package com.github.kasuminova;

import com.github.balloonupdate.GraphicsMain;
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
        GraphicsMain.main(true);
    }

    public void beginScanning(IEnvironment environment) {
    }

    public void onLoad(IEnvironment env, Set<String> otherServices) {
    }

    public List<ITransformer> transformers() {
        return Collections.emptyList();
    }
}
