package com.cst438;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.fasterxml.jackson.databind.ObjectMapper;

/* 
 * Example of using Junit 
 * Mockmvc is used to test a simulated REST call to the RestController
 */
@SpringBootTest
@AutoConfigureMockMvc
public class JunitTestStudent {

	@Autowired
	private MockMvc mvc;

	/*
	 * add course 40442 to student test@csumb.edu in schedule Fall 2021
	 */
	@Test
	public void TestGetStudent() throws Exception {
		
		MockHttpServletResponse response;
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/student?email=test@csumb.edu")
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
						
		// verify that returned data contains the added course 
		EnrollmentDTO student_enrollment = fromJsonString(response.getContentAsString(), EnrollmentDTO.class);
		System.out.print(student_enrollment.id() + " " + student_enrollment.studentName());
		boolean found = false;		
		if (student_enrollment.id() == 1) {
			found = true;
		}
		assertEquals(true, found, "Student not found in repository.");
	}
	
	@Test
	public void TestAddStudent()  throws Exception {
		
		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/students/add/Tester/test@gmail.com")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// do http GET for student schedule 
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/student?email=test@gmail.com")
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
						
		// verify that returned data contains the added course 
		EnrollmentDTO student_enrollment = fromJsonString(response.getContentAsString(), EnrollmentDTO.class);
		System.out.print(student_enrollment.id() + " " + student_enrollment.studentName());
		boolean found = false;		
		if (student_enrollment.id() == 5) {
			found = true;
		}
		assertEquals(true, found, "Added student not in updated repository.");
		
	}
	/*
	 * drop course 30157 Fall 2020 from student test@csumb.edu
	 */
	@Test
	public void deleteStudent()  throws Exception {
		
		MockHttpServletResponse response;
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/students/add/Andrea/andrea@gmail.com")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/student?email=andrea@gmail.com")
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		// verify that 30157 is in student schedule
		EnrollmentDTO student_enrollment = fromJsonString(response.getContentAsString(), EnrollmentDTO.class);
		
		boolean found = false;
		if ( student_enrollment.id() == 4) found = true;
		assertTrue(found);

		// drop course 30157 in Fall 2020
		response = mvc.perform(
				MockMvcRequestBuilders
			      .delete("/student/4"))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
	
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/student?email=andrea@gmail.com")
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		// verify that 30157 is not in student schedule
		
		assertEquals(400, response.getStatus());
	}
		
	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
