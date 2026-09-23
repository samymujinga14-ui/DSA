import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

// Program entry point. Main starts the service-centre simulation.
public class Main {
    // Create the controller and start its menu loop.
    public static void main(String[] args) {
        new ServiceCentreSimulation().run();
    }
}

// Controls user interaction and connects menu options to data structures.
class ServiceCentreSimulation {
    // Sample data used to demonstrate the queue, list, statistics, and sorting.
    private final Student[] students = {
        new Student(221045678, "Maria", "Registration", 12),
        new Student(222034512, "Tomas", "Student Card", 5),
        new Student(223041876, "Ndapewa", "Fees", 8),
        new Student(221067341, "Simon", "Documents", 4),
        new Student(224056789, "Anna", "Registration", 10),
        new Student(225034567, "Peter", "Fees", 7)
    };
    // Queue stores students waiting to be served in FIFO order.
    private final Queue queue = new Queue();
    // Linked list stores students after they have been served.
    private final StudentList records = new StudentList();
    // Scanner reads menu choices and values typed by the user.
    private final Scanner scanner = new Scanner(System.in);
    private int servedCount;
    private Student currentStudent;
    private int remainingCount;
    private int actualTime;

    // Repeatedly display the menu, process the selected option, and pause for output.
    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": enqueueStudents(); break;
                    case "2": serveStudent(); break;
                    case "3": queue.displayQueue(); break;
                    case "4": insertRecord(); break;
                    case "5": records.displayStudents(); break;
                    case "6": searchRecord(); break;
                    case "7": deleteRecord(); break;
                    case "8": Statistics.display(serviceTimes()); break;
                    case "9": sortTimes(); break;
                    case "10": sortingExperiment(); break;
                    case "11": postfixDemo(); break;
                    case "12": addManualStudent(); break;
                    case "13": updateRecordServiceType(); break;
                    case "14": insertRecordAtPosition(); break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid option. Enter a number from 0 to 14.");
                }
            } catch (RuntimeException error) {
                System.out.println("Error: " + error.getMessage());
            }
            if (running) {
                System.out.println();
                System.out.println("Operation complete. Press Enter to return to the main menu.");
                scanner.nextLine();
                System.out.println();
            }
        }
        System.out.println("Program ended.");
    }

    private void printMenu() {
        // Each menu number is connected to one operation in the simulation.
        System.out.println("=== Student Service Centre ===");
        System.out.println("1. Enqueue sample students");
        System.out.println("2. Serve next student");
        System.out.println("3. Display queue");
        System.out.println("4. Insert a service record");
        System.out.println("5. Display service records");
        System.out.println("6. Search service record");
        System.out.println("7. Delete service record");
        System.out.println("8. Compute service statistics");
        System.out.println("9. Sort service times");
        System.out.println("10. Compare sorting performance");
        System.out.println("11. Evaluate postfix expression");
        System.out.println("12. Add a student manually");
        System.out.println("13. Update a service record");
        System.out.println("14. Insert a record at a position");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private void enqueueStudents() {
        // Add every sample student to the rear of the waiting queue.
        int added = 0;
        for (Student student : students) {
            if (queue.containsStudent(student.getStudentNo())) {
                continue;
            }
            queue.enqueue(student);
            records.insertStudentIfAbsent(student);
            added++;
        }
        remainingCount = queue.size();
        System.out.println(added + " sample students added. Queue length: " + queue.size()
            + " | Records: " + records.size());
    }

    private void serveStudent() {
        System.out.println("1. Dequeue and serve next student (instant simulation)");
        System.out.println("2. Dequeue and serve next student (real-time countdown)");
        int choice = readInteger("Choose dequeue option: ");
        if (choice != 1 && choice != 2) {
            throw new IllegalArgumentException("Invalid dequeue option.");
        }
        serveNextStudent(choice == 2);
    }

    private void serveNextStudent(boolean realTimeMode) {
        currentStudent = queue.dequeue();
        if (currentStudent == null) {
            remainingCount = 0;
            System.out.println("No students waiting");
            return;
        }

        remainingCount = Math.max(0, remainingCount - 1);
        actualTime = 0;
        System.out.println("Serving: " + currentStudent.getName() + " ("
            + currentStudent.getServiceType() + ")");

        int timeLeft = currentStudent.getEstimatedTime();
        if (realTimeMode) {
            System.out.println("Presentation mode: each simulated minute takes 1 second.");
            System.out.println("Type s and press Enter to skip the countdown.");
            while (timeLeft > 0) {
                System.out.println("Time left: " + timeLeft + " minutes");
                if (skipPressed()) {
                    break;
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    break;
                }
                timeLeft--;
                actualTime++;
            }
        } else {
            actualTime = currentStudent.getEstimatedTime();
            timeLeft = 0;
        }

        servedCount++;
        System.out.println(currentStudent.getName() + " completed. Status: Completed");
        recordSummary(currentStudent, currentStudent.getEstimatedTime(), actualTime);
        System.out.println("Waiting: " + remainingCount + " | Served: " + servedCount
            + " | Records: " + records.size());
    }

    private boolean skipPressed() {
        try {
            if (System.in.available() == 0) {
                return false;
            }
            boolean skip = false;
            int input;
            while (System.in.available() > 0 && (input = System.in.read()) != -1) {
                if (input == 's' || input == 'S') {
                    skip = true;
                }
            }
            return skip;
        } catch (java.io.IOException error) {
            return false;
        }
    }

    private void recordSummary(Student student, int estimated, int actual) {
        int overtime = Math.max(0, actual - estimated);
        System.out.println("Student: " + student.getName()
            + " | Service: " + student.getServiceType()
            + " | Estimated: " + estimated
            + " | Actual: " + actual
            + " | Overtime: " + overtime);
    }

    private void insertRecord() {
        // Insert a selected sample student directly into the service-record list.
        int number = readInteger("Enter student number to record: ");
        Student student = findSampleStudent(number);
        if (student == null) System.out.println("Student not found.");
        else {
            records.insertStudent(student);
            System.out.println("Record inserted. Records: " + records.size());
        }
    }

    private void addManualStudent() {
        // Read a new student from the user and enqueue it for service.
        int number = readInteger("Student number: ");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Service type: ");
        String serviceType = scanner.nextLine().trim();
        int estimatedTime = readInteger("Estimated service time in minutes: ");
        Student student = new Student(number, name, serviceType, estimatedTime);
        queue.enqueue(student);
        records.insertStudentIfAbsent(student);
        remainingCount = queue.size();
        System.out.println("Student added to queue and service records. Queue length: "
            + queue.size() + " | Records: " + records.size());
    }

    private void updateRecordServiceType() {
        // Search the linked list and update the matching student's service type.
        int number = readInteger("Student number to update: ");
        System.out.print("New service type: ");
        String serviceType = scanner.nextLine().trim();
        if (records.updateServiceType(number, serviceType)) {
            System.out.println("Service record updated.");
        } else {
            System.out.println("Student not found.");
        }
    }

    private void insertRecordAtPosition() {
        // Demonstrate insertion at a chosen one-based linked-list position.
        int number = readInteger("Student number: ");
        Student student = findSampleStudent(number);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        int position = readInteger("Position (1-based): ");
        System.out.println(records.insertAtPosition(student, position)
                ? "Record inserted at position " + position + "."
                : "Invalid position. Current record count: " + records.size());
    }

    private void searchRecord() {
        System.out.println("1. Search by name");
        System.out.println("2. Search by student number");
        System.out.println("3. Search by name and student number");
        int choice = readInteger("Choose search option: ");
        int matches;
        switch (choice) {
            case 1:
                System.out.print("Enter student name: ");
                matches = records.displayMatches(scanner.nextLine().trim(), null);
                break;
            case 2:
                int number = readInteger("Enter student number: ");
                matches = records.displayMatches(null, number);
                break;
            case 3:
                System.out.print("Enter student name: ");
                String name = scanner.nextLine().trim();
                int studentNumber = readInteger("Enter student number: ");
                matches = records.displayMatches(name, studentNumber);
                break;
            default:
                throw new IllegalArgumentException("Invalid search option.");
        }
        if (matches == 0) {
            System.out.println("Not Found");
        }
    }

    private void deleteRecord() {
        // Confirm deletion before unlinking the matching record from the list.
        int number = readInteger("Enter student number to delete: ");
        Student student = records.searchStudent(number);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.print("Delete " + student.getName() + "? (y/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }
        boolean deleted = records.deleteStudent(number);
        System.out.println(deleted ? "Record deleted." : "Student not found.");
        System.out.println("Records: " + records.size());
    }

    private void sortTimes() {
        // Sort a copy of the service-time array and compare linear with binary search.
        int[] times = serviceTimes();
        System.out.println("Original: " + Arrays.toString(times));
        System.out.println("1. Selection  2. Insertion  3. Merge  4. Quick");
        int choice = readInteger("Choose sort: ");
        int[] sorted;
        switch (choice) {
            case 1: sorted = Algorithms.selectionSort(times); break;
            case 2: sorted = Algorithms.insertionSort(times); break;
            case 3: sorted = Algorithms.mergeSort(times); break;
            case 4: sorted = Algorithms.quickSort(times); break;
            default: throw new IllegalArgumentException("Invalid sort choice.");
        }
        System.out.println("Sorted: " + Arrays.toString(sorted));
        System.out.println("Linear search for 15: index " + Algorithms.linearSearch(times, 15));
        System.out.println("Binary search for 15: index " + Algorithms.binarySearch(sorted, 15));
        System.out.print("Show trace? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            Algorithms.sortWithTrace(times, choice);
        }
    }

    private void sortingExperiment() {
        // Test four algorithms on several random input sizes.
        int[] sizes = {20, 50, 100, 500};
        int repetitions = 5;
        System.out.println("Each result is the average of " + repetitions + " random runs.");
        for (int size : sizes) {
            System.out.println("Array size: " + size);
            timeSort("Selection sort", size, repetitions, 1);
            timeSort("Insertion sort", size, repetitions, 2);
            timeSort("Merge sort", size, repetitions, 3);
            timeSort("Quick sort", size, repetitions, 4);
        }
    }

    private void timeSort(String name, int size, int repetitions, int choice) {
        // Generate fresh random data and calculate an average nanoTime result.
        Random random = new Random(1000L + size + choice);
        long total = 0;
        for (int run = 0; run < repetitions; run++) {
            int[] data = new int[size];
            for (int i = 0; i < size; i++) data[i] = random.nextInt(10000);
            long start = System.nanoTime();
            if (choice == 1) Algorithms.selectionSort(data);
            if (choice == 2) Algorithms.insertionSort(data);
            if (choice == 3) Algorithms.mergeSort(data);
            if (choice == 4) Algorithms.quickSort(data);
            total += System.nanoTime() - start;
        }
        System.out.println(name + " average: " + total / repetitions + " ns");
    }

    private void postfixDemo() {
        // Read a postfix expression and evaluate it with the custom stack.
        System.out.print("Enter postfix expression, for example 5 6 2 + *: ");
        System.out.println("Result: " + Stack.postfixEvaluation(scanner.nextLine()));
    }

    private Student findSampleStudent(int number) {
        // Find a sample student by student number using a linear scan.
        for (Student student : students) if (student.getStudentNo() == number) return student;
        return null;
    }

    private int[] serviceTimes() {
        // Copy estimated service times into an integer array for processing.
        int[] times = new int[students.length];
        for (int i = 0; i < students.length; i++) times[i] = students[i].getEstimatedTime();
        return times;
    }

    private int readInteger(String prompt) {
        // Keep asking until the user supplies a valid integer.
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException error) {
                System.out.println("Invalid number. Please enter digits only.");
            }
        }
    }
}

// Stores the information associated with one student.
class Student {
    private int studentNo;
    private String name;
    private String serviceType;
    private int estimatedTime;

    // Constructor initializes all student fields.
    Student(int studentNo, String name, String serviceType, int estimatedTime) {
        this.studentNo = studentNo;
        this.name = name;
        this.serviceType = serviceType;
        this.estimatedTime = estimatedTime;
    }

    // Getters and setters allow controlled access to private fields.
    public int getStudentNo() { return studentNo; }
    public void setStudentNo(int studentNo) { this.studentNo = studentNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public int getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(int estimatedTime) { this.estimatedTime = estimatedTime; }

    // Converts a student into readable text for console output.
    @Override
    public String toString() {
        return studentNo + " | " + name + " | " + serviceType + " | " + estimatedTime + " minutes";
    }
}

// Linked queue implementing FIFO: first student in is first student out.
class Queue {
    // Each queue node stores one student and a link to the next node.
    private static class Node {
        Student student;
        Node next;
        Node(Student student) { this.student = student; }
    }
    private Node front; // Oldest student in the queue.
    private Node rear;  // Newest student in the queue.
    private int count;  // Number of students currently waiting.

    public void enqueue(Student student) {
        // FIFO is used because students must be served in arrival order.
        Node node = new Node(student);
        if (rear == null) front = node;
        else rear.next = node;
        rear = node;
        count++;
    }

    public Student dequeue() {
        // Remove the oldest node and repair both ends when the queue becomes empty.
        if (front == null) return null;
        Student student = front.student;
        front = front.next;
        if (front == null) rear = null;
        count--;
        return student;
    }

    // Return the next student without removing that student.
    public Student peek() { return front == null ? null : front.student; }
    // Check whether the queue has no nodes.
    public boolean isEmpty() { return front == null; }
    public int size() { return count; }

    public boolean containsStudent(int number) {
        Node current = front;
        while (current != null) {
            if (current.student.getStudentNo() == number) return true;
            current = current.next;
        }
        return false;
    }

    // Traverse from front to rear and print every waiting student.
    public void displayQueue() {
        if (isEmpty()) {
            System.out.println("Queue Empty");
            return;
        }
        Node current = front;
        while (current != null) {
            System.out.println(current.student);
            current = current.next;
        }
    }
}

// Singly linked list used for flexible service-record storage.
class StudentList {
    // Each list node stores a student and the address of the next node.
    private static class Node {
        Student student;
        Node next;
        Node(Student student) { this.student = student; }
    }
    private Node head;
    private int count;

    public void insertStudent(Student student) {
        insertAtEnd(student);
    }

    public void insertStudentIfAbsent(Student student) {
        if (searchStudent(student.getStudentNo()) == null) {
            insertAtEnd(student);
        }
    }

    // Append a new node after the current final node.
    public boolean insertAtEnd(Student student) {
        Node node = new Node(student);
        if (head == null) head = node;
        else {
            Node current = head;
            while (current.next != null) current = current.next;
            current.next = node;
        }
        count++;
        return true;
    }

    // Link a new node before the current head.
    public boolean insertAtBeginning(Student student) {
        Node node = new Node(student);
        node.next = head;
        head = node;
        count++;
        return true;
    }

    // Positions follow the algorithm's one-based convention.
    public boolean insertAtPosition(Student student, int position) {
        if (position < 1 || position > count + 1) return false;
        if (position == 1) return insertAtBeginning(student);
        Node current = head;
        for (int i = 1; i < position - 1; i++) current = current.next;
        Node node = new Node(student);
        node.next = current.next;
        current.next = node;
        count++;
        return true;
    }

    // Search by student number, then change the stored service type.
    public boolean updateServiceType(int number, String serviceType) {
        Student student = searchStudent(number);
        if (student == null) return false;
        student.setServiceType(serviceType);
        return true;
    }

    public int size() { return count; }

    // Traverse the list until the requested student is found.
    public Student searchStudent(int number) {
        Node current = head;
        while (current != null) {
            if (current.student.getStudentNo() == number) return current.student;
            current = current.next;
        }
        return null;
    }

    public int displayMatches(String name, Integer number) {
        int matches = 0;
        Node current = head;
        while (current != null) {
            Student student = current.student;
            boolean nameMatches = name == null
                || student.getName().equalsIgnoreCase(name);
            boolean numberMatches = number == null
                || student.getStudentNo() == number;
            if (nameMatches && numberMatches) {
                System.out.println(student);
                matches++;
            }
            current = current.next;
        }
        return matches;
    }

    public boolean deleteStudent(int number) {
        // Track the previous node so the matching node can be unlinked safely.
        Node current = head;
        Node previous = null;
        while (current != null) {
            if (current.student.getStudentNo() == number) {
                if (previous == null) head = current.next;
                else previous.next = current.next;
                count--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    // Print every student record from head to the final node.
    public void displayStudents() {
        if (head == null) {
            System.out.println("No service records.");
            return;
        }
        Node current = head;
        while (current != null) {
            System.out.println(current.student);
            current = current.next;
        }
    }
}

// Custom array-based stack. It avoids built-in stack classes and follows LIFO.
class Stack {
    private final double[] values;
    private int top = -1;

    // The expression token count determines a safe maximum stack capacity.
    public Stack(int capacity) {
        values = new double[capacity];
    }

    public void push(double value) {
        // LIFO is required by postfix evaluation: the latest operand is used first.
        if (top == values.length - 1) throw new IllegalStateException("Stack Overflow");
        values[++top] = value;
    }

    // Remove and return the most recently pushed value.
    public double pop() {
        if (isEmpty()) throw new IllegalStateException("Stack Underflow");
        return values[top--];
    }

    // Return the top value without removing it.
    public Double peek() {
        if (isEmpty()) return null;
        return values[top];
    }

    public boolean isEmpty() { return top == -1; }
    public int size() { return top + 1; }

    public static double postfixEvaluation(String expression) {
        // Read tokens left-to-right; operators pop two operands and push one result.
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Postfix expression cannot be empty");
        }
        String[] tokens = expression.trim().split("\\s+");
        Stack stack = new Stack(tokens.length);
        for (String token : tokens) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                if (!isOperator(token)) throw new IllegalArgumentException("Invalid operator: " + token);
                double second = stack.pop();
                double first = stack.pop();
                switch (token) {
                    case "+": stack.push(first + second); break;
                    case "-": stack.push(first - second); break;
                    case "*": stack.push(first * second); break;
                    case "/":
                        if (second == 0) throw new ArithmeticException("Cannot divide by zero");
                        stack.push(first / second);
                        break;
                }
            }
        }
        if (stack.size() != 1) throw new IllegalArgumentException("Invalid postfix expression");
        return stack.pop();
    }

    // Try parsing a token to decide whether it is an operand or an operator.
    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException error) {
            return false;
        }
    }

    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-")
                || token.equals("*") || token.equals("/");
    }
}

// Static utility methods for sorting, searching, tracing, and timing algorithms.
class Algorithms {
    public static int[] selectionSort(int[] input) {
        // Selection sort repeatedly places the smallest remaining value at the front.
        int[] a = Arrays.copyOf(input, input.length);
        for (int i = 0; i < a.length - 1; i++) {
            int smallest = i;
            for (int j = i + 1; j < a.length; j++) if (a[j] < a[smallest]) smallest = j;
            int temp = a[i]; a[i] = a[smallest]; a[smallest] = temp;
        }
        return a;
    }

    public static int[] insertionSort(int[] input) {
        // Insertion sort grows a sorted prefix by inserting each next value.
        int[] a = Arrays.copyOf(input, input.length);
        for (int i = 1; i < a.length; i++) {
            int value = a[i], j = i - 1;
            while (j >= 0 && a[j] > value) { a[j + 1] = a[j]; j--; }
            a[j + 1] = value;
        }
        return a;
    }

    public static int[] mergeSort(int[] input) {
        // Merge sort divides the array, sorts each half, then merges both halves.
        int[] a = Arrays.copyOf(input, input.length);
        mergeSort(a, 0, a.length - 1);
        return a;
    }

    private static void mergeSort(int[] a, int low, int high) {
        if (low >= high) return;
        int middle = (low + high) / 2;
        mergeSort(a, low, middle); mergeSort(a, middle + 1, high);
        int[] result = new int[high - low + 1];
        int left = low, right = middle + 1, index = 0;
        while (left <= middle && right <= high) result[index++] = a[left] <= a[right] ? a[left++] : a[right++];
        while (left <= middle) result[index++] = a[left++];
        while (right <= high) result[index++] = a[right++];
        System.arraycopy(result, 0, a, low, result.length);
    }

    public static int[] quickSort(int[] input) {
        // Quick sort partitions around a pivot and recursively sorts both sides.
        int[] a = Arrays.copyOf(input, input.length);
        quickSort(a, 0, a.length - 1);
        return a;
    }

    public static void sortWithTrace(int[] input, int choice) {
        int[] a = Arrays.copyOf(input, input.length);
        System.out.println("Trace:");
        if (choice == 1) {
            for (int i = 0; i < a.length - 1; i++) {
                int smallest = i;
                for (int j = i + 1; j < a.length; j++) {
                    if (a[j] < a[smallest]) smallest = j;
                }
                int temp = a[i]; a[i] = a[smallest]; a[smallest] = temp;
                System.out.println("Pass " + (i + 1) + ": " + Arrays.toString(a));
            }
        } else if (choice == 2) {
            for (int i = 1; i < a.length; i++) {
                int value = a[i];
                int j = i - 1;
                while (j >= 0 && a[j] > value) { a[j + 1] = a[j]; j--; }
                a[j + 1] = value;
                System.out.println("Pass " + i + ": " + Arrays.toString(a));
            }
        } else if (choice == 3) {
            int[] sorted = mergeSort(a);
            System.out.println("Merged result: " + Arrays.toString(sorted));
        } else if (choice == 4) {
            int[] sorted = quickSort(a);
            System.out.println("Quick-sort result: " + Arrays.toString(sorted));
        } else {
            throw new IllegalArgumentException("Invalid sort choice.");
        }
    }

    private static void quickSort(int[] a, int low, int high) {
        if (low >= high) return;
        int pivot = a[high], smaller = low - 1;
        for (int i = low; i < high; i++) if (a[i] <= pivot) {
            smaller++; int temp = a[smaller]; a[smaller] = a[i]; a[i] = temp;
        }
        int temp = a[smaller + 1]; a[smaller + 1] = a[high]; a[high] = temp;
        int split = smaller + 1;
        quickSort(a, low, split - 1); quickSort(a, split + 1, high);
    }

    // Linear search checks values one by one and works on unsorted arrays.
    public static int linearSearch(int[] values, int target) {
        for (int i = 0; i < values.length; i++) if (values[i] == target) return i;
        return -1;
    }

    // Binary search repeatedly halves a sorted search range.
    public static int binarySearch(int[] values, int target) {
        int low = 0, high = values.length - 1;
        while (low <= high) {
            int middle = (low + high) / 2;
            if (values[middle] == target) return middle;
            if (values[middle] < target) low = middle + 1;
            else high = middle - 1;
        }
        return -1;
    }

}

// Calculates and displays descriptive statistics for service times.
class Statistics {
    public static void display(int[] times) {
    // Compute totals, extremes, median, mode, and time-range distribution.
        if (times.length == 0) {
            System.out.println("No service times available.");
            return;
        }
        int total = 0, highest = times[0], lowest = times[0], aboveTen = 0;
        for (int time : times) {
            total += time;
            if (time > highest) highest = time;
            if (time < lowest) lowest = time;
            if (time > 10) aboveTen++;
        }
        int[] sorted = Algorithms.mergeSort(times);
        double median = sorted.length % 2 == 1
                ? sorted[sorted.length / 2]
                : (sorted[sorted.length / 2 - 1] + sorted[sorted.length / 2]) / 2.0;
        int mode = sorted[0];
        int modeCount = 1;
        int currentCount = 1;
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i] == sorted[i - 1]) currentCount++;
            else currentCount = 1;
            if (currentCount > modeCount) {
                mode = sorted[i];
                modeCount = currentCount;
            }
        }
        int[] distribution = new int[3];
        for (int time : times) {
            if (time <= 5) distribution[0]++;
            else if (time <= 10) distribution[1]++;
            else distribution[2]++;
        }
        System.out.println("Students: " + times.length);
        System.out.println("Total time: " + total + " minutes");
        System.out.println("Average: " + (double) total / times.length + " minutes");
        System.out.println("Median: " + median + " minutes");
        System.out.println("Mode: " + mode + " minutes (" + modeCount + " occurrence(s))");
        System.out.println("Highest: " + highest + " minutes");
        System.out.println("Lowest: " + lowest + " minutes");
        System.out.println("Above 10 minutes: " + aboveTen);
        System.out.println("Distribution: 0-5=" + distribution[0]
                + ", 6-10=" + distribution[1] + ", above 10=" + distribution[2]);
    }
}
