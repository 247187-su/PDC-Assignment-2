
public class TransactionStack {

    private final char[] stack;
    private int top;

    public TransactionStack(int capacity) {

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Stack capacity must be positive."
            );
        }

        stack = new char[capacity];
        top = -1;
    }

    // Insert a character into the stack
    public void push(char value) {

        if (isFull()) {
            throw new IllegalStateException(
                "Stack overflow: no space available."
            );
        }

        stack[++top] = value;
    }

    // Remove and return the last inserted character
    public char pop() {

        if (isEmpty()) {
            throw new IllegalStateException(
                "Stack underflow: stack is empty."
            );
        }

        return stack[top--];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == stack.length - 1;
    }

    public int size() {
        return top + 1;
    }

    public int capacity() {
        return stack.length;
    }
}
