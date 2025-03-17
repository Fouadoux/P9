package com.glucovision.patientservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@ActiveProfiles("test")  // Charge application-test.properties
class PatientServiceApplicationTests {

	@Test
	void contextLoads() {
	}
	@Autowired
	private DataSource dataSource;

	@Test
	void checkIfH2IsUsed() throws Exception {
		String driverName = dataSource.getConnection().getMetaData().getDriverName();
		System.out.println("Database Driver: " + driverName);
		assertThat(driverName).contains("H2");
	}
}
