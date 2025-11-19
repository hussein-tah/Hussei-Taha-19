package hussein19;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        ExamSystem system = new ExamSystem();
        system.addDemoData();

        boolean running = true;
        while (running) {
            System.out.println("\n=== Start Menu ===");
            System.out.println("1. Login as Student");
            System.out.println("2. Login as Teacher");
            System.out.println("3. Register Student");
            System.out.println("4. Register Teacher");
            System.out.println("5. Exit");
            System.out.print("Choose: ");
            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    studentLogin(system, input);
                    break;
                case "2":
                    teacherLogin(system, input);
                    break;
                case "3":
                    registerStudent(system, input);
                    break;
                case "4":
                    registerTeacher(system, input);
                    break;
                case "5":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }

        input.close();
    }

    private static void studentLogin(ExamSystem system, Scanner input) {
        System.out.print("Student ID: ");
        String id = input.nextLine().trim();
        System.out.print("Password: ");
        String pass = input.nextLine().trim();
        ExamSystem.Student s = system.loginStudent(id, pass);
        if (s != null) {
            System.out.println("✔ Login successful. Welcome, " + s.getName() + "!");
            studentSession(system, s, input);
        } else {
            System.out.println("❌ Invalid Student ID or Password.");
        }
    }

    private static void teacherLogin(ExamSystem system, Scanner input) {
        System.out.print("Teacher ID: ");
        String id = input.nextLine().trim();
        System.out.print("Password: ");
        String pass = input.nextLine().trim();
        ExamSystem.Teacher t = system.loginTeacher(id, pass);
        if (t != null) {
            System.out.println("✔ Login successful. Welcome, " + t.getName() + "!");
            teacherSession(system, t, input);
        } else {
            System.out.println("❌ Invalid Teacher ID or Password.");
        }
    }

    private static void registerStudent(ExamSystem system, Scanner input) {
        System.out.print("New Student ID: ");
        String id = input.nextLine().trim();
        System.out.print("Name: ");
        String name = input.nextLine().trim();
        System.out.print("Password: ");
        String pass = input.nextLine().trim();
        boolean ok = system.registerStudent(id, name, pass);
        System.out.println(ok ? "✔ Student registered." : "❌ ID already exists.");
    }

    private static void registerTeacher(ExamSystem system, Scanner input) {
        System.out.print("New Teacher ID: ");
        String id = input.nextLine().trim();
        System.out.print("Name: ");
        String name = input.nextLine().trim();
        System.out.print("Password: ");
        String pass = input.nextLine().trim();
        boolean ok = system.registerTeacher(id, name, pass);
        System.out.println(ok ? "✔ Teacher registered." : "❌ ID already exists.");
    }

    // ----- Student session -----
    private static void studentSession(ExamSystem system, ExamSystem.Student s, Scanner input) {
        boolean session = true;
        while (session) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. View Info");
            System.out.println("2. Show Exams");
            System.out.println("3. Take Exam");
            System.out.println("4. View My Results");
            System.out.println("5. Logout");
            System.out.print("Choose: ");
            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    s.displayInfo();
                    break;
                case "2":
                    system.showAllExams();
                    break;
                case "3":
                    if (system.getExamsCount() == 0) {
                        System.out.println("No exams available.");
                        break;
                    }
                    system.showAllExams();
                    System.out.print("Enter exam index to take: ");
                    try {
                        int idx = Integer.parseInt(input.nextLine().trim());
                        ExamSystem.WrittenExam ex = system.getExamAt(idx);
                        if (ex != null) s.takeExam(ex, input);
                        else System.out.println("Invalid index.");
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid number.");
                    }
                    break;
                case "4":
                    System.out.println("=== My Results ===");
                    s.getExamResults().forEach((examId, score) ->
                            System.out.println("Exam: " + examId + " | Score: " + score));
                    break;
                case "5":
                    session = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ----- Teacher session -----
    private static void teacherSession(ExamSystem system, ExamSystem.Teacher t, Scanner input) {
        boolean session = true;
        while (session) {
            System.out.println("\n--- Teacher Menu ---");
            System.out.println("1. View Info");
            System.out.println("2. Show Exams");
            System.out.println("3. Create Exam");
            System.out.println("4. Logout");
            System.out.print("Choose: ");
            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    t.displayInfo();
                    break;
                case "2":
                    system.showAllExams();
                    break;
                case "3":
                    System.out.print("Exam ID: ");
                    String eid = input.nextLine().trim();
                    System.out.print("Exam Name: ");
                    String ename = input.nextLine().trim();
                    System.out.print("Total Marks: ");
                    int marks = 0;
                    try {
                        marks = Integer.parseInt(input.nextLine().trim());
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid number for marks.");
                        break;
                    }
                    ExamSystem.WrittenExam exam = t.createExam(eid, ename, marks);
                    System.out.println("You must add at least 2 MCQ questions.");

                    // collect at least 2 questions
                    for (int i = 1; i <= 2; i++) {
                        System.out.println("\nQuestion " + i + ":");
                        System.out.print("Enter question text: ");
                        String qtext = input.nextLine();
                        String[] opts = new String[4];
                        for (int j = 0; j < 4; j++) {
                            System.out.print("Option " + (j+1) + ": ");
                            opts[j] = input.nextLine();
                        }
                        int correct = 0;
                        while (true) {
                            System.out.print("Correct option (1-4): ");
                            try {
                                correct = Integer.parseInt(input.nextLine().trim());
                                if (correct >= 1 && correct <= 4) break;
                                else System.out.println("Enter 1..4.");
                            } catch (NumberFormatException exx) {
                                System.out.println("Invalid number.");
                            }
                        }
                        exam.addQuestion(new ExamSystem.Question(qtext, opts, correct-1));
                    }

                    // optional: add more
                    System.out.print("Add more questions? (yes/no): ");
                    if (input.nextLine().trim().equalsIgnoreCase("yes")) {
                        while (true) {
                            System.out.println("\nEnter question text: ");
                            String qtext = input.nextLine();
                            String[] opts = new String[4];
                            for (int j = 0; j < 4; j++) {
                                System.out.print("Option " + (j+1) + ": ");
                                opts[j] = input.nextLine();
                            }
                            int correct = 0;
                            while (true) {
                                System.out.print("Correct option (1-4): ");
                                try {
                                    correct = Integer.parseInt(input.nextLine().trim());
                                    if (correct >= 1 && correct <= 4) break;
                                    else System.out.println("Enter 1..4.");
                                } catch (NumberFormatException exx) {
                                    System.out.println("Invalid number.");
                                }
                            }
                            exam.addQuestion(new ExamSystem.Question(qtext, opts, correct-1));
                            System.out.print("Add another? (yes/no): ");
                            if (!input.nextLine().trim().equalsIgnoreCase("yes")) break;
                        }
                    }

                    system.addExam(exam);
                    System.out.println("Exam created successfully.");
                    break;
                case "4":
                    session = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
