package uk.gov.gsi.hmrc.rest.dms.utils;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class DfcUtilsTest {
	List<String> testingList = Arrays.asList("abc","ced","efc","123123","asd4564","123123123123qsdqsd","abc","abc");

	@Test
	public void testGetDocListAsCSVGolden() {		
		assertEquals("abc,ced,efc,123123,asd4564,123123123123qsdqsd,abc,abc",DfcUtils.getDocListAsCSV(testingList));
	}
	@Test
	public void testGetDocListAsCSVEmptyString() {	
		testingList = Arrays.asList("");
		assertEquals("",DfcUtils.getDocListAsCSV(testingList));
	}
	@Test
	public void testGetDocListAsCSVSingleItem() {	
		testingList = Arrays.asList("abc");
		assertEquals("abc",DfcUtils.getDocListAsCSV(testingList));
	}
	
	@Test
	public void testExpectionStringBuilder() {
		String expectionMessage = "JDBC exception on Hibernate data access: SQLException for SQL [n/a]; SQL state [S1000]; error code [1105]; Could not allocate space for object 'dbo.RA_INVOKER_REQUEST_DETAILS'.'PK_RA_INVOKER_REQUEST_DETAILS' in database 'nicerti_rtserver_operational' because the 'PRIMARY' filegroup is full. Create disk space by deleting unneeded files, dropping objects in the filegroup, adding additional files to the filegroup, or setting autogrowth on for existing files in the filegroup.; nested exception is org.hibernate.exception.GenericJDBCException: Could not allocate space for object 'dbo.RA_INVOKER_REQUEST_DETAILS'.'PK_RA_INVOKER_REQUEST_DETAILS' in database 'nicerti_rtserver_operational' because the 'PRIMARY' filegroup is full. Create disk space by deleting unneeded files, dropping objects in the filegroup, adding additional files to the filegroup, or setting autogrowth on for existing files in the filegroup.";
		String expectedMessage = "JDBC exception on Hibernate data access: SQLException for SQL [n/a]; SQL state [S1000]; error code [1105]; Could not allocate space for object *dbo.RA_INVOKER_REQUEST_DETAILS*.*PK_RA_INVOKER_REQUEST_DETAILS* in database *nicerti_rtserver_operational* because the *PRIMARY* filegroup is full. Create disk space by deleting unneeded files, dropping objects in the filegroup, adding additional files to the filegroup, or setting autogrowth on for existing files in the filegroup.; nested exception is o";
	
		assertEquals(expectedMessage, DfcUtils.expectionStringBuilder(expectionMessage));		
	}
}
