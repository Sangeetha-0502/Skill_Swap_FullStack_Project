package com.example.skill_swap.skill_swap_project.dtoClasses;

import java.time.LocalDateTime;
import java.util.List;

public class UserData {

    private Long id;
    private String name;
    private String email;
    private String bio;
    private String linkedInUrl;
    private String githubUrl;
    private List<String> certificateUrls;
    private String profilePictureUrl;
   
    // Constructors
    public UserData() {}

    public UserData(Long id, String name, String email, String bio, String linkedInUrl,
                   String githubUrl, List<String> certificateUrls, String profilePictureUrl,
                   LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.linkedInUrl = linkedInUrl;
        this.githubUrl = githubUrl;
        this.certificateUrls = certificateUrls;
        this.profilePictureUrl = profilePictureUrl;
       
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLinkedInUrl() {
        return linkedInUrl;
    }

    public void setLinkedInUrl(String linkedInUrl) {
        this.linkedInUrl = linkedInUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public List<String> getCertificateUrls() {
        return certificateUrls;
    }

    public void setCertificateUrls(List<String> certificateUrls) {
        this.certificateUrls = certificateUrls;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

}
