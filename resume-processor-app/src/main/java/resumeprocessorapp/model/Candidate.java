package resumeprocessorapp.model;

public class Candidate {

	private Integer candidateId;
	
	private String name;
	
	private String email;
	
	private String experience;
	
	private String designation;
	
	private String frontendSkills;
	
	private String backendSkills;
	
	private String databaseSkills;
	
	private String frameworks;
	
	private String education;
	
	

	public Integer getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(Integer candidateId) {
		this.candidateId = candidateId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getFrontendSkills() {
		return frontendSkills;
	}

	public void setFrontendSkills(String frontendSkills) {
		this.frontendSkills = frontendSkills;
	}

	public String getBackendSkills() {
		return backendSkills;
	}

	public void setBackendSkills(String backendSkills) {
		this.backendSkills = backendSkills;
	}

	public String getDatabaseSkills() {
		return databaseSkills;
	}

	public void setDatabaseSkills(String databaseSkills) {
		this.databaseSkills = databaseSkills;
	}

	public String getFrameworks() {
		return frameworks;
	}

	public void setFrameworks(String frameworks) {
		this.frameworks = frameworks;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	@Override
	public String toString() {
		return "Candidate [candidateId=" + candidateId + ", name=" + name + ", email=" + email + ", experience="
				+ experience + ", designation=" + designation + ", frontendSkills=" + frontendSkills
				+ ", backendSkills=" + backendSkills + ", databaseSkills=" + databaseSkills + ", frameworks="
				+ frameworks + ", education=" + education + "]";
	}

	public Candidate(Integer candidateId, String name, String email, String experience, String designation,
			String frontendSkills, String backendSkills, String databaseSkills, String frameworks, String education) {
		super();
		this.candidateId = candidateId;
		this.name = name;
		this.email = email;
		this.experience = experience;
		this.designation = designation;
		this.frontendSkills = frontendSkills;
		this.backendSkills = backendSkills;
		this.databaseSkills = databaseSkills;
		this.frameworks = frameworks;
		this.education = education;
	}

	public Candidate() {
		
	}
	
	
	
	
}
