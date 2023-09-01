package resumeprocessorapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		
		String pdfContent = "Mandar Kore \n"
				+ "(Software Developer) \n"
				+ "Experience \n"
				+ "4 years of experience in developing web based application \n"
				+ "in Java, J2EE web technologies. Good knowledge of object\n"
				+ "oriented programming concepts and several design \n"
				+ "patterns. \n"
				+ "Skills \n"
				+ "Front End Skills: React, React native, Angular \n"
				+ "Back End Skills: Java, adv. java, spring boot\n"
				+ "Database Skills: PostgreSQL, MySQL, Cassandra \n"
				+ "Frameworks: Spring Boot, Spring-Core, Spring-MVC, \n"
				+ "Spring Security, Hibernate. \n"
				+ "Education \n"
				+ "Padmabhushan Vasantraodada Patil Institute of \n"
				+ "Technology, Sangli (MH). \n"
				+ "Bachelor of Engineering, Information Technology\n"
				+ "Relevant Project Experience\n"
				+ "\n"
				+ "1. Project Title: Chef Recruitment Portal Development\n"
				+ "\n"
				+ "Client: A restaurant chain in Pune\n"
				+ "Duration: 5 Months";
		
		Pattern namePattern = Pattern.compile("^(.*?)\\s+\\(.*?\\)", Pattern.DOTALL);
        Matcher nameMatcher = namePattern.matcher(pdfContent);
		
        if (nameMatcher.find()) {
            String name = nameMatcher.group(1).trim();
            System.out.println(name);
        }
        
        Pattern experiencePattern = Pattern.compile("Experience\\s+(.*?)\\s+Skills", Pattern.DOTALL);
        Matcher experienceMatcher = experiencePattern.matcher(pdfContent);
        
        if (experienceMatcher.find()) {
            String experience = experienceMatcher.group(1).trim();
            System.out.println(experience);
        }
        
        Pattern frontendPattern = Pattern.compile("Front End Skills:(.*?)Back End Skills:", Pattern.DOTALL);
        Matcher frontendMatcher = frontendPattern.matcher(pdfContent);
        if (frontendMatcher.find()) {
            String frontendSkills = frontendMatcher.group(1).trim();
            System.out.println(frontendSkills);
        }
        
        Pattern backendPattern = Pattern.compile("Back End Skills:(.*?)Database Skills:", Pattern.DOTALL);
        Matcher backendMatcher = backendPattern.matcher(pdfContent);
        if (backendMatcher.find()) {
            String backendSkills = backendMatcher.group(1).trim();
            System.out.println(backendSkills);
        }

        Pattern databasePattern = Pattern.compile("Database Skills:(.*?)Frameworks:", Pattern.DOTALL);
        Matcher databaseMatcher = databasePattern.matcher(pdfContent);
        if (databaseMatcher.find()) {
            String databaseSkills = databaseMatcher.group(1).trim();
            System.out.println(databaseSkills);
        }

        Pattern frameworksPattern = Pattern.compile("Frameworks:(.*?)Education", Pattern.DOTALL);
        Matcher frameworksMatcher = frameworksPattern.matcher(pdfContent);
        if (frameworksMatcher.find()) {
        	String frameworksSkills = frameworksMatcher.group(1).trim();
        	System.out.println(frameworksSkills);
        }

        Pattern educationPattern = Pattern.compile("Education\\s+(.*?)Relevant Project Experience", Pattern.DOTALL);
        Matcher educationMatcher = educationPattern.matcher(pdfContent);
        
        if (educationMatcher.find()) {
            String educationSkills = educationMatcher.group(1).trim();
            System.out.println(educationSkills);
        }

	}
	
}
