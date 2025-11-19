package hussein19;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String id;
    private String name;
    private String password;
    private int loginAttempts;
    private Map<String, Integer> examResults; // examId -> score

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        setPassword(password);
        this.loginAttempts = 0;
        this.examResults = new HashMap<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getLoginAttempts() { return loginAttempts; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("Password cannot be empty.");
        this.password = password;
    }

    public boolean checkPassword(String inputPassword) {
        if (this.password.equals(inputPassword)) {
            loginAttempts = 0;
            return true;
        } else {
            loginAttempts++;
            return false;
        }
    }

    public void displayInfo() {
        System.out.println("==== User Information ====");
        System.out.println("User ID     : " + id);
        System.out.println("Name        : " + name);
        System.out.println("Login Fails : " + loginAttempts);
    }

    public void recordResult(String examId, int score) {
        examResults.put(examId, score);
    }

    public Map<String, Integer> getExamResults() {
        return examResults;
    }

    @Override
    public String toString() {
        return "User{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
    }
}
