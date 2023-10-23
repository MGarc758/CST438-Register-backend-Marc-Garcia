package com.cst438.controller;

import java.util.ArrayList;
import java.util.List;
import java.security.Principal;

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
import com.cst438.domain.UserRepository;
import com.cst438.domain.User;
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
	UserRepository userRepository;
	
	@Autowired
	GradebookService gradebookService;
	
	/*
	 * get all students.
	 */
	@GetMapping("/student")
	public StudentDTO getStudent(Principal principal) {
		User user = userRepository.findByAlias(principal.getName());
		if (user != null && user.getRole().equals("USER")) {
		
			Student student = studentRepository.findByEmail(user.getEmail());
			if (student != null) {
				System.out.println("/student called. "+student.getName()+" "+student.getStudent_id());
				StudentDTO newStudent = createStudent(student);
				return newStudent;
			} else {
				throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student doesn't exist.  ");
			}
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "User not found. ");
		}
	}
	
	@GetMapping("/students")
	public StudentDTO[] getAllStudents(Principal principal) {
		User user = userRepository.findByAlias(principal.getName());
		if (user != null && user.getRole().equals("ADMIN")) {		
			Iterable<Student> students = studentRepository.findAll();
			if (students != null) {
				List<StudentDTO> allStudents = new ArrayList<StudentDTO>();
				for( Student s : students) {
					StudentDTO temp = createStudent(s);
					allStudents.add(temp);
				}
				
				StudentDTO[] output = new StudentDTO[allStudents.size()];
				for ( int i = 0; i < allStudents.size(); i++) {
					output[i] = allStudents.get(i);
				}
				
				return output;
			} else {
				throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student doesn't exist.  ");
			}
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "User not found. ");
		}
	}
	
	/*
	 * add a student
	 */
	@PostMapping("/students/add/{name}/{email}")
	@Transactional
	public void addStudent(Principal principal, @PathVariable String name, @PathVariable String email ) { 
		User user = userRepository.findByAlias(principal.getName());
		if (user != null && user.getRole().equals("ADMIN")) {		
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
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "User not found. ");
		}
	}
	
	
	/*
	 * drop a course from student schedule
	 */
	@DeleteMapping("/student/{student_id}")
	@Transactional
	public void deleteStudent(Principal principal, @PathVariable int student_id ) {
		;   // student's email 
		// TODO  check that today's date is not past deadline to drop course.
		User user = userRepository.findByAlias(principal.getName());
		if (user != null && user.getRole().equals("ADMIN")) {		
			Student student = studentRepository.findById(student_id).orElse(null);
			if (student!=null && student.getStudent_id() == student_id ) {
				System.out.println("Delete student called. "+student.getName()+" "+student.getStudent_id());
				// OK.  drop the course.
				 studentRepository.delete(student);
			} else {
				// something is not right with the enrollment.  
				throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student_id invalid. "+student_id);
			}
		} else {
			// something is not right with the enrollment.  
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "User not found. "+student_id);
		}
	}
	
	@PutMapping("/student/{id}")
	public void updateStudent(Principal principal, @RequestBody StudentDTO student_enrollment, @PathVariable ("id") int id) {
		User user = userRepository.findByAlias(principal.getName());
		if (user != null && user.getRole().equals("ADMIN")) {		
		Student student = checkStudent(id);
		student.setName(student_enrollment.name());
		student.setEmail(student_enrollment.email());
		student.setStatus(student_enrollment.status().toString());
		student.setStatusCode(student_enrollment.status_code());
		studentRepository.save(student);
		} else {
			// something is not right with the enrollment.  
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "User not found. "+id);
		}
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
