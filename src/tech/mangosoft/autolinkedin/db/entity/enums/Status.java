package tech.mangosoft.autolinkedin.db.entity.enums;

public enum Status {


    STATUS_NEW(0),
    STATUS_IN_PROGRESS(1),
    STATUS_SUSPENDED(2),
    STATUS_FINISHED(16),
    STATUS_ERROR(32);

    private int id;

    Status(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Status getStatusById(int id) throws Exception {
        for (Status e : values()) {
            if (e.id == id) return e;
        }
        throw new Exception("No Task defined with id " + id);
    }
}
