package ar.edu.itba.paw.models;

import java.util.Objects;

/**
 * This class provides a generic implementation of a model.
 *
 * @param <I>
 */
public abstract class GenericModel<I> {
    public abstract I getId();

    public abstract void setId(I id);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!this.isSameType(o)) return false;
        return this.getId().equals(((GenericModel<?>) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    protected abstract boolean isSameType(Object o);
}
