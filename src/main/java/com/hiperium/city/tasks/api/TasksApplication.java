package com.hiperium.city.tasks.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hiperium.city.tasks.api.utils.EnvironmentUtil;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import com.hiperium.city.tasks.api.vo.AuroraPostgresSecretVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.MessageFormat;
import java.util.Objects;

@SpringBootApplication
public class TasksApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(TasksApplication.class);
	private static final String JDBC_SQL_CONNECTION = "jdbc:postgresql://{0}:{1}/{2}";

	public static void main(String[] args) throws JsonProcessingException {
		LOGGER.info("main() - BEGIN");
		loadEnvironmentVariables();
		SpringApplication.run(TasksApplication.class, args);
		LOGGER.info("main() - END");
	}

	private static void loadEnvironmentVariables() throws JsonProcessingException {
		settingJdbcConnection();
		settingDefaultTimeZone();
		settingAwsRegion();
		settingAwsEndpointOverride();
	}

	private static void settingJdbcConnection() throws JsonProcessingException {
		AuroraPostgresSecretVO auroraSecretVO = EnvironmentUtil.getAuroraSecretVO();
		if (Objects.nonNull(auroraSecretVO)) {
			String sqlConnection = MessageFormat.format(JDBC_SQL_CONNECTION, auroraSecretVO.getHost(),
					auroraSecretVO.getPort(), auroraSecretVO.getDbname());
			LOGGER.info("JDBC Connection found: {}", sqlConnection);
			System.setProperty("spring.datasource.url", sqlConnection);
			System.setProperty("spring.datasource.username", auroraSecretVO.getUsername());
			System.setProperty("spring.datasource.password", auroraSecretVO.getPassword());
			System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
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

	private static void settingAwsEndpointOverride() {
		String endpointOverride = EnvironmentUtil.getAwsEndpointOverride();
		if (Objects.nonNull(endpointOverride)) {
			LOGGER.info("AWS Endpoint-Override found: {}", endpointOverride);
			System.setProperty("aws.dynamodb.endpoint-override", endpointOverride);
		}
	}
}
