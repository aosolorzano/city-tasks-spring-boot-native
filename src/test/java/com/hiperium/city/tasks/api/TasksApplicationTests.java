package com.hiperium.city.tasks.api;

import com.hiperium.city.tasks.api.common.AbstractContainerBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
class TasksApplicationTests {

	@Test
	void contextLoads() {
		Assertions.assertThat(Boolean.TRUE).isTrue();
	}

}
