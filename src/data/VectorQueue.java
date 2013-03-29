package data;

import java.util.Queue;
import java.util.Vector;

@SuppressWarnings("serial")
public class VectorQueue<E> extends Vector<E> implements Queue<E> {
	
	public VectorQueue() {
		super();
	}

	public E element() {
		return firstElement();
	}

	public boolean offer(E e) {
		return add(e);
	}

	public E peek() {
		if (size() > 0) return firstElement();
		return null;
	}

	public E poll() {
		if (size() > 0) return remove(0);
		return null;
	}

	public E remove() {
		return remove(0);
	}

}
