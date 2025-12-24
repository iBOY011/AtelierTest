/*
 * Copyright 2015-2025 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.example.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CalculatorTests {

	@Test
	@DisplayName("1 + 1 = 2")
	void addsTwoNumbers() {
		Calculator calculator = new Calculator();
		assertEquals(2, calculator.add(1, 1), "1 + 1 should equal 2");
	}

	@ParameterizedTest(name = "{0} + {1} = {2}", quoteTextArguments = false)
	@CsvSource(textBlock = """
			0,    1,   1
			1,    2,   3
			49,  51, 100
			1,  100, 101
			-3,      5,    2
			-10,    -5,  -15
			0,       0,    0
			5,      -7,   -2
			2000000000, 100000000, 2100000000
			-2000000000, -100000000, -2100000000
			""")
	void add(int first, int second, int expectedResult) {
		Calculator calculator = new Calculator();
		assertEquals(expectedResult, calculator.add(first, second),
				() -> first + " + " + second + " should equal " + expectedResult);
	}

	@Test
	@DisplayName("Addition avec types plus petits (auto-cast vers int)")
	void addWithWideningPrimitiveConversions() {
		Calculator calculator = new Calculator();
		byte b1 = 10, b2 = 20;
		short s1 = 30000, s2 = 123;
		char c1 = 'A', c2 = 1; // 'A' = 65
		long l1 = 1L, l2 = 2L; // nécessite un cast explicite vers int
		short sMax = Short.MAX_VALUE, sMin = Short.MIN_VALUE;
		byte bMax = Byte.MAX_VALUE, bMin = Byte.MIN_VALUE;

		assertEquals(30, calculator.add(b1, b2), "byte -> int auto-cast");
		assertEquals(30123, calculator.add(s1, s2), "short -> int auto-cast");
		assertEquals(66, calculator.add(c1, c2), "char -> int auto-cast");
		assertEquals(3, calculator.add((int) l1, (int) l2), "long -> int avec cast explicite");
		assertEquals(65533, calculator.add(sMax, sMax - 1), "short max conserve la valeur apres cast");
		assertEquals(-129, calculator.add(bMin, -2), "byte min conserve la valeur apres cast");
		assertEquals(254, calculator.add(bMax, bMax), "byte max conserve la valeur apres cast");
	}

	@Test
	@DisplayName("Conversions depuis float/double (casts explicites vers int)")
	void addWithFloatingPointCasts() {
		Calculator calculator = new Calculator();
		float f1 = 1.9f, f2 = 2.1f; // cast tronque
		double d1 = -3.7, d2 = 1.2;
		assertEquals(3, calculator.add((int) f1, (int) f2), "float cast -> int tronque");
		assertEquals(-2, calculator.add((int) d1, (int) d2), "double cast -> int tronque");
	}

	@Test
	@DisplayName("Valeurs proches des bornes int sans overflow")
	void addAtIntegerBoundaries() {
		Calculator calculator = new Calculator();
		assertEquals(Integer.MAX_VALUE, calculator.add(Integer.MAX_VALUE - 1, 1));
		assertEquals(Integer.MIN_VALUE, calculator.add(Integer.MIN_VALUE + 1, -1));
	}

	@Test
	@DisplayName("Overflow attendu : MAX_VALUE + 1 et MIN_VALUE - 1")
	void addOverflowThrows() {
		Calculator calculator = new Calculator();
		assertThrows(ArithmeticException.class, () -> calculator.add(Integer.MAX_VALUE, 1),
				"overflow positif devrait lever ArithmeticException");
		assertThrows(ArithmeticException.class, () -> calculator.add(Integer.MIN_VALUE, -1),
				"overflow negatif devrait lever ArithmeticException");
	}

	@Test
	@DisplayName("Null en entrée -> NullPointerException (auto-unboxing)")
	void addNullInputsThrow() {
		Calculator calculator = new Calculator();
		Integer nullValue = null;
		assertThrows(NullPointerException.class, () -> calculator.add(nullValue, 1),
				"auto-unboxing d'un null doit lancer NPE");
		assertThrows(NullPointerException.class, () -> calculator.add(1, nullValue),
				"auto-unboxing d'un null doit lancer NPE");
	}
}
