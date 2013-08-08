package at.ac.tuwien.auto.iotsys.gateway.obix.observer;

import java.util.LinkedList;
import java.util.List;

import obix.Feed;
import obix.Obj;

/**
 * Observes the state changes of an obix object. Holds a history 
 * of the changes until the events are polled or the max number of 
 * elements is exceeded.
 * 
 * This class also acts as a singleton subject, in order to allow CoAP updates on observed resources.
 *
 */
public class FeedObserver implements EventObserver<Obj> {
	private Feed subject;
	
	public static final int MAX_EVENTS = 50;
	private static final Object lock = new Object();
	private LinkedList<Obj> unpolledEvents = new LinkedList<Obj>();
	private Obj filter;
	
	public FeedObserver(Obj filter) {
		this.filter = filter;
	}
		
	@Override
	public void update(Object currentState) {
		synchronized(lock) {
			unpolledEvents.add((Obj)currentState);
		}
	}
	
	/**
	 * Provides the latest events.
	 * @return 
	 */
	public List<Obj> getEvents(){
		List<Obj> ret = null;
		synchronized(lock) {
			ret = subject.query(unpolledEvents, filter);
			unpolledEvents.clear();
		}
		return ret;
	}

	public Subject getSubject() {
		return subject;
	}
	
	public void setSubject(Subject subject) {
		if (!(subject instanceof Feed)) return;
		this.subject = (Feed) subject;
	}
	
	public Obj getFilter() {
		return filter;
	}
}