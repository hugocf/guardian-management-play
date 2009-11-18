package com.gu.management.database.checking;

import org.hibernate.JDBCException;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class ConnectionCheckResultClassifierTest {

	private Collection<Integer> emptySQLErrorCodeList = Collections.emptyList();
	private Collection<String> emptySQLErrorMessageList = Collections.emptyList();

    @Test
	public void testShouldNotClassifyAsSevereIfResultIsSuccessful() throws Exception {
		ConnectionCheckResult result = new ConnectionCheckResult(true);

		ConnectionCheckResultClassifier classifer = new ConnectionCheckResultClassifier(emptySQLErrorCodeList, emptySQLErrorMessageList);
		assertThat(classifer.isSevere(result), equalTo(false));
	}

	@Test
	public void testShouldClassifyAsSevereIfResultIsNotSuccessfulAndHasSevereSQLErrorCode() throws Exception {
		SQLException sqlException = new SQLException("ORA-12345: the database is exploding", "industry standard exception code", 12345);
		JDBCException exception = new JDBCException("some sql exception occured", sqlException);
		ConnectionCheckResult result = new ConnectionCheckResult(exception);

		ConnectionCheckResultClassifier classifer = new ConnectionCheckResultClassifier(Arrays.asList(12345), emptySQLErrorMessageList);
		assertThat(classifer.isSevere(result), equalTo(true));
	}

	@Test
	public void testShouldClassifyAsSevereIfResultIsNotSuccessfulAndMatchesSevereErrorMessage() throws Exception {
		SQLException sqlException = new SQLException("the database is levitating on a sea of jelly");
		JDBCException exception = new JDBCException("some sql exception occured", sqlException);
		ConnectionCheckResult result = new ConnectionCheckResult(exception);

		ConnectionCheckResultClassifier classifer = new ConnectionCheckResultClassifier(emptySQLErrorCodeList,
				Arrays.asList("the database is levitating on a sea of jelly"));
		assertThat(classifer.isSevere(result), equalTo(true));
	}

	@Test
	public void testShouldNotClassifyAsSevereIfResultIsNotSuccessfulAndIsNotSevereSQLErrorCodeOrMessage() throws Exception {
		SQLException sqlException = new SQLException("ORA-987765: the database is out to lunch", "industry standard exception code", 987765);
		JDBCException exception = new JDBCException("some sql exception occured", sqlException);
		ConnectionCheckResult result = new ConnectionCheckResult(exception);

		ConnectionCheckResultClassifier classifer = new ConnectionCheckResultClassifier(Arrays.asList(12345), emptySQLErrorMessageList);
		assertThat(classifer.isSevere(result), equalTo(false));
	}

	@Test
	public void testShouldClassifyAsSevereIfResultIsNotSuccessfulAndMatchesSevereException() throws Exception {
		TimeoutException exception =new TimeoutException();
		ConnectionCheckResult result = new ConnectionCheckResult(exception);

		ConnectionCheckResultClassifier classifer = new ConnectionCheckResultClassifier(emptySQLErrorCodeList, emptySQLErrorMessageList);
		assertThat(classifer.isSevere(result), equalTo(true));
	}
	
	@Test
	public void testShouldNotMatchSuperClassOfSevereClass() throws Exception {
		ConnectionCheckResult result = new ConnectionCheckResult(new IOException());
		ConnectionCheckResultClassifier classifer = new ConnectionCheckResultClassifier(emptySQLErrorCodeList, emptySQLErrorMessageList);

		assertThat(classifer.isSevere(result), equalTo(false));
	}

}