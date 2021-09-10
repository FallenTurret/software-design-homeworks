package ru.itmo.sd.mvc.dao;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import ru.itmo.sd.mvc.model.ListOfThingsToDo;
import ru.itmo.sd.mvc.model.ThingToDo;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ThingsToDoJdbcDao extends JdbcDaoSupport implements ThingsToDoDao {
    public ThingsToDoJdbcDao(DataSource dataSource) {
        super();
        setDataSource(dataSource);
        createTables();
    }

    private void createTables() {
        var template = getJdbcTemplate();
        String sql = "CREATE TABLE IF NOT EXISTS Lists" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "list_name TEXT NOT NULL," +
                "size INTEGER NOT NULL);";
        template.execute(sql);
        sql = "CREATE TABLE IF NOT EXISTS Things" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "list_id INTEGER NOT NULL REFERENCES Lists," +
                "list_index INTEGER NOT NULL," +
                "thing_name TEXT NOT NULL," +
                "done INTEGER NOT NULL);";
        template.execute(sql);
    }

    @Override
    public List<ListOfThingsToDo> getLists() {
        String sql = "SELECT Lists.id AS id, list_name, size, thing_name, done " +
                "FROM Lists LEFT JOIN Things ON Lists.id = Things.list_id " +
                "ORDER BY Lists.id, list_index;";
        return getJdbcTemplate().query(sql, resultSet -> {
            var res = new ArrayList<ListOfThingsToDo>();
            ListOfThingsToDo cur = null;
            int id = -1;
            while (resultSet.next()) {
                int listId = resultSet.getInt("id");
                if (listId > id) {
                    id = listId;
                    if (cur != null) {
                        res.add(cur);
                    }
                    cur = new ListOfThingsToDo();
                    cur.setName(resultSet.getString("list_name"));
                    cur.setList(new ArrayList<>());
                }
                if (resultSet.getInt("size") == 0)
                    continue;
                var thing = new ThingToDo();
                thing.setName(resultSet.getString("thing_name"));
                thing.setDone(resultSet.getBoolean("done"));
                cur.getList().add(thing);
            }
            if (cur != null) {
                res.add(cur);
            }
            return res;
        });
    }

    @Override
    public void addList(ListOfThingsToDo list) {
        String sql = "INSERT INTO Lists(list_name, size) VALUES(?, ?);";
        getJdbcTemplate().update(sql, list.getName(), 0);
    }

    @Override
    public void deleteList(int listIndex) {
        String sql = "WITH l AS (" +
                "SELECT id FROM Lists ORDER BY id LIMIT 1 OFFSET " + listIndex + ") " +
                "DELETE FROM Things WHERE list_id IN l;";
        String sql2 = "WITH l AS (" +
                "SELECT id FROM Lists ORDER BY id LIMIT 1 OFFSET " + listIndex + ") " +
                "DELETE FROM Lists WHERE id IN l;";
        getJdbcTemplate().batchUpdate(sql, sql2);
    }

    @Override
    public void addThingToDo(int listIndex, ThingToDo thing) {
        var template = getJdbcTemplate();
        String sql = "SELECT id, size FROM Lists ORDER BY id LIMIT 1 OFFSET " + listIndex + ";";
        var res = template.query(sql, (ResultSet resultSet) -> {
            if (resultSet.next()) {
                return new int[] {resultSet.getInt("id"), resultSet.getInt("size")};
            }
            return null;
        });
        sql = "INSERT INTO Things(list_id, list_index, thing_name, done) VALUES(?, ?, ?, ?);";
        template.update(sql, res[0], res[1], thing.getName(), 0);
        sql = "UPDATE Lists SET size = size + 1 WHERE id = ?;";
        template.update(sql, res[0]);
    }

    @Override
    public void markAsDone(int listIndex, int thingToDoIndex) {
        String sql = "WITH l AS (" +
                "SELECT id FROM Lists ORDER BY id LIMIT 1 OFFSET " + listIndex + ") " +
                "UPDATE Things SET done = 1 " +
                "WHERE list_id IN l AND list_index = " + thingToDoIndex + ";";
        getJdbcTemplate().execute(sql);
    }
}
