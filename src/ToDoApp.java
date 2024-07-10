import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ToDoApp extends JFrame {
    private final DefaultListModel<String> taskListModel;
    private final JList<String> taskList;
    private final JTextField taskField;
    private final JButton addButton, updateButton, deleteButton, completeButton, viewAllButton, viewCompletedButton,
            viewPendingButton, themeButton;
    private final List<Task> allTasks;
    private Color backgroundColor;
    private Color foregroundColor;
    private Color buttonColor;
    private Color selectionColor;
    private Color inputFieldColor;

    public ToDoApp() {
        setTitle("To-Do List Application");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Default theme
        setTheme(Color.DARK_GRAY, Color.WHITE, new Color(75, 110, 175), new Color(75, 110, 175), new Color(43, 43, 43));

        // Task input field
        taskField = new JTextField();
        taskField.setBackground(inputFieldColor);
        taskField.setForeground(foregroundColor);
        taskField.setCaretColor(foregroundColor);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(taskField, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);

        // Task list
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setBackground(inputFieldColor);
        taskList.setForeground(foregroundColor);
        taskList.setSelectionBackground(selectionColor);
        add(new JScrollPane(taskList), BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        addButton = createButton("Add");
        updateButton = createButton("Update");
        deleteButton = createButton("Delete");
        completeButton = createButton("Complete");
        viewAllButton = createButton("View All");
        viewCompletedButton = createButton("View Completed");
        viewPendingButton = createButton("View Pending");
        themeButton = createButton("Change Theme");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(viewAllButton);
        buttonPanel.add(viewCompletedButton);
        buttonPanel.add(viewPendingButton);
        buttonPanel.add(themeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Initialize the list of all tasks
        allTasks = new ArrayList<>();

        // Load tasks from file
        loadTasks();

        // Add action listeners
        addButton.addActionListener(new AddTaskListener());
        updateButton.addActionListener(new UpdateTaskListener());
        deleteButton.addActionListener(new DeleteTaskListener());
        completeButton.addActionListener(new CompleteTaskListener());
        viewAllButton.addActionListener(new ViewAllTasksListener());
        viewCompletedButton.addActionListener(new ViewCompletedTasksListener());
        viewPendingButton.addActionListener(new ViewPendingTasksListener());
        themeButton.addActionListener(new ThemeChangeListener());
    }

    private void setTheme(Color bg, Color fg, Color btn, Color sel, Color input) {
        backgroundColor = bg;
        foregroundColor = fg;
        buttonColor = btn;
        selectionColor = sel;
        inputFieldColor = input;
        getContentPane().setBackground(bg);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor);
        button.setForeground(foregroundColor);
        return button;
    }

    private void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";;");
                String taskName = parts[0];
                boolean isCompleted = Boolean.parseBoolean(parts[1]);
                Task task = new Task(taskName, isCompleted);
                allTasks.add(task);
                taskListModel.addElement(task.toString());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading tasks from file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveTasks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("tasks.txt"))) {
            for (Task task : allTasks) {
                writer.println(task.toFileString());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving tasks to file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTaskList(List<Task> tasks) {
        taskListModel.clear();
        for (Task task : tasks) {
            taskListModel.addElement(task.toString());
        }
    }

    // Action listeners
    class AddTaskListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String task = taskField.getText().trim();
            if (!task.isEmpty()) {
                Task newTask = new Task(task, false);
                allTasks.add(newTask);
                taskListModel.addElement(newTask.toString());
                taskField.setText("");
                saveTasks();
                JOptionPane.showMessageDialog(ToDoApp.this, "Task added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(ToDoApp.this, "Task cannot be empty!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class UpdateTaskListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                String newTaskName = taskField.getText().trim();
                if (!newTaskName.isEmpty()) {
                    Task updatedTask = new Task(newTaskName, allTasks.get(selectedIndex).isCompleted());
                    allTasks.set(selectedIndex, updatedTask);
                    taskListModel.set(selectedIndex, updatedTask.toString());
                    taskField.setText("");
                    saveTasks();
                    JOptionPane.showMessageDialog(ToDoApp.this, "Task updated successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(ToDoApp.this, "Task cannot be empty!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(ToDoApp.this, "Select a task to update!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class DeleteTaskListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                allTasks.remove(selectedIndex);
                taskListModel.remove(selectedIndex);
                saveTasks();
                JOptionPane.showMessageDialog(ToDoApp.this, "Task deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(ToDoApp.this, "Select a task to delete!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class CompleteTaskListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                Task task = allTasks.get(selectedIndex);
                if (!task.isCompleted()) {
                    task.setCompleted(true);
                    taskListModel.set(selectedIndex, task.toString());
                    saveTasks();
                    JOptionPane.showMessageDialog(ToDoApp.this, "Task marked as completed!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(ToDoApp.this, "Task is already marked as completed!", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(ToDoApp.this, "Select a task to mark as complete!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class ViewAllTasksListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (allTasks.isEmpty()) {
                JOptionPane.showMessageDialog(ToDoApp.this, "No tasks to display!", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                updateTaskList(allTasks);
                JOptionPane.showMessageDialog(ToDoApp.this, "Displaying all tasks.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    class ViewCompletedTasksListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            List<Task> completedTasks = new ArrayList<>();
            for (Task task : allTasks) {
                if (task.isCompleted()) {
                    completedTasks.add(task);
                }
            }
            if (completedTasks.isEmpty()) {
                JOptionPane.showMessageDialog(ToDoApp.this, "No completed tasks to display!", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                updateTaskList(completedTasks);
                JOptionPane.showMessageDialog(ToDoApp.this, "Displaying completed tasks.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    class ViewPendingTasksListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            List<Task> pendingTasks = new ArrayList<>();
            for (Task task : allTasks) {
                if (!task.isCompleted()) {
                    pendingTasks.add(task);
                }
            }
            if (pendingTasks.isEmpty()) {
                JOptionPane.showMessageDialog(ToDoApp.this, "No pending tasks to display!", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                updateTaskList(pendingTasks);
                JOptionPane.showMessageDialog(ToDoApp.this, "Displaying pending tasks.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    class ThemeChangeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object[] options = { "Dark Theme", "Light Theme", "Soft Rose Theme" };
            int choice = JOptionPane.showOptionDialog(ToDoApp.this,
                    "Choose a theme",
                    "Theme Selector",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0:
                    setTheme(Color.DARK_GRAY, Color.WHITE, new Color(75, 110, 175), new Color(75, 110, 175),
                            new Color(43, 43, 43));
                    break;
                case 1:
                    setTheme(Color.WHITE, Color.BLACK, new Color(255, 255, 255, 255), new Color(250, 250, 250),
                            Color.LIGHT_GRAY);
                    break;
                case 2:
                    setTheme(new Color(255, 182, 193), Color.BLACK, new Color(255, 192, 203), new Color(255, 105, 180),
                            new Color(255, 240, 245));
                    break;
                default:
                    break;
            }

            // Apply the new theme to all components
            taskField.setBackground(inputFieldColor);
            taskField.setForeground(foregroundColor);
            taskField.setCaretColor(foregroundColor);

            taskList.setBackground(inputFieldColor);
            taskList.setForeground(foregroundColor);
            taskList.setSelectionBackground(selectionColor);

            addButton.setBackground(buttonColor);
            addButton.setForeground(foregroundColor);
            updateButton.setBackground(buttonColor);
            updateButton.setForeground(foregroundColor);
            deleteButton.setBackground(buttonColor);
            deleteButton.setForeground(foregroundColor);
            completeButton.setBackground(buttonColor);
            completeButton.setForeground(foregroundColor);
            viewAllButton.setBackground(buttonColor);
            viewAllButton.setForeground(foregroundColor);
            viewCompletedButton.setBackground(buttonColor);
            viewCompletedButton.setForeground(foregroundColor);
            viewPendingButton.setBackground(buttonColor);
            viewPendingButton.setForeground(foregroundColor);
            themeButton.setBackground(buttonColor);
            themeButton.setForeground(foregroundColor);

            getContentPane().setBackground(backgroundColor);

        }
    }

    // Task class to store task information
    static class Task {
        private final String name;
        private boolean isCompleted;

        public Task(String name, boolean isCompleted) {
            this.name = name;
            this.isCompleted = isCompleted;
        }

        public String getName() {
            return name;
        }

        public boolean isCompleted() {
            return isCompleted;
        }

        public void setCompleted(boolean completed) {
            isCompleted = completed;
        }

        @Override
        public String toString() {
            return name + (isCompleted ? " (Completed)" : "");
        }

        public String toFileString() {
            return name + ";;" + isCompleted;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ToDoApp().setVisible(true));
}
}