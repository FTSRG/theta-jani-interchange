/*
 * Copyright 2018 Contributors to the Theta project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hu.bme.mit.theta.interchange.jani.model

import hu.bme.mit.theta.interchange.jani.model.json.JaniModelMapper
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpressionsTest {
    private val objectMapper = JaniModelMapper()

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `serialize expressions`(testCase: SerializationTestCase<Expression>) {
        testCase.assertSerialized(objectMapper, Expression::class.java)
    }

    @ParameterizedTest
    @MethodSource("deserializationSerializedTopLevelTypeDataProvider")
    fun `deserialize expressions`(testCase: SerializationTestCase<Expression>) {
        testCase.assertDeserialized(objectMapper, Expression::class.java)
    }

    @Suppress("unused")
    fun serializedTopLevelTypeDataProvider() = Stream.of(
        "false" isJsonFor BoolConstant.FALSE,
        "true" isJsonFor BoolConstant.TRUE,

        "3" isJsonFor IntConstant(3),
        "-1" isJsonFor IntConstant(-1),

        "0.12" isJsonFor RealConstant(0.12),
        "3.0E-12" isJsonFor RealConstant(3e-12),
        "-0.12" isJsonFor RealConstant(-0.12),
        "-3.0E-12" isJsonFor RealConstant(-3e-12),

        "\"e\"" isJsonFor NamedConstant.E,
        "\"π\"" isJsonFor NamedConstant.PI,

        """{"op":"ite","if":true,"then":1,"else":2}""" isJsonFor
            Ite(BoolConstant.TRUE, IntConstant(1), IntConstant(2)),

        """{"op":"¬","exp":true}""" isJsonFor UnaryOp.NOT.of(BoolConstant.TRUE),
        """{"op":"floor","exp":0.12}""" isJsonFor UnaryOp.FLOOR.of(RealConstant(0.12)),
        """{"op":"ceil","exp":0.12}""" isJsonFor UnaryOp.CEIL.of(RealConstant(0.12)),
        """{"op":"der","exp":"x"}""" isJsonFor UnaryOp.DER.of(Identifier("x")),

        """{"op":"∨","left":true,"right":false}""" isJsonFor BinaryOp.OR.of(BoolConstant.TRUE, BoolConstant.FALSE),
        """{"op":"∧","left":true,"right":false}""" isJsonFor BinaryOp.AND.of(BoolConstant.TRUE, BoolConstant.FALSE),
        """{"op":"=","left":1,"right":2}""" isJsonFor BinaryOp.EQ.of(IntConstant(1), IntConstant(2)),
        """{"op":"≠","left":1,"right":2}""" isJsonFor BinaryOp.NEQ.of(IntConstant(1), IntConstant(2)),
        """{"op":"<","left":1,"right":2}""" isJsonFor BinaryOp.LT.of(IntConstant(1), IntConstant(2)),
        """{"op":"≤","left":1,"right":2}""" isJsonFor BinaryOp.LEQ.of(IntConstant(1), IntConstant(2)),
        """{"op":"+","left":1,"right":2}""" isJsonFor BinaryOp.ADD.of(IntConstant(1), IntConstant(2)),
        """{"op":"-","left":1,"right":2}""" isJsonFor BinaryOp.SUB.of(IntConstant(1), IntConstant(2)),
        """{"op":"*","left":1,"right":2}""" isJsonFor BinaryOp.MUL.of(IntConstant(1), IntConstant(2)),
        """{"op":"%","left":1,"right":2}""" isJsonFor BinaryOp.MOD.of(IntConstant(1), IntConstant(2)),
        """{"op":"/","left":1,"right":2}""" isJsonFor BinaryOp.DIV.of(IntConstant(1), IntConstant(2)),
        """{"op":"pow","left":1,"right":2}""" isJsonFor BinaryOp.POW.of(IntConstant(1), IntConstant(2)),
        """{"op":"log","left":1,"right":2}""" isJsonFor BinaryOp.LOG.of(IntConstant(1), IntConstant(2)),

        """{"op":"av","elements":[]}""" isJsonFor ArrayValue(emptyList()),
        """{"op":"av","elements":[1,2]}""" isJsonFor ArrayValue(IntConstant(1), IntConstant(2)),

        """{"op":"ac","var":"x","length":5,"exp":1}""" isJsonFor
            ArrayConstructor("x", IntConstant(5), IntConstant(1)),

        """{"op":"dv","type":"foo","values":[]}""" isJsonFor DatatypeValue("foo", emptyList()),
        """{"op":"dv","type":"foo","values":[{"member":"m1","value":1}]}""" isJsonFor
            DatatypeValue("foo", listOf(DatatypeMemberValue("m1", IntConstant(1)))),
        """{"op":"dv","type":"foo","values":[{"member":"m1","value":1},{"member":"m2","value":true}]}""" isJsonFor
            DatatypeValue("foo", listOf(
                DatatypeMemberValue("m1", IntConstant(1)),
                DatatypeMemberValue("m2", BoolConstant.TRUE)
            )),

        """{"op":"ov","exp":1}""" isJsonFor OptionValue(IntConstant(1)),

        """{"op":"empty"}""" isJsonFor EmptyOption,

        """{"op":"abs","exp":-2}""" isJsonFor DerivedUnaryOp.ABS.of(IntConstant(-2)),
        """{"op":"sgn","exp":-2}""" isJsonFor DerivedUnaryOp.SGN.of(IntConstant(-2)),
        """{"op":"trc","exp":-2.5}""" isJsonFor DerivedUnaryOp.TRC.of(RealConstant(-2.5)),

        """{"op":"⇒","left":false,"right":true}""" isJsonFor
            DerivedBinaryOp.IMPLIES.of(BoolConstant.FALSE, BoolConstant.TRUE),
        """{"op":">","left":1,"right":2}""" isJsonFor
            DerivedBinaryOp.GT.of(IntConstant(1), IntConstant(2)),
        """{"op":"≥","left":1,"right":2}""" isJsonFor
            DerivedBinaryOp.GEQ.of(IntConstant(1), IntConstant(2)),
        """{"op":"min","left":1,"right":2}""" isJsonFor
            DerivedBinaryOp.MIN.of(IntConstant(1), IntConstant(2)),
        """{"op":"max","left":1,"right":2}""" isJsonFor
            DerivedBinaryOp.MAX.of(IntConstant(1), IntConstant(2)),

        """{"op":"call","function":"f","args":[]}""" isJsonFor Call("f", emptyList()),
        """{"op":"call","function":"f","args":[1]}""" isJsonFor Call("f", listOf(IntConstant(1))),
        """{"op":"call","function":"f","args":[1,true]}""" isJsonFor
            Call("f", listOf(IntConstant(1), BoolConstant.TRUE)),

        """{"op":"sinh","exp":1.0}""" isJsonFor HyperbolicOp.SINH.of(RealConstant(1.0)),
        """{"op":"cosh","exp":1.0}""" isJsonFor HyperbolicOp.COSH.of(RealConstant(1.0)),
        """{"op":"tanh","exp":1.0}""" isJsonFor HyperbolicOp.TANH.of(RealConstant(1.0)),
        """{"op":"coth","exp":1.0}""" isJsonFor HyperbolicOp.COTH.of(RealConstant(1.0)),
        """{"op":"sech","exp":1.0}""" isJsonFor HyperbolicOp.SECH.of(RealConstant(1.0)),
        """{"op":"csch","exp":1.0}""" isJsonFor HyperbolicOp.CSCH.of(RealConstant(1.0)),
        """{"op":"asinh","exp":1.0}""" isJsonFor HyperbolicOp.ASINH.of(RealConstant(1.0)),
        """{"op":"acosh","exp":1.0}""" isJsonFor HyperbolicOp.ACOSH.of(RealConstant(1.0)),
        """{"op":"atanh","exp":1.0}""" isJsonFor HyperbolicOp.ATANH.of(RealConstant(1.0)),
        """{"op":"acoth","exp":1.0}""" isJsonFor HyperbolicOp.ACOTH.of(RealConstant(1.0)),
        """{"op":"asech","exp":1.0}""" isJsonFor HyperbolicOp.ASECH.of(RealConstant(1.0)),
        """{"op":"acsch","exp":1.0}""" isJsonFor HyperbolicOp.ACSCH.of(RealConstant(1.0)),

        """{"name":"foo","exp":3}""" isJsonFor Named("foo", IntConstant(3)),

        """{"op":"nondet","var":"x","exp":true}""" isJsonFor Nondet("x", BoolConstant.TRUE),

        """{"op":"sin","exp":1.0}""" isJsonFor TrigonometricOp.SIN.of(RealConstant(1.0)),
        """{"op":"cos","exp":1.0}""" isJsonFor TrigonometricOp.COS.of(RealConstant(1.0)),
        """{"op":"tan","exp":1.0}""" isJsonFor TrigonometricOp.TAN.of(RealConstant(1.0)),
        """{"op":"cot","exp":1.0}""" isJsonFor TrigonometricOp.COT.of(RealConstant(1.0)),
        """{"op":"sec","exp":1.0}""" isJsonFor TrigonometricOp.SEC.of(RealConstant(1.0)),
        """{"op":"csc","exp":1.0}""" isJsonFor TrigonometricOp.CSC.of(RealConstant(1.0)),
        """{"op":"asin","exp":1.0}""" isJsonFor TrigonometricOp.ASIN.of(RealConstant(1.0)),
        """{"op":"acos","exp":1.0}""" isJsonFor TrigonometricOp.ACOS.of(RealConstant(1.0)),
        """{"op":"atan","exp":1.0}""" isJsonFor TrigonometricOp.ATAN.of(RealConstant(1.0)),
        """{"op":"acot","exp":1.0}""" isJsonFor TrigonometricOp.ACOT.of(RealConstant(1.0)),
        """{"op":"asec","exp":1.0}""" isJsonFor TrigonometricOp.ASEC.of(RealConstant(1.0)),
        """{"op":"acsc","exp":1.0}""" isJsonFor TrigonometricOp.ACSC.of(RealConstant(1.0))
    )!!

    private fun deserializationOnlySerializedTopLevelTypeDataProvider() = Stream.of(
        "3E-12" isJsonFor RealConstant(3e-12),
        "3.0e-12" isJsonFor RealConstant(3e-12),
        "3e-12" isJsonFor RealConstant(3e-12),
        "-3E-12" isJsonFor RealConstant(-3e-12),
        "-3.0e-12" isJsonFor RealConstant(-3e-12),
        "-3e-12" isJsonFor RealConstant(-3e-12)
    )!!

    @Suppress("unused")
    fun deserializationSerializedTopLevelTypeDataProvider() = Stream.concat(
        serializedTopLevelTypeDataProvider(),
        deserializationOnlySerializedTopLevelTypeDataProvider()
    )!!
}
