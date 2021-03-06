package uk.davidwei.perfmock.test.acceptance;

import junit.framework.TestCase;
import uk.davidwei.perfmock.Mockery;
import uk.davidwei.perfmock.test.unit.support.AssertThat;

public class UniqueNamesAcceptanceTest extends TestCase {
    Mockery context = new Mockery();
    
    public void testCannotHaveTwoMockObjectsWithTheSameName() {
        context.mock(MockedType.class, "name");
        try {
            context.mock(MockedType.class, "name");
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            AssertThat.stringIncludes("should mention name", "name", e.getMessage());
        }
    }

    public void testCannotHaveTwoMockObjectsWithTheSameDefaultName() {
        context.mock(MockedType.class);
        try {
            context.mock(MockedType.class);
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            AssertThat.stringIncludes("should mention name", "name", e.getMessage());
        }
    }
}
