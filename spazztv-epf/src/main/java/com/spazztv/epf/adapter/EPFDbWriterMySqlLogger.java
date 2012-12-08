/**
 * 
 */
package com.spazztv.epf.adapter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;

import com.spazztv.epf.EPFImporter;

/**
 * Logger advice for EPFDbWriterMySql (EPFDbWriterMySqlDao)
 * <p>
 * All logging defined here is debug level only which outputs verbose SQL
 * statements.
 * 
 * <p>
 * Log level debugging is controlled by the corresponding log4j.xml
 * configuration.
 * 
 * @author Thomas Billingsley
 * 
 */
@Aspect("perthis(call(* com.spazztv.epf.adapter.EPFDbWriterMySqlDao..*(..)))")
public class EPFDbWriterMySqlLogger {

	@Before("call(* com.spazztv.epf.adapter.EPFDbWriterMySqlDao.executeSQLStatement(..))")
	public void beforeExecuteSQLStatement(JoinPoint joinPoint) {
		Logger log = EPFImporter.getLogger();
		if (log.isDebugEnabled()) {
			log.debug("MySQL Exec: " + joinPoint.getArgs()[0]);
		}
	}
}