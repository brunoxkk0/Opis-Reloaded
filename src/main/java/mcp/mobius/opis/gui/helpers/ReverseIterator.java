package mcp.mobius.opis.gui.helpers;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.Lists;

public class ReverseIterator<T> implements Iterable<T> {

    private final ListIterator<T> listIterator;

    public ReverseIterator(Collection<T> wrappedList) {
        List list = Lists.newArrayList(wrappedList);
        this.listIterator = list.listIterator(wrappedList.size());
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return listIterator.hasPrevious();
            }

            @Override
            public T next() {
                return listIterator.previous();
            }

            @Override
            public void remove() {
                listIterator.remove();
            }

        };
    }

}
