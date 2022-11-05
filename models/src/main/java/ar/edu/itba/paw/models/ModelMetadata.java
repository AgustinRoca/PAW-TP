package ar.edu.itba.paw.models;

import java.util.Objects;

public class ModelMetadata {
    private final Long count;
    private final Object min;
    private final Object max;

    public ModelMetadata() {
        this.count = null;
        this.min = this.max = null;
    }

    public ModelMetadata(Long count, Object min, Object max) {
        this.count = count;
        this.min = min;
        this.max = max;
    }

    public Object getMin() {
        return this.min;
    }

    public Object getMax() {
        return this.max;
    }

    public Long getCount() {
        return this.count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelMetadata)) return false;
        ModelMetadata that = (ModelMetadata) o;
        return Objects.equals(this.getCount(), that.getCount()) &&
                Objects.equals(this.getMin(), that.getMin()) &&
                Objects.equals(this.getMax(), that.getMax());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getCount(), this.getMin(), this.getMax());
    }
}
