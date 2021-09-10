package ru.itmo.sd.mvc.dao;

import ru.itmo.sd.mvc.model.ListOfThingsToDo;
import ru.itmo.sd.mvc.model.ThingToDo;

import java.util.List;

public interface ThingsToDoDao {
    List<ListOfThingsToDo> getLists();
    void addList(ListOfThingsToDo list);
    void deleteList(int listIndex);
    void addThingToDo(int listIndex, ThingToDo thing);
    void markAsDone(int listIndex, int thingToDoIndex);
}
