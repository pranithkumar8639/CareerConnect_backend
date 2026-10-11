package com.careerconnect.student;

import com.careerconnect.user.AppUser;
import jakarta.persistence.*;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;

    @Column(length = 30) private String phone;
    @Column(length = 120) private String location;
    @Column(length = 120) private String education;
    @Column(length = 2000) private String skills;
    @Column(length = 2000) private String bio;
    @Column(length = 255) private String resumeOriginalName;
    @Column(length = 80) private String resumeStoredName;

    protected StudentProfile() {}
    public StudentProfile(AppUser user) { this.user = user; }
    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public String getPhone() { return phone; }
    public String getLocation() { return location; }
    public String getEducation() { return education; }
    public String getSkills() { return skills; }
    public String getBio() { return bio; }
    public String getResumeOriginalName() { return resumeOriginalName; }
    public String getResumeStoredName() { return resumeStoredName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setLocation(String location) { this.location = location; }
    public void setEducation(String education) { this.education = education; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setBio(String bio) { this.bio = bio; }
    public void setResumeOriginalName(String name) { this.resumeOriginalName = name; }
    public void setResumeStoredName(String name) { this.resumeStoredName = name; }
}
