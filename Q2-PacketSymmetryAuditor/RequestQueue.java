
public class RequestQueue {

    private final char[] queue;
    private int front;
    private int rear;
    private int count;

    public RequestQueue(int capacity) {

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Queue capacity must be positive."
            );
        }

        queue = new char[capacity];
        front = 0;
        rear = 0;
        count = 0;
    }

    // Add a character to the rear of the queue
    public void enqueue(char value) {

        if (isFull()) {
            throw new IllegalStateException(
                "Queue overflow: no space available."
            );
        }

        queue[rear] = value;
        rear = (rear + 1) % queue.length;
        count++;
    }

    // Remove the oldest character
    public char dequeue() {

        if (isEmpty()) {
            throw new IllegalStateException(
                "Queue underflow: queue is empty."
            );
        }

        char value = queue[front];
        front = (front + 1) % queue.length;
        count--;

        return value;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == queue.length;
    }

    public int size() {
        return count;
    }

    public int capacity() {
        return queue.length;
    }
}
