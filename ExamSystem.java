package hussein110;

import java.util.ArrayList;
import java.util.List;

public class ExamSystem {

    private List<User> users = new ArrayList<>();
    private List<WrittenExam> exams = new ArrayList<>();

    // ===== Question Class (MCQ) =====
    public static class Question {
        private String text;
        private String[] options; // length 4
        private int correctIndex; // 0..3

        public Question(String text, String[] options, int correctIndex) {
            this.text = text;
            this.options = options;
            this.correctIndex = correctIndex;
        }

        public String getText() { return text; }
        public String[] getOptions() { return options; }
        public int getCorrectIndex() { return correctIndex; }

        public void display() {
            System.out.println(text);
            for (int i = 0; i < options.length; i++) {
                System.out.println((i+1) + ") " + options[i]);
            }
        }

        public boolean isCorrect(int chosenOneBased) {
            return (chosenOneBased - 1) == correctIndex;
        }
    }

    // ===== Exam base class =====
    public abstract class Exam {
        protected String examId;
        protected String examName;
        protected int totalMarks;

        public Exam(String examId, String examName, int totalMarks) {
            this.examId = examId;
            this.examName = examName;
            this.totalMarks = totalMarks;
        }

        public String getExamId() { return examId; }
        public String getExamName() { return examName; }
        public int getTotalMarks() { return totalMarks; }

        public abstract void showInfo();
    }

    // ===== WrittenExam (contains MCQ questions list) =====
    public class WrittenExam extends Exam {
        private List<Question> questions = new ArrayList<>();

        public WrittenExam(String examId, String examName, int totalMarks) {
            super(examId, examName, totalMarks);
        }

        public void addQuestion(Question q) {
            questions.add(q);
        }

        public List<Question> getQuestions() {
            return questions;
        }

        @Override
        public void showInfo() {
            System.out.println("----- Written Exam -----");
            System.out.println("Exam ID    : " + examId);
            System.out.println("Exam Name  : " + examName);
            System.out.println("Total Marks: " + totalMarks);
            System.out.println("Questions  : " + questions.size());
        }
    }

    // ===== User management =====
    public boolean addUser(User u) {
        if (getUserById(u.getId()) != null) return false;
        users.add(u);
        return true;
    }

    public User getUserById(String id) {
        for (User u : users) if (u.getId().equals(id)) return u;
        return null;
    }

    public Student loginStudent(String id, String password) {
        User u = getUserById(id);
        if (u instanceof Student && u.checkPassword(password)) return (Student) u;
        return null;
    }

    public Teacher loginTeacher(String id, String password) {
        User u = getUserById(id);
        if (u instanceof Teacher && u.checkPassword(password)) return (Teacher) u;
        return null;
    }

    public boolean registerStudent(String id, String name, String password) {
        if (getUserById(id) != null) return false;
        users.add(new Student(id, name, password));
        return true;
    }

    public boolean registerTeacher(String id, String name, String password) {
        if (getUserById(id) != null) return false;
        users.add(new Teacher(id, name, password));
        return true;
    }

    // ===== Exams management =====
    public void addExam(WrittenExam e) { exams.add(e); }

    public void showAllExams() {
        if (exams.isEmpty()) {
            System.out.println("No exams available.");
            return;
        }
        System.out.println("=== List of Exams ===");
        for (int i = 0; i < exams.size(); i++) {
            System.out.println("[" + i + "] " + exams.get(i).getExamName()
                    + " (ID: " + exams.get(i).getExamId() + ", Q: " + exams.get(i).getQuestions().size() + ")");
        }
    }

    public WrittenExam getExamAt(int index) {
        if (index >= 0 && index < exams.size()) return exams.get(index);
        return null;
    }

    public int getExamsCount() { return exams.size(); }

    // ===== Student class =====
    public class Student extends User {
        public Student(String id, String name, String password) { super(id, name, password); }

        /**
         * Take the exam: asks all MCQ questions, requires exam has at least 2 questions.
         * Records result in the user's examResults map (score = number of correct).
         */
        public void takeExam(WrittenExam exam, java.util.Scanner input) {
            if (exam == null) {
                System.out.println("Invalid exam.");
                return;
            }
            List<Question> qs = exam.getQuestions();
            if (qs.size() < 2) {
                System.out.println("This exam does not have enough questions (minimum 2).");
                return;
            }

            System.out.println("\n=== Starting Exam: " + exam.getExamName() + " ===");
            int correctCount = 0;
            for (int i = 0; i < qs.size(); i++) {
                Question q = qs.get(i);
                System.out.println("\nQuestion " + (i+1) + ":");
                q.display();
                int ans = 0;
                while (true) {
                    System.out.print("Your answer (1-4): ");
                    String line = input.nextLine();
                    try {
                        ans = Integer.parseInt(line);
                        if (ans >= 1 && ans <= 4) break;
                        else System.out.println("Please enter a number between 1 and 4.");
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid input. Enter 1, 2, 3 or 4.");
                    }
                }
                if (q.isCorrect(ans)) {
                    System.out.println("✔ Correct!");
                    correctCount++;
                } else {
                    System.out.println("✘ Wrong! Correct answer: " + (q.getOptions()[q.getCorrectIndex()]));
                }
            }

            System.out.println("\n=== Exam Finished ===");
            System.out.println("Score: " + correctCount + " / " + qs.size());
            // record result (store number correct)
            recordResult(exam.getExamId(), correctCount);
        }
    }

    // ===== Teacher class =====
    public class Teacher extends User {
        public Teacher(String id, String name, String password) { super(id, name, password); }

        /**
         * Create a blank WrittenExam instance; teacher will add questions in Main flow.
         */
        public WrittenExam createExam(String examId, String examName, int totalMarks) {
            return new WrittenExam(examId, examName, totalMarks);
        }
    }

    // ===== Demo data (Beginner Java MCQ) =====
    public void addDemoData() {
        if (getUserById("S001") == null) addUser(new Student("S001", "Hussein", "1234"));
        if (getUserById("S002") == null) addUser(new Student("S002", "Ali", "abcd"));
        if (getUserById("T001") == null) addUser(new Teacher("T001", "Mr.Ali", "admin"));

        // Add one demo exam (beginner Java MCQ) if not present
        if (getExamsCount() == 0) {
            WrittenExam ex = new WrittenExam("E001", "Java Basics - Quiz 1", 100);
            // Question 1
            ex.addQuestion(new Question(
                    "What does System.out.println() do?",
                    new String[] {"Read input", "Print output", "Declare a variable", "Stop the program"},
                    1)); // correctIndex = 1 -> option 2
            // Question 2
            ex.addQuestion(new Question(
                    "Which keyword is used to create a class in Java?",
                    new String[] {"func", "class", "define", "object"},
                    1));
            // Question 3
            ex.addQuestion(new Question(
                    "Which data type is used for whole numbers?",
                    new String[] {"double", "String", "int", "boolean"},
                    2));
            addExam(ex);
        }
    }
}
