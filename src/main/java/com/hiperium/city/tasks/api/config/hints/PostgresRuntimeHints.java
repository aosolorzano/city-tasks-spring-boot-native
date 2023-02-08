package com.hiperium.city.tasks.api.config.hints;

import org.hibernate.dialect.PostgreSQLPGObjectJdbcType;
import org.postgresql.util.PGobject;
import org.quartz.impl.jdbcjobstore.JobStoreSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class PostgresRuntimeHints implements RuntimeHintsRegistrar {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresRuntimeHints.class);

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        LOGGER.info("registerHints() - BEGIN");
        hints.reflection().registerType(PGobject.class, MemberCategory.values());
        LOGGER.info("registerHints() - END");
    }
}
