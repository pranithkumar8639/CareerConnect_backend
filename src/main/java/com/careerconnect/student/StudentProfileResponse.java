package com.careerconnect.student;

public record StudentProfileResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String location,
        String education,
        String skills,
        String bio,
        String resumeOriginalName) {
    public static StudentProfileResponse from(StudentProfile profile) {
        return new StudentProfileResponse(
                profile.getId(), profile.getUser().getFullName(), profile.getUser().getEmail(),
                profile.getPhone(), profile.getLocation(), profile.getEducation(),
                profile.getSkills(), profile.getBio(), profile.getResumeOriginalName());
    }
}
