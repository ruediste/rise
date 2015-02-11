package com.github.ruediste.laf.core.classReload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.laf.core.classReload.Gate;

public class GateTest {

	private boolean passed;

	@Before
	public void setup() {
		passed = false;
	}

	@Test
	public void simple() {
		Gate gate = new Gate();

		new Thread(new Runnable() {

			@Override
			public void run() {
				sleep();
				passed = true;
				gate.open();
			}
		}).start();
		assertFalse(passed);
		gate.pass();
		assertTrue(passed);
	}

	@Test(timeout = 200)
	public void goThroughFirst() {
		Gate gate = new Gate();

		new Thread(new Runnable() {

			@Override
			public void run() {
				gate.open();
				passed = true;
			}
		}).start();
		sleep();
		assertTrue(passed);
		gate.pass();
	}

	@Test(timeout = 50)
	public void passOpenGate() {
		Gate gate = new Gate();
		gate.open();
		gate.pass();
	}

	@Test(timeout = 200)
	public void close() throws InterruptedException {
		Gate gate = new Gate();
		gate.open();
		gate.pass();
		gate.close();

		new Thread(new Runnable() {

			@Override
			public void run() {
				gate.pass();
				passed = true;
			}
		}).start();

		Thread.sleep(50);
		assertFalse(passed);
	}

	protected void sleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
