package com.github.kasuminova;

import com.github.balloonupdate.BalloonUpdateMain;
import com.github.balloonupdate.logging.LogSys;
import com.github.balloonupdate.util.FileObject;
import com.github.kasuminova.Downloader.SetupSwing;
import net.minecraftforge.fml.relauncher.IFMLCallHook;

import java.util.Map;

public class StartUpdate implements IFMLCallHook {
    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public Void call() {
        SetupSwing.init();
        FileObject externalConfig = new FileObject(BalloonUpdate.mcLocation + "/config/balloon-update.yml");
        new BalloonUpdateMain().run(true, false, externalConfig, false);
        LogSys.INSTANCE.info("finished!", true);
        return null;
    }
}
