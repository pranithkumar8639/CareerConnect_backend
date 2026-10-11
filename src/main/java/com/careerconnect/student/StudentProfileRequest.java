package com.careerconnect.student;

public record StudentProfileRequest(
        String phone,
        String location,
        String education,
        String skills,
        String bio) {}
