package com.Quiz;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

	public class QuizApplication {

	    public static void main(String[] args) {
	        try {
	            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizdb","root","Aniket@04");
	            Scanner scanner = new Scanner(System.in);
	            
	            // Store the score
	            System.out.print("Enter student ID: ");
	            String studentId = scanner.next();
	            System.out.print("Enter student name: ");
	            String studentName = scanner.next();
	            

	            
	            List<Question> questions = getRandomQuestions(conn);
	            int score = 0;

	            for (Question q : questions) {
	                System.out.println(q.getQuestion());
	                System.out.println("1. " + q.getOption1());
	                System.out.println("2. " + q.getOption2());
	                System.out.println("3. " + q.getOption3());
	                System.out.println("4. " + q.getOption4());
	                System.out.print("Choose an option (1-4): ");
	                int answer = scanner.nextInt();
	                if (answer == q.getCorrectOption()) {
	                    score++;
	                }
	            }

	           

	            // Display results
	            System.out.println("Your score: " + score +"/10");
	            displayGrade(score);
	            if(score<=8 && score >=10) {
	            	System.out.println("Your Grade is A");
	            }
	            else if (score<=6 && score >=7) {
					System.out.println("Your Grade is B");
				}
	            
	            else if (score == 5) {
	            	System.out.println("Your Grade is C");
					
				}
	            else {
					System.out.println("You are Failed");
				}
	            
	            storeScore(conn, studentId, studentName, score);

	            // Display all scores
	            displayAllScores(conn);

	            // Retrieve score by student ID
	            System.out.print("Enter student ID to retrieve score: ");
	            String idToRetrieve = scanner.next();
	            retrieveScore(conn, idToRetrieve);

	            conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    private static List<Question> getRandomQuestions(Connection conn) throws SQLException {
	        List<Question> questions = new ArrayList<>();
	        String query = "SELECT * FROM questions ORDER BY RAND() LIMIT 10";
	        try (Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            while (rs.next()) {
	                questions.add(new Question(
	                    rs.getInt("id"),
	                    rs.getString("question"),
	                    rs.getString("option1"),
	                    rs.getString("option2"),
	                    rs.getString("option3"),
	                    rs.getString("option4"),
	                    rs.getInt("correct_option")
	                ));
	            }
	        }
	        return questions;
	    }

	    private static void storeScore(Connection conn, String studentId, String studentName, int score) throws SQLException {
	        String query = "INSERT INTO student_scores (student_id, student_name, score) VALUES (?, ?, ?)";
	        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	            pstmt.setString(1, studentId);
	            pstmt.setString(2, studentName);
	            pstmt.setInt(3, score);
	            pstmt.executeUpdate();
	        }
	    }

	    private static void displayGrade(int score) {
	        if (score >= 8) {
	            System.out.println("Class A");
	        } else if (score >= 6) {
	            System.out.println("Class B");
	        } else if (score == 5) {
	            System.out.println("Class C");
	        } else {
	            System.out.println("Fail");
	        }
	    }

	    private static void displayAllScores(Connection conn) throws SQLException {
	        String query = "SELECT * FROM student_scores ORDER BY score DESC";
	        try (Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            System.out.println("Student ID | Name | Score");
	            while (rs.next()) {
	                System.out.println(rs.getString("student_id") + " | " +
	                                   rs.getString("student_name") + " | " +
	                                   rs.getInt("score"));
	            }
	        }
	    }

	    private static void retrieveScore(Connection conn, String studentId) throws SQLException {
	        String query = "SELECT * FROM student_scores WHERE student_id = ?";
	        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	            pstmt.setString(1, studentId);
	            try (ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	                    System.out.println("Student ID: " + rs.getString("student_id"));
	                    System.out.println("Name: " + rs.getString("student_name"));
	                    System.out.println("Score: " + rs.getInt("score"));
	                } else {
	                    System.out.println("No record found for Student ID: " + studentId);
	                }
	            }
	        }
	    }
	}

	class Question {
	    private int id;
	    private String question;
	    private String option1;
	    private String option2;
	    private String option3;
	    private String option4;
	    private int correctOption;

	    public Question(int id, String question, String option1, String option2, String option3, String option4, int correctOption) {
	        this.id = id;
	        this.question = question;
	        this.option1 = option1;
	        this.option2 = option2;
	        this.option3 = option3;
	        this.option4 = option4;
	        this.correctOption = correctOption;
	    }

	    public String getQuestion() {
	        return question;
	    }

	    public String getOption1() {
	        return option1;
	    }

	    public String getOption2() {
	        return option2;
	    }

	    public String getOption3() {
	        return option3;
	    }

	    public String getOption4() {
	        return option4;
	    }

	    public int getCorrectOption() {
	        return correctOption;
	    }
	}