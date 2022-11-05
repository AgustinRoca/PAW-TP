package ar.edu.itba.paw.models;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Paginator<M extends GenericModel<?>> {
    private final Long totalCount;
    private final int pageSize, page, remainingPages;
    private final List<M> models;

    public Paginator(Collection<M> models, int page, int pageSize, Long totalCount) {
        this.models = new LinkedList<>(models);
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.page = page;

        this.remainingPages = (int) Math.ceil((double) totalCount / (double) pageSize) - page;
    }

    public Long getTotalCount() {
        return this.totalCount;
    }

    public int getRemainingPages() {
        return this.remainingPages;
    }

    public List<M> getModels() {
        return this.models;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getPage() {
        return this.page;
    }

    public int getTotalPages() {
        return this.remainingPages + this.page;
    }
}
