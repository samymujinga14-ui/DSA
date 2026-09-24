# Student Service Centre Simulation

A Java console application that simulates the operation of a student service centre. Students join a waiting line, are served in first-in-first-out (FIFO) order, and have their service information stored in a linked list. The project also demonstrates searching, sorting, postfix expression evaluation, and descriptive statistics.

## Features

- FIFO queue: Enqueue sample students or add students manually, display the waiting queue, and serve the next student.
- Service records: Store student records in a singly linked list.
- Record management: Search by name, student number, or both; insert records; update service types; and delete records.
- Service simulation: Serve students instantly or use a real-time countdown where one simulated minute takes one second.
- Sorting and searching: Use selection, insertion, merge, and quick sort, together with linear and binary search.
- Performance comparison: Compare average sorting times for several randomly generated input sizes.
- Postfix evaluation: Evaluate space-separated arithmetic expressions using a custom array-based stack.
- Statistics: Calculate total, average, median, mode, highest, lowest, and service-time distribution values.

Requirements

- Java Development Kit (JDK) 8 or later
- A terminal or command prompt

Check that Java is installed:

```bash
java -version
javac -version
```


## Running the Program

The primary runnable version is the self-contained `Main.java` file. From the project directory, compile and run it with:

```bash
javac Main.java
java Main
```


On Windows PowerShell, the commands are the same:

```powershell
javac Main.java
java Main
```


To use this project from a remote repository, clone it first and replace the placeholder URL with the repository's actual URL:

```bash
git clone https://github.com/samymujinga14-ui/DSA
cd DSA
javac Main.java
java Main
```


The project is intentionally kept in one self-contained source file, so compile `Main.java` directly.

### Menu Options

| Option | Operation |
| --- | --- |

| 1 | Enqueue the sample students |
| 2 | Serve the next student instantly or with a real-time countdown |
| 3 | Display the waiting queue |
| 4 | Insert a sample student as a service record |
| 5 | Display service records |
| 6 | Search service records by name, student number, or both |
| 7 | Delete a service record |
| 8 | Display service-time statistics |
| 9 | Sort service times and compare search methods |
| 10 | Compare sorting performance |
| 11 | Evaluate a postfix expression |
| 12 | Add a student manually |
| 13 | Update a service record's service type |
| 14 | Insert a record at a selected position |
| 0 | Exit the program |

After most operations, press **Enter** to return to the main menu.

### Postfix Expression Example

Choose option `11` and enter an expression with spaces between tokens:

```text
5 6 2 + *
```


The result is `40` because the expression represents `5 * (6 + 2)`.

### Real-Time Service Example

Choose option `2`, then choose the real-time mode. The program displays the remaining simulated minutes. Enter `s` and press **Enter** to skip the countdown.

## Project Structure

- `Main.java` - Contains the complete program: entry point, menu controller, student model, queue, linked list, stack, algorithms, and statistics classes.

## Data Structures and Algorithms

| Component | Purpose |
| --- | --- |

| Queue | Maintains students waiting for service in FIFO order |
| Singly linked list | Stores and manages service records |
| Array-based stack | Evaluates postfix expressions using LIFO operations |
| Selection sort | Repeatedly selects the smallest remaining value |
| Insertion sort | Builds a sorted portion one value at a time |
| Merge sort | Divides, sorts, and merges array sections |
| Quick sort | Partitions values around a pivot |
| Linear search | Searches values sequentially |
| Binary search | Searches a sorted array by repeatedly halving the range |

Sample Students

The built-in sample data contains six students with different service types and estimated service times. Use menu option `1` to add them to the queue and service-record list.

## Coursework

- **Developed by:**
-                   226141608 Samy Mujinga Wa pelekoni
-                   226034755 Immanuel Louw
-                   226133745 Oscar Mayumbelo
-                   226008053 Tavey Ngenokesho
-                   226051625 Ryan Kamolakamwe
-               
- **Course:** Computer Science - Data Structures & Algorithms
- **Institution:** NUST
