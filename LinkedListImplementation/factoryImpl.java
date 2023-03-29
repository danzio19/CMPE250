import java.util.HashSet;
import java.util.NoSuchElementException;

public class FactoryImpl implements Factory {

    private Holder first;
    private Holder last;
    private Integer size;

    public FactoryImpl(Integer size) {
        this.size = size;
    }

    public Integer getSize() {
        return size;
    }

    @Override
    public void addFirst(Product product) {
        Holder newHolder = new Holder(null, product, first);
        addFirstHolder(newHolder);
    }
    private void addFirstHolder(Holder holder) {
        if (first == null) // if the line is empty
            last = holder; // new holder is both first and last
        else
            first.setPreviousHolder(holder); // if not empty, add it to the first
        holder.setNextHolder(first);
        first = holder;
        size++;
    }

    @Override
    public void addLast(Product product) {
        Holder newHolder = new Holder(last, product, null);
        addLastHolder(newHolder);
    }
    private void addLastHolder(Holder holder) {

        if (last == null) // if line is empty
            first = holder; // new holder is both first and last
        else
            last.setNextHolder(holder); // if not empty, add it to the last
        holder.setPreviousHolder(last);
        last = holder;
        size++;
    }

    @Override
    public Product removeFirst() throws NoSuchElementException {
        if (size == 0) {  // if the line is empty, throw exception
            throw new NoSuchElementException();
        }

        return removeFirstHolder().getProduct();
    }
    private Holder removeFirstHolder() {
        if (size == 0) {  // if the line is empty, throw exception
            throw new NoSuchElementException();
        }
        Holder removed = first;
        first = first.getNextHolder(); // remove the holder

        removed.setNextHolder(null);
        removed.setPreviousHolder(null);

        size--;
        return removed;
    }

    @Override
    public Product removeLast() throws NoSuchElementException {
        if (size == 0) { // if the line is empty, throw exception
            throw new NoSuchElementException();
        }
        return removeLastHolder().getProduct();
    }
    private Holder removeLastHolder() throws NoSuchElementException{
        if (size == 0) { // if the line is empty, throw exception
            throw new NoSuchElementException();
        }
        Holder removed = last;
        last = last.getPreviousHolder(); // remove the holder

       removed.setPreviousHolder(null); // set removed links to null
        last.setNextHolder(null);

        size--;
        return removed;
    }

    @Override
    public Product find(int id) throws NoSuchElementException {
        Holder current = first;
        try {
            while (current.getProduct().getId() != id) {
                current = current.getNextHolder();
            }
        }
        catch (NullPointerException e) {
            throw new NoSuchElementException();
        }

        return current.getProduct();
    }

    @Override
    public Product update(int id, Integer value) throws NoSuchElementException {
        Product product = find(id);
        Product beforeUpdate = new Product(id, product.getValue());
        product.setValue(value);
        return beforeUpdate;
    }

    @Override
    public Product get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index > size - 1)
            throw new IndexOutOfBoundsException();

        return getHolder(index).getProduct();
    }

    private Holder getHolder(int index) {
        Holder current = first;
        for (int i = 0; i < index; i++)
            current = current.getNextHolder();
        return current;
    }

    @Override
    public void add(int index, Product product) throws IndexOutOfBoundsException {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        else if (index == size) {
            addLast(product);
        }
        else if (index == 0) {
            addFirst(product);
        }
        else {
            Holder nextHolder = getHolder(index);
            Holder added = new Holder(nextHolder.getPreviousHolder(), product, nextHolder);

            nextHolder.getPreviousHolder().setNextHolder(added);
            added.getNextHolder().setPreviousHolder(added);
            size++;

        }

    }

    @Override
    public Product removeIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index > size - 1)
            throw new IndexOutOfBoundsException();

        Holder removed = removeHolder(getHolder(index));

        return removed.getProduct();
    }

    @Override
    public Product removeProduct(int value) throws NoSuchElementException {
        Holder current = first;
        try {
            while (current.getProduct().getValue() != value)
                current = current.getNextHolder();
        }
        catch (NullPointerException e) {
            throw new NoSuchElementException();
        }

        Holder removed = removeHolder(current);

        return removed.getProduct();
    }
    private Holder removeHolder(Holder removed) {
        if (removed.equals(first)) {
            removeFirstHolder();
        }
        else if (removed.equals(last)) {
            removeLastHolder();
        }
        else {
            removed.getPreviousHolder().setNextHolder(removed.getNextHolder());
            removed.getNextHolder().setPreviousHolder(removed.getPreviousHolder());
            removed.setPreviousHolder(null);
            removed.setNextHolder(null);
            size--;
        }

        return removed;
    }

    @Override
    public int filterDuplicates() {
        HashSet<Integer> set = new HashSet<>();
        Holder current = first;
        Holder previous = null;
        int numRemoved = 0;
        while (current != null) {
            if (set.contains(current.getProduct().getValue())) {
                removeHolder(current);
                numRemoved++;
                current = previous.getNextHolder();
            }
            else {
                set.add(current.getProduct().getValue());
                previous = current;
                current = current.getNextHolder();
            }
        }
        return numRemoved;
    }

    @Override
    public void reverse() {

        Holder mid = getHolder(size/2);
        Holder left;
        Holder right;
        if (size % 2 == 0) {
            left = mid;
            right = left.getNextHolder();
        }
        else {
            left = mid.getPreviousHolder();
            right = mid.getNextHolder();
        }
        for (int i = 0; i < size/2; i++){
            left = left.getPreviousHolder();
            Holder removedLeft = removeHolder(left.getNextHolder());
            addLastHolder(removedLeft);

            right = right.getNextHolder();
            Holder removedRight = removeHolder(right.getPreviousHolder());
            addFirstHolder(removedRight);
        }

    }
    public String toString() {
        if (size == 0)
            return "{}";
        String output = "{";
        output += first.getProduct().toString();
        Holder current = first.getNextHolder();
        while (current != null) {
            output += "," + current.getProduct().toString();
            current = current.getNextHolder();
        }
        output += "}";
        return output;
    }
}
