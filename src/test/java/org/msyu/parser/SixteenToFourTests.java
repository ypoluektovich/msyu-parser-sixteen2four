package org.msyu.parser;

import org.testng.annotations.Test;

import java.util.Arrays;

import static org.msyu.parser.SixteenToFour.CODE_POINT;
import static org.msyu.parser.SixteenToFour.ERROR;
import static org.msyu.parser.SixteenToFour.INITIAL_STATE;
import static org.msyu.parser.SixteenToFour.advance;
import static org.msyu.parser.SixteenToFour.typeOfState;

public class SixteenToFourTests {

	private static int[] process(CharSequence in) {
		int[] states = new int[in.length()];
		int state = INITIAL_STATE;
		int type = typeOfState(INITIAL_STATE);
		for (int i = 0; i < in.length(); ) {
			state = advance(state, in.charAt(i));
			type = typeOfState(state);
			states[i++] = state;
			if (type == ERROR) {
				throw new BadEnd(type, states, i);
			}
		}
		if (type != CODE_POINT) {
			throw new BadEnd(type, states, states.length);
		}
		return states;
	}

	private static class BadEnd extends RuntimeException {
		final int type;
		final int[] states;
		BadEnd(int type, int[] states, int count) {
			assert type != CODE_POINT;
			this.type = type;
			this.states = Arrays.copyOf(states, count);
		}
	}

	@Test
	public void simple_characters() {
		assert Arrays.equals(process("abc"), new int[]{'a', 'b', 'c'});
	}

	@Test
	public void surrogate_pair() {
		assert Arrays.equals(process("\uD83C\uDC00"), new int[]{0x200D83C, 0x1F000});
	}

	@Test(expectedExceptions = BadEnd.class)
	public void surrogate_high_end() {
		try {
			process("\uD83C");
		} catch (BadEnd e) {
			assert e.type != ERROR;
			assert Arrays.equals(e.states, new int[]{0x200D83C});
			throw e;
		}
	}

	@Test(expectedExceptions = BadEnd.class)
	public void surrogate_high_high() {
		try {
			process("\uD83C\uD83C");
		} catch (BadEnd e) {
			assert e.type == ERROR;
			assert Arrays.equals(e.states, new int[]{0x200D83C, ERROR << 24});
			throw e;
		}
	}

	@Test(expectedExceptions = BadEnd.class)
	public void surrogate_low() {
		try {
			process("\uDC00");
		} catch (BadEnd e) {
			assert e.type == ERROR;
			assert Arrays.equals(e.states, new int[]{ERROR << 24});
			throw e;
		}
	}

}
