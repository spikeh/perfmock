package test.jmock.dynamic.testsupport;

import org.jmock.Verifiable;
import org.jmock.dynamic.Invocation;
import org.jmock.dynamic.Stub;
import org.jmock.expectation.ExpectationValue;
import org.jmock.util.Verifier;


public class MockStub 
	implements Stub, Verifiable
{
	private String name;
	
	public MockStub() {
		this("mockStub");
	}
	
	public MockStub( String name ) {
		this.name = name;
	}
	
    public String toString() {
        return name;
    }
    
    public ExpectationValue invokeInvocation = 
		new ExpectationValue("invoke invocation");
	public Object invokeResult;
	
	public Object invoke(Invocation invocation) throws Throwable {
		invokeInvocation.setActual(invocation);
		return invokeResult;
	}

	
	public ExpectationValue writeToBuffer = new ExpectationValue("writeTo buffer");
	public String writeToOutput = "";
	
	public StringBuffer describeTo(StringBuffer buffer) {
		writeToBuffer.setActual(buffer);
		buffer.append(writeToOutput);
		return buffer;
	}
	
	public void verify() {
		Verifier.verifyObject(this);
	}
}