package com.poli.lms;

public class Member {
    public int mid;
    public String name;
    public String gender;
    public String department;
    public int numberBorrowed;

    public Member(int mid, String name, String gender, String department, int numberBorrowed) {
        this.mid = mid;
        this.name = name;
        this.gender = gender;
        this.department = department;
        this.numberBorrowed = numberBorrowed;
    }

    public String getDepartment() {
        return department;
    }

    public String getGender() {
        return gender;
    }

    public int getMid() {
        return mid;
    }

    public String getName() {
        return name;
    }

    public int getNumberBorrowed() {
        return numberBorrowed;
    }
}
