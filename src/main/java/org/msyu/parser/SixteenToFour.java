package org.msyu.parser;

public final class SixteenToFour {

	private SixteenToFour() {
		// do not instantiate utility classes
	}

	public static final int CODE_POINT = 0;
	public static final int ERROR = 1;
	private static final int SURROGATE = 2;

	public static final int INITIAL_STATE = 0;
	private static final int ERROR_STATE = ERROR << 24;

	public static int typeOfState(int state) {
		return state >>> 24;
	}

	public static int advance(int state, char c) {
		switch (typeOfState(state)) {
			case CODE_POINT:
				int ci = ((int) c) & 0xFFFF;
				if (Character.isHighSurrogate(c)) {
					return (SURROGATE << 24) | ci;
				} else if (!Character.isLowSurrogate(c)) {
					return ci;
				}
				break;
			case SURROGATE:
				if (Character.isLowSurrogate(c)) {
					return Character.toCodePoint((char) state, c);
				}
				break;
		}
		return ERROR_STATE;
	}

}
