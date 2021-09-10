package ru.itmo.sd.mvc.model;

import java.util.ArrayList;
import java.util.List;

public class ListOfThingsToDo {
    private String name;
    private List<ThingToDo> list = new ArrayList<>();

    public ListOfThingsToDo() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ThingToDo> getList() {
        return list;
    }

    public void setList(List<ThingToDo> list) {
        this.list = list;
    }
}
