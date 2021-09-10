package ru.itmo.sd.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itmo.sd.mvc.dao.ThingsToDoDao;
import ru.itmo.sd.mvc.model.ListOfThingsToDo;
import ru.itmo.sd.mvc.model.ThingToDo;

import java.util.List;

@Controller
public class ThingsToDoController {
    private final ThingsToDoDao thingsToDoDao;

    public ThingsToDoController(ThingsToDoDao thingsToDoDao) {
        this.thingsToDoDao = thingsToDoDao;
    }

    @GetMapping(value = "/get-lists")
    public String getLists(ModelMap map) {
        prepareModelMap(map, thingsToDoDao.getLists());
        return "index";
    }

    @PostMapping(value = "/add-list")
    public String addList(@ModelAttribute("list") ListOfThingsToDo list) {
        thingsToDoDao.addList(list);
        return "redirect:/get-lists";
    }

    @GetMapping(value = "/delete-list/{index}")
    public String deleteList(@PathVariable int index) {
        thingsToDoDao.deleteList(index);
        return "redirect:/get-lists";
    }

    @PostMapping(value = "/add-thing-to-do/{index}")
    public String addThingToDo(@PathVariable int index, @ModelAttribute("thingToDo") ThingToDo thingToDo) {
        thingsToDoDao.addThingToDo(index, thingToDo);
        return "redirect:/get-lists";
    }

    @GetMapping(value = "/mark-as-done/{listIndex}/{thingIndex}")
    public String markAsDone(@PathVariable int listIndex, @PathVariable int thingIndex) {
        thingsToDoDao.markAsDone(listIndex, thingIndex);
        return "redirect:/get-lists";
    }

    private void prepareModelMap(ModelMap map, List<ListOfThingsToDo> lists) {
        map.addAttribute("lists", lists);
        map.addAttribute("list", new ListOfThingsToDo());
        map.addAttribute("thingToDo", new ThingToDo());
    }
}
