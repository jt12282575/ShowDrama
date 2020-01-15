package dada.com.showdrama.Data.Paging;

import java.util.List;

import dada.com.showdrama.Model.Drama;

public class DramaListProvider {

    private List<Drama> list;

    public DramaListProvider(List<Drama> list) {
        this.list = list;
    }

    public List<Drama> getDramaList(int page, int pageSize) {
        int initialIndex = page * pageSize;
        int finalIndex = initialIndex + pageSize;

        //TODO manage out of range index

        return list.subList(initialIndex, finalIndex);
    }
}
