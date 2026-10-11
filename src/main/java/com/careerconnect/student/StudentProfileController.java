package com.careerconnect.student;

import com.careerconnect.user.AppUser;
import com.careerconnect.user.Role;
import com.careerconnect.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/student/profile")
public class StudentProfileController {
    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final Path resumeDirectory;

    public StudentProfileController(UserRepository userRepository,
            StudentProfileRepository profileRepository,
            @Value("${app.upload-dir:uploads/resumes}") String uploadDir) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.resumeDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @GetMapping
    public StudentProfileResponse getProfile(Authentication authentication) {
        return StudentProfileResponse.from(getOrCreate(authentication.getName()));
    }

    @PutMapping
    public StudentProfileResponse saveProfile(@RequestBody StudentProfileRequest request,
            Authentication authentication) {
        StudentProfile profile = getOrCreate(authentication.getName());
        profile.setPhone(clean(request.phone(), 30));
        profile.setLocation(clean(request.location(), 120));
        profile.setEducation(clean(request.education(), 120));
        profile.setSkills(clean(request.skills(), 2000));
        profile.setBio(clean(request.bio(), 2000));
        return StudentProfileResponse.from(profileRepository.save(profile));
    }

    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StudentProfileResponse uploadResume(@RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a resume file first.");
        }
        if (file.getSize() > 5L * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Resume must be 5 MB or smaller.");
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "resume" : file.getOriginalFilename());
        String lower = original.toLowerCase(Locale.ROOT);
        if (!(lower.endsWith(".pdf") || lower.endsWith(".doc") || lower.endsWith(".docx"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF, DOC, or DOCX files are allowed.");
        }
        Files.createDirectories(resumeDirectory);
        String stored = UUID.randomUUID() + extension(lower);
        Path target = resumeDirectory.resolve(stored).normalize();
        if (!target.startsWith(resumeDirectory)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file name.");
        }
        file.transferTo(target);
        StudentProfile profile = getOrCreate(authentication.getName());
        if (profile.getResumeStoredName() != null) {
            Files.deleteIfExists(resumeDirectory.resolve(profile.getResumeStoredName()).normalize());
        }
        profile.setResumeOriginalName(original);
        profile.setResumeStoredName(stored);
        return StudentProfileResponse.from(profileRepository.save(profile));
    }

    @GetMapping("/resume")
    public ResponseEntity<Resource> downloadResume(Authentication authentication) {
        StudentProfile profile = getOrCreate(authentication.getName());
        if (profile.getResumeStoredName() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No resume has been uploaded yet.");
        }
        Path path = resumeDirectory.resolve(profile.getResumeStoredName()).normalize();
        if (!path.startsWith(resumeDirectory) || !Files.exists(path)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume file was not found.");
        }
        Resource resource = new FileSystemResource(path);
        String safeName = profile.getResumeOriginalName() == null ? "resume" : profile.getResumeOriginalName().replace("\"", "");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(safeName).build().toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private StudentProfile getOrCreate(String email) {
        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User account not found."));
        if (user.getRole() != Role.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only student accounts can manage a student profile.");
        }
        return profileRepository.findByUser_EmailIgnoreCase(email)
                .orElseGet(() -> profileRepository.save(new StudentProfile(user)));
    }

    private String clean(String value, int max) {
        if (value == null) return null;
        String trimmed = value.trim();
        if (trimmed.length() > max) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One of the profile fields is too long.");
        }
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String extension(String name) {
        if (name.endsWith(".docx")) return ".docx";
        if (name.endsWith(".doc")) return ".doc";
        return ".pdf";
    }
}
