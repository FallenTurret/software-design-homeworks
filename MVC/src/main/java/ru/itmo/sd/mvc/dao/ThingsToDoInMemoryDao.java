package ru.itmo.sd.mvc.dao;

import ru.itmo.sd.mvc.model.ListOfThingsToDo;
import ru.itmo.sd.mvc.model.ThingToDo;

import java.util.LinkedList;
import java.util.List;

public class ThingsToDoInMemoryDao implements ThingsToDoDao {
    private final LinkedList<ListOfThingsToDo> lists = new LinkedList<>();

    @Override
    public List<ListOfThingsToDo> getLists() {
        return List.copyOf(lists);
    }

    @Override
    public void addList(ListOfThingsToDo list) {
        var copy = new ListOfThingsToDo();
        copy.setName(list.getName());
        lists.add(copy);
    }

    @Override
    public void deleteList(int listIndex) {
        lists.remove(listIndex);
    }

    @Override
    public void addThingToDo(int listIndex, ThingToDo thingToDo) {
        var copy = new ThingToDo();
        copy.setName(thingToDo.getName());
        copy.setDone(thingToDo.isDone());
        lists.get(listIndex).getList().add(copy);
    }

    @Override
    public void markAsDone(int listIndex, int thingToDoIndex) {
        lists.get(listIndex).getList().get(thingToDoIndex).setDone(true);
    }
}
