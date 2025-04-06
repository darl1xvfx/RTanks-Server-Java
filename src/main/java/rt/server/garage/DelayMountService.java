package rt.server.garage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DelayMountService {
    public int delayMountWeaponInSec = 0;
    public int delayMountResistanceInSec = 0;
    public int delayMountArmorInSec = 0;
       
    private ScheduledExecutorService scheduler;

    public void startTimer() {
        scheduler = Executors.newScheduledThreadPool(1);
        
        Runnable task = () -> {
            if (delayMountWeaponInSec > 0) {
                delayMountWeaponInSec--;
            }
            if (delayMountResistanceInSec > 0) {
                delayMountResistanceInSec--;
            }
            if (delayMountArmorInSec > 0) {
                delayMountArmorInSec--;
            }
            if (delayMountWeaponInSec == 0 && 
                delayMountResistanceInSec == 0 && 
                delayMountArmorInSec == 0) {
                stopTimer();
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    public void stopTimer() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
