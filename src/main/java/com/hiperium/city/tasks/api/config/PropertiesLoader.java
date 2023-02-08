package com.hiperium.city.tasks.api.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hiperium.city.tasks.api.utils.EnvironmentUtil;
import com.hiperium.city.tasks.api.vo.AuroraPostgresSecretVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Objects;

public final class PropertiesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);
    private static final String JDBC_SQL_CONNECTION = "jdbc:postgresql://{0}:{1}/{2}";
    public static final String POSTGRESQL_DRIVER_NAME = "org.postgresql.Driver";

    private PropertiesLoader() {
        // Private constructor.
    }

    public static void load() throws JsonProcessingException {
        settingJdbcConnection();
        settingDefaultTimeZone();
        settingAwsRegion();
        settingAwsAccessKey();
        settingAwsSecretKey();
        settingAwsEndpointOverride();
    }

    private static void settingJdbcConnection() throws JsonProcessingException {
        AuroraPostgresSecretVO auroraSecretVO = EnvironmentUtil.getAuroraSecretVO();
        if (Objects.nonNull(auroraSecretVO)) {
            String sqlConnection = MessageFormat.format(JDBC_SQL_CONNECTION, auroraSecretVO.host(),
                    auroraSecretVO.port(), auroraSecretVO.dbname());
            LOGGER.info("JDBC Connection found: {}", sqlConnection);
            // Set the default JDBC connection.
            System.setProperty("spring.datasource.url", sqlConnection);
            System.setProperty("spring.datasource.username", auroraSecretVO.username());
            System.setProperty("spring.datasource.password", auroraSecretVO.password());
            System.setProperty("spring.datasource.driver-class-name", POSTGRESQL_DRIVER_NAME);
            // Set the default JDBC connection for Quartz.
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.CityTasksQuartzDS.URL", sqlConnection);
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.CityTasksQuartzDS.user", auroraSecretVO.username());
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.CityTasksQuartzDS.password", auroraSecretVO.password());
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.CityTasksQuartzDS.driver", POSTGRESQL_DRIVER_NAME);
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.CityTasksQuartzDS.provider", "hikaricp");
        }
    }

    private static void settingDefaultTimeZone() {
        String timeZoneId = EnvironmentUtil.getTimeZoneId();
        if (Objects.nonNull(timeZoneId)) {
            LOGGER.info("Time Zone ID found: {}", timeZoneId);
            System.setProperty("hiperium.city.tasks.time.zone.id", timeZoneId);
        }
    }

    private static void settingAwsRegion() {
        String awsRegion = EnvironmentUtil.getAwsRegion();
        if (Objects.nonNull(awsRegion)) {
            LOGGER.info("AWS Region found: {}", awsRegion);
            System.setProperty("aws.region", awsRegion);
        }
    }

    private static void settingAwsAccessKey() {
        String awsAccessKey = EnvironmentUtil.getAwsAccessKey();
        if (Objects.nonNull(awsAccessKey)) {
            LOGGER.info("AWS Access Key found: {}", awsAccessKey);
            System.setProperty("aws.accessKeyId", awsAccessKey);
        }
    }

    private static void settingAwsSecretKey() {
        String awsSecretKey = EnvironmentUtil.getAwsSecretKey();
        if (Objects.nonNull(awsSecretKey)) {
            LOGGER.info("AWS Secret Key found: {}", awsSecretKey);
            System.setProperty("aws.secretKey", awsSecretKey);
        }
    }

    private static void settingAwsEndpointOverride() {
        String endpointOverride = EnvironmentUtil.getAwsEndpointOverride();
        if (Objects.nonNull(endpointOverride)) {
            LOGGER.info("AWS Endpoint-Override found: {}", endpointOverride);
            System.setProperty("aws.dynamodb.endpoint-override", endpointOverride);
        }
    }
}
