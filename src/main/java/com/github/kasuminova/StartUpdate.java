package com.github.kasuminova;

import com.github.balloonupdate.BalloonUpdateMain;
import com.github.balloonupdate.logging.LogSys;
import com.github.balloonupdate.util.File2;
import net.minecraftforge.fml.relauncher.IFMLCallHook;

import java.util.Map;

public class StartUpdate implements IFMLCallHook {
    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public Void call() {
        File2 externalConfig = new File2(BalloonUpdate.mcLocation + "/config/balloon-update.yml");
        new BalloonUpdateMain().run(true, false, externalConfig, false);
        LogSys.INSTANCE.info("finished!", true);
        return null;
    }
}
