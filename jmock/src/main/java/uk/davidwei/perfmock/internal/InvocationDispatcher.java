package uk.davidwei.perfmock.internal;

import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;
import uk.davidwei.perfmock.api.Expectation;
import uk.davidwei.perfmock.api.ExpectationError;
import uk.davidwei.perfmock.api.Invocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class InvocationDispatcher implements ExpectationCollector, SelfDescribing {
    private static NetworkDispatcher networkDispatcher;
	private List<Expectation> expectations = Collections.synchronizedList(new ArrayList<Expectation>());
	private List<StateMachine> stateMachines = new ArrayList<StateMachine>();
    private double totalResponseTime = 0.0;

    public static void setNetworkDispatcher(NetworkDispatcher dispatcher) {
        networkDispatcher = dispatcher;
    }

    public StateMachine newStateMachine(String name) {
        StateMachine stateMachine = new StateMachine(name);
        stateMachines.add(stateMachine);
        return stateMachine;
    }
    
	public void add(Expectation expectation) {
		expectations.add(expectation);
	}
	
    public void describeTo(Description description) {
        describe(description, expectations);
    }

    public void describeMismatch(Invocation invocation, Description description) {
        describe(description, describedWith(expectations, invocation));
    }

    private Iterable<SelfDescribing> describedWith(List<Expectation> expectations, final Invocation invocation) {
        final Iterator<Expectation> iterator = expectations.iterator();
        return new Iterable<SelfDescribing>() {
            public Iterator<SelfDescribing> iterator() {
                return new Iterator<SelfDescribing>() {
                    public boolean hasNext() { return iterator.hasNext(); }
                    public SelfDescribing next() {
                        return new SelfDescribing() {
                            public void describeTo(Description description) {
                                iterator.next().describeMismatch(invocation, description);
                            }
                        };
                    }
                    public void remove() { iterator.remove(); }
                };
            }
        };
    }

    private void describe(Description description, Iterable<? extends SelfDescribing> selfDescribingExpectations) {
        if (expectations.isEmpty()) {
            description.appendText("no expectations specified: did you...\n"+
                                   " - forget to start an expectation with a cardinality clause?\n" +
                                   " - call a mocked method to specify the parameter of an expectation?");
        }
        else {
            description.appendList("expectations:\n  ", "\n  ", "", selfDescribingExpectations);
            if (!stateMachines.isEmpty()) {
                description.appendList("\nstates:\n  ", "\n  ", "", stateMachines);
            }
        }
    }
    

    public boolean isSatisfied() {
		for (Expectation expectation : expectations) {
		    if (! expectation.isSatisfied()) {
                return false;
            }
        }
        return true;
	}
	
	public Object dispatch(Invocation invocation) throws Throwable {
		for (Expectation expectation : expectations) {
		    if (expectation.matches(invocation)) {
                if (networkDispatcher != null) {
                    InvocationExpectation invocationExpectation = ((InvocationExpectation)expectation);
                    networkDispatcher.query(invocation, invocationExpectation.getPerformanceModel(), invocationExpectation.getParam());
                }
		        return expectation.invoke(invocation);
            }
        }
        
        throw ExpectationError.unexpected("unexpected invocation", invocation);
	}

    public void updateResponseTime(long threadId) {}

    public double totalResponseTime() {
        return totalResponseTime;
    }

}
