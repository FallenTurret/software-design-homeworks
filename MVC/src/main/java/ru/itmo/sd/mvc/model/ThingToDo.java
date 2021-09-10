package ru.itmo.sd.mvc.model;

public class ThingToDo {
    private String name;
    private boolean done = false;

    public ThingToDo() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
