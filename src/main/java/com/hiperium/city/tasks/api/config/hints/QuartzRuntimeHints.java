package com.hiperium.city.tasks.api.config.hints;

import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.jdbcjobstore.JobStoreSupport;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.jdbcjobstore.PostgreSQLDelegate;
import org.quartz.utils.HikariCpPoolingConnectionProvider;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class QuartzRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(JobStoreSupport.class, MemberCategory.values());
        hints.reflection().registerType(JobStoreTX.class, MemberCategory.values());
        hints.reflection().registerType(StdSchedulerFactory.class, MemberCategory.values());
        hints.reflection().registerType(HikariCpPoolingConnectionProvider.class, MemberCategory.values());
        hints.reflection().registerType(PostgreSQLDelegate.class, MemberCategory.values());
    }
}
