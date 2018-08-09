package tech.mangosoft.autolinkedin.db.entity.enums;

public enum Task {

    TASK_DO_NOTHING(0),
    TASK_GRABBING(1),
    TASK_CONNECTION(2);

    private int id;

    Task(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Task getTaskById(int id) throws Exception {
        for (Task e : values()) {
            if (e.id == id) return e;
        }
        throw new Exception("No Task defined with id " + id);
    }
}
