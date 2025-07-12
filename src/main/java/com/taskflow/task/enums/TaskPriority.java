package com.taskflow.task.enums;

public enum TaskPriority {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3);

    private final String displayName;
    private final int orderValue;

    TaskPriority(String displayName, int orderValue) {
        this.displayName = displayName;
        this.orderValue = orderValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrderValue() {
        return orderValue;
    }

    public static TaskPriority fromString(String priority) {
        for (TaskPriority taskPriority : TaskPriority.values()) {
            if (taskPriority.name().equalsIgnoreCase(priority)) {
                return taskPriority;
            }
        }
        throw new IllegalArgumentException("Invalid task priority: " + priority);
    }
}