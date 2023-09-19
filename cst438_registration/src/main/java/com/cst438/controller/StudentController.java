package com.cst438.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;
@RestController
@CrossOrigin 
public class StudentController {
	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	@Autowired
	GradebookService gradebookService;
	/*
	 * get all students.
	 */
	@GetMapping("/student")
	public StudentDTO getStudent( @RequestParam("email") String email ) {
		
		Student student = studentRepository.findByEmail(email);
		if (student != null) {
			System.out.println("/student called. "+student.getName()+" "+student.getStudent_id());
			StudentDTO newStudent = createStudent(student);
			return newStudent;
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student doesn't exist.  ");
		}
	}
	/*
	 * add a student
	 */
	@PostMapping("/students/add/{name}/{email}")
	@Transactional
	public void addStudent( @PathVariable String name, @PathVariable String email ) { 
		Student student = studentRepository.findByEmail(email);
		// student.status
		// = 0  ok to register.  != 0 registration is on hold.		
		if (student == null) {
			// TODO check that today's date is not past add deadline for the course.
			Student newStudent = new Student();
			newStudent.setName(name);
			newStudent.setEmail(email);
			newStudent.setStatusCode(0);
			
			System.out.println("/student/add/ called. "+newStudent.getName()+" "+newStudent.getStudent_id());

			studentRepository.save(newStudent);
			// return true if student successfully
			System.out.print("Added student to student repository.");
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student already exists.  ");
		}	
	}
	
	
	/*
	 * drop a course from student schedule
	 */
	@DeleteMapping("/student/{student_id}")
	@Transactional
	public void deleteStudent(  @PathVariable int student_id ) {
		;   // student's email 
		// TODO  check that today's date is not past deadline to drop course.
		Student student = studentRepository.findById(student_id).orElse(null);
		if (student!=null && student.getStudent_id() == student_id ) {
			System.out.println("Delete student called. "+student.getName()+" "+student.getStudent_id());
			// OK.  drop the course.
			 studentRepository.delete(student);
		} else {
			// something is not right with the enrollment.  
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student_id invalid. "+student_id);
		}
	}
	
	@PutMapping("/student/{id}")
	public void updateStudent(@RequestBody StudentDTO student_enrollment, @PathVariable ("id") int id) {
		Student student = checkStudent(id);
		student.setName(student_enrollment.name());
		student.setEmail(student_enrollment.email());
		student.setStatus(student_enrollment.status().toString());
		student.setStatusCode(student_enrollment.status_code());
		studentRepository.save(student);
	}
	/* 
	 * helper method to transform course, enrollment, student entities into 
	 * a an instances of ScheduleDTO to return to front end.
	 * This makes the front end less dependent on the details of the database.
	 */
//	private EnrollmentDTO[] createSchedule(int year, String semester, Student s, List<Enrollment> enrollments) {
//		EnrollmentDTO[] result = new EnrollmentDTO[enrollments.size()];
//		for (int i=0; i<enrollments.size(); i++) {
//			EnrollmentDTO dto = createSchedule(enrollments.get(i));
//			result[i] = dto;
//		}
//		return result;
//	}
//	
	
	private Student checkStudent(int id) {
		Student student = studentRepository.findById(id).orElse(null);
		if ( student == null ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student not found. "+id );
		}
		
		return student;
	}
	
	private StudentDTO createStudent(Student s) {
		
		StudentDTO dto = new StudentDTO(
		   s.getStudent_id(),
		   s.getName(),
		   s.getEmail(),
		   null,
		   -1
		   );
		   
		return dto;
	}
	
//	
}
