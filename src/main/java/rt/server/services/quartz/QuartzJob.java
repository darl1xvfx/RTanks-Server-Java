/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package rt.server.services.quartz;

import org.quartz.JobExecutionContext;

public interface QuartzJob {
    public void run(JobExecutionContext var1);
}

