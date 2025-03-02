package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.*;

import java.util.List;

public class RestTableRepository extends BaseRepository<RestTable> {

    public RestTableRepository(){
        super();
    }

    @Override
    public int getId(RestTable entity) {
        return entity.getId();
    }

    @Override
    protected Class<RestTable> getEntityClass() {
        return RestTable.class;
    }
    public void populate(List<RestTable> tables) {
        for (RestTable table : tables) {
            save(table);
        }
    }
}
