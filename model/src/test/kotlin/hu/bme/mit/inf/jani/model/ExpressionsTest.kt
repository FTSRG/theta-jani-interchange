package hu.bme.mit.inf.jani.model

import hu.bme.mit.inf.jani.model.json.JaniModelMapper
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpressionsTest {
    private val objectMapper = JaniModelMapper()

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `serialize expressions on the top level`(testCase: SerializationTestCase<Expression>) {
        testCase.assertSerialized(objectMapper, Expression::class.java)
    }

    @ParameterizedTest
    @MethodSource("deserializationSerializedTopLevelTypeDataProvider")
    fun `deserialize expressions on the top level`(testCase: SerializationTestCase<Expression>) {
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

            "\"foo\"" isJsonFor Identifier("foo"),

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

            """{"op":"filter","fun":"min","values":"a","states":"b"}""" isJsonFor
                    Filter.MIN.of(Identifier("a"), Identifier("b")),
            """{"op":"filter","fun":"max","values":"a","states":"b"}""" isJsonFor
                    Filter.MAX.of(Identifier("a"), Identifier("b")),
            """{"op":"filter","fun":"sum","values":"a","states":"b"}""" isJsonFor
                    Filter.SUM.of(Identifier("a"), Identifier("b")),
            """{"op":"filter","fun":"avg","values":"a","states":"b"}""" isJsonFor
                    Filter.AVG.of(Identifier("a"), Identifier("b")),
            """{"op":"filter","fun":"count","values":"a","states":"b"}""" isJsonFor
                    Filter.COUNT.of(Identifier("a"), Identifier("b")),
            """{"op":"filter","fun":"∀","values":"a","states":"b"}""" isJsonFor
                    Filter.FORALL.of(Identifier("a"), Identifier("b")),
            """{"op":"filter","fun":"∃","values":"a","states":"b"}""" isJsonFor
                    Filter.EXISTS.of(Identifier("a"), Identifier("b")),
            """{"op":"filter","fun":"argmin","values":"a","states":"b"}""" isJsonFor
                    Filter.ARGMIN.of(Identifier("a"), Identifier("b")),
            """{"op":"filter","fun":"argmax","values":"a","states":"b"}""" isJsonFor
                    Filter.ARGMAX.of(Identifier("a"), Identifier("b")),
            """{"op":"filter","fun":"values","values":"a","states":"b"}""" isJsonFor
                    Filter.VALUES.of(Identifier("a"), Identifier("b")),

            """{"op":"Pmin","exp":true}""" isJsonFor ProbabilityOp.MIN.of(BoolConstant.TRUE),
            """{"op":"Pmax","exp":true}""" isJsonFor ProbabilityOp.MAX.of(BoolConstant.TRUE),

            """{"op":"∀","exp":true}""" isJsonFor PathQuantifier.FORALL.of(BoolConstant.TRUE),
            """{"op":"∃","exp":true}""" isJsonFor PathQuantifier.EXISTS.of(BoolConstant.TRUE),

            """{"op":"Emin","exp":"a"}""" isJsonFor ExpectationOp.MIN.of(Identifier("a")),
            """{"op":"Emax","exp":"a","accumulate":"steps"}""" isJsonFor
                    ExpectationOp.MAX.of(Identifier("a"), accumulate = RewardAccumulation.STEPS),
            """{"op":"Emin","exp":"a","accumulate":"time"}""" isJsonFor
                    ExpectationOp.MIN.of(Identifier("a"), accumulate = RewardAccumulation.TIME),
            """{"op":"Emin","exp":"a","reach":true}""" isJsonFor
                    ExpectationOp.MIN.of(Identifier("a"), reach = BoolConstant.TRUE),
            """{"op":"Emin","exp":"a","step-instant":1}""" isJsonFor
                    ExpectationOp.MIN.of(Identifier("a"), stepInstant = IntConstant(1)),
            """{"op":"Emin","exp":"a","time-instant":1.0}""" isJsonFor
                    ExpectationOp.MIN.of(Identifier("a"), timeInstant = RealConstant(1.0)),
            """{"op":"Emin","exp":"a","reward-instants":[{"exp":"b","accumulate":"steps","instant":1}]}""" isJsonFor
                    ExpectationOp.MIN.of(
                            Identifier("a"),
                            rewardInstants = listOf(
                                    RewardInstant(Identifier("b"), RewardAccumulation.STEPS, IntConstant(1))
                            )
                    ),
            """{"op":"Emin","exp":"a","reward-instants":[{"exp":"b","accumulate":"time","instant":1.0}]}""" isJsonFor
                    ExpectationOp.MIN.of(
                            Identifier("a"),
                            rewardInstants = listOf(
                                    RewardInstant(
                                            Identifier("b"), RewardAccumulation.TIME, RealConstant(1.0)
                                    )
                            )
                    ),
            """{"op":"Emin","exp":"a","reward-instants":[{"exp":"b","accumulate":"steps","instant":1},{"exp":"c","accumulate":"time","instant":1.0}]}""" isJsonFor
                    ExpectationOp.MIN.of(
                            Identifier("a"),
                            rewardInstants = listOf(
                                    RewardInstant(Identifier("b"), RewardAccumulation.STEPS, IntConstant(1)),
                                    RewardInstant(
                                            Identifier("c"), RewardAccumulation.TIME, RealConstant(1.0)
                                    )
                            )
                    ),

            """{"op":"U","left":true,"right":false}""" isJsonFor
                    BinaryPathOp.U.of(BoolConstant.TRUE, BoolConstant.FALSE),
            """{"op":"W","left":true,"right":false}""" isJsonFor
                    BinaryPathOp.W.of(BoolConstant.TRUE, BoolConstant.FALSE),
            """{"op":"U","left":true,"right":false,"step-bounds":{}}""" isJsonFor
                    BinaryPathOp.U.of(BoolConstant.TRUE, BoolConstant.FALSE, stepBounds = PropertyInterval()),
            """{"op":"U","left":true,"right":false,"step-bounds":{"lower":1}}""" isJsonFor BinaryPathOp.U.of(
                    BoolConstant.TRUE, BoolConstant.FALSE, stepBounds = PropertyInterval(lower = IntConstant(1))
            ),
            """{"op":"U","left":true,"right":false,"step-bounds":{"lower":1,"lower-exclusive":true}}""" isJsonFor
                    BinaryPathOp.U.of(
                            BoolConstant.TRUE, BoolConstant.FALSE,
                            stepBounds = PropertyInterval(lower = IntConstant(1), lowerExclusive = true)
                    ),
            """{"op":"U","left":true,"right":false,"step-bounds":{"upper":1}}""" isJsonFor BinaryPathOp.U.of(
                    BoolConstant.TRUE, BoolConstant.FALSE, stepBounds = PropertyInterval(upper = IntConstant(1))
            ),
            """{"op":"U","left":true,"right":false,"step-bounds":{"upper":1,"upper-exclusive":true}}""" isJsonFor
                    BinaryPathOp.U.of(
                            BoolConstant.TRUE, BoolConstant.FALSE,
                            stepBounds = PropertyInterval(upper = IntConstant(1), upperExclusive = true)
                    ),
            """{"op":"U","left":true,"right":false,"time-bounds":{"lower":1.0}}""" isJsonFor BinaryPathOp.U.of(
                    BoolConstant.TRUE, BoolConstant.FALSE,
                    timeBounds = PropertyInterval(lower = RealConstant(1.0))
            ),
            """{"op":"U","left":true,"right":false,"reward-bounds":[{"exp":"a","accumulate":"steps","bounds":{"lower":1}}]}""" isJsonFor
                    BinaryPathOp.U.of(
                            BoolConstant.TRUE, BoolConstant.FALSE,
                            rewardBounds = listOf(
                                    RewardBound(
                                            Identifier("a"), RewardAccumulation.STEPS,
                                            PropertyInterval(lower = IntConstant(1))
                                    )
                            )
                    ),
            """{"op":"U","left":true,"right":false,"reward-bounds":[{"exp":"a","accumulate":"steps","bounds":{"lower":1}},{"exp":"b","accumulate":"time","bounds":{"upper":1.0,"upper-exclusive":true}}]}""" isJsonFor
                    BinaryPathOp.U.of(
                            BoolConstant.TRUE, BoolConstant.FALSE,
                            rewardBounds = listOf(
                                    RewardBound(
                                            Identifier("a"), RewardAccumulation.STEPS,
                                            PropertyInterval(lower = IntConstant(1))
                                    ),
                                    RewardBound(
                                            Identifier("b"), RewardAccumulation.TIME,
                                            PropertyInterval(upper = RealConstant(1.0), upperExclusive = true)
                                    )
                            )
                    ),

            """{"op":"Smin","exp":true}""" isJsonFor SteadyStateOp.MIN.of(BoolConstant.TRUE),
            """{"op":"Smax","exp":true}""" isJsonFor SteadyStateOp.MAX.of(BoolConstant.TRUE),

            """{"op":"initial"}""" isJsonFor StatePredicate.INITIAL,
            """{"op":"deadlock"}""" isJsonFor StatePredicate.DEADLOCK,
            """{"op":"timelock"}""" isJsonFor StatePredicate.TIMELOCK,

            """{"op":"aa","exp":"a","index":0}""" isJsonFor ArrayAccess(Identifier("a"), IntConstant(0)),

            """{"op":"av","elements":[]}""" isJsonFor ArrayValue(emptyList()),
            """{"op":"av","elements":[1,2]}""" isJsonFor ArrayValue(IntConstant(1), IntConstant(2)),

            """{"op":"ac","var":"x","length":5,"exp":1}""" isJsonFor
                    ArrayConstructor("x", IntConstant(5), IntConstant(1)),

            """{"op":"da","exp":"a","member":"b"}""" isJsonFor DatatypeMemberAccess(Identifier("a"), "b"),

            """{"op":"dv","type":"foo","values":[]}""" isJsonFor DatatypeValue("foo", emptyList()),
            """{"op":"dv","type":"foo","values":[{"member":"m1","value":1}]}""" isJsonFor
                    DatatypeValue("foo", listOf(DatatypeMemberValue("m1", IntConstant(1)))),
            """{"op":"dv","type":"foo","values":[{"member":"m1","value":1},{"member":"m2","value":true}]}""" isJsonFor
                    DatatypeValue("foo", listOf(
                            DatatypeMemberValue("m1", IntConstant(1)),
                            DatatypeMemberValue("m2", BoolConstant.TRUE)
                    )),

            """{"op":"oa","exp":"a"}""" isJsonFor OptionValueAccess(Identifier("a")),

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

            """{"op":"R","left":true,"right":false}""" isJsonFor
                    DerivedBinaryPathOp.R.of(BoolConstant.TRUE, BoolConstant.FALSE),

            """{"op":"F","exp":true}""" isJsonFor UnaryPathOp.F.of(BoolConstant.TRUE),
            """{"op":"G","exp":true}""" isJsonFor UnaryPathOp.G.of(BoolConstant.TRUE),
            """{"op":"F","exp":true,"step-bounds":{}}""" isJsonFor
                    UnaryPathOp.F.of(BoolConstant.TRUE, stepBounds = PropertyInterval()),
            """{"op":"F","exp":true,"step-bounds":{"lower":1}}""" isJsonFor
                    UnaryPathOp.F.of(BoolConstant.TRUE, stepBounds = PropertyInterval(lower = IntConstant(1))),
            """{"op":"F","exp":true,"step-bounds":{"lower":1,"lower-exclusive":true}}""" isJsonFor UnaryPathOp.F.of(
                    BoolConstant.TRUE,
                    stepBounds = PropertyInterval(lower = IntConstant(1), lowerExclusive = true)
            ),
            """{"op":"F","exp":true,"step-bounds":{"upper":1}}""" isJsonFor
                    UnaryPathOp.F.of(BoolConstant.TRUE, stepBounds = PropertyInterval(upper = IntConstant(1))),
            """{"op":"F","exp":true,"step-bounds":{"upper":1,"upper-exclusive":true}}""" isJsonFor UnaryPathOp.F.of(
                    BoolConstant.TRUE,
                    stepBounds = PropertyInterval(upper = IntConstant(1), upperExclusive = true)
            ),
            """{"op":"F","exp":true,"time-bounds":{"lower":1.0}}""" isJsonFor
                    UnaryPathOp.F.of(BoolConstant.TRUE, timeBounds = PropertyInterval(lower = RealConstant(1.0))),
            """{"op":"F","exp":true,"reward-bounds":[{"exp":"a","accumulate":"steps","bounds":{"lower":1}}]}""" isJsonFor
                    UnaryPathOp.F.of(
                            BoolConstant.TRUE,
                            rewardBounds = listOf(
                                    RewardBound(
                                            Identifier("a"), RewardAccumulation.STEPS,
                                            PropertyInterval(lower = IntConstant(1))
                                    )
                            )
                    ),
            """{"op":"F","exp":true,"reward-bounds":[{"exp":"a","accumulate":"steps","bounds":{"lower":1}},{"exp":"b","accumulate":"time","bounds":{"upper":1.0,"upper-exclusive":true}}]}""" isJsonFor
                    UnaryPathOp.F.of(
                            BoolConstant.TRUE,
                            rewardBounds = listOf(
                                    RewardBound(
                                            Identifier("a"), RewardAccumulation.STEPS,
                                            PropertyInterval(lower = IntConstant(1))
                                    ),
                                    RewardBound(
                                            Identifier("b"), RewardAccumulation.TIME,
                                            PropertyInterval(upper = RealConstant(1.0), upperExclusive = true)
                                    )
                            )
                    ),

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

            """{"op":"Emin","exp":"a","accumulate":"exit"}""" isJsonFor
                    ExpectationOp.MIN.of(Identifier("a"), accumulate = RewardAccumulation.EXIT),
            """{"op":"Emin","exp":"a","reward-instants":[{"exp":"b","accumulate":"exit","instant":1.0}]}""" isJsonFor
                    ExpectationOp.MIN.of(
                            Identifier("a"),
                            rewardInstants = listOf(
                                    RewardInstant(
                                            Identifier("b"), RewardAccumulation.EXIT, RealConstant(1.0)
                                    )
                            )
                    ),
            """{"op":"U","left":true,"right":false,"reward-bounds":[{"exp":"a","accumulate":"exit","bounds":{"lower":1}}]}""".trimMargin() isJsonFor
                    BinaryPathOp.U.of(
                            BoolConstant.TRUE, BoolConstant.FALSE,
                            rewardBounds = listOf(
                                    RewardBound(
                                            Identifier("a"), RewardAccumulation.EXIT,
                                            PropertyInterval(lower = IntConstant(1))
                                    )
                            )
                    ),
            """{"op":"F","exp":true,"reward-bounds":[{"exp":"a","accumulate":"exit","bounds":{"lower":1}}]}""" isJsonFor
                    UnaryPathOp.F.of(
                            BoolConstant.TRUE,
                            rewardBounds = listOf(
                                    RewardBound(
                                            Identifier("a"), RewardAccumulation.EXIT,
                                            PropertyInterval(lower = IntConstant(1))
                                    )
                            )
                    ),

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
            "-3e-12" isJsonFor RealConstant(-3e-12),

            """{"op":"Emin","exp":"a","reward-instants":[]}""" isJsonFor ExpectationOp.MIN.of(Identifier("a")),

            """{"op":"U","left":true,"right":false,"step-bounds":{"lower":1,"lower-exclusive":false}}""" isJsonFor
                    BinaryPathOp.U.of(
                            BoolConstant.TRUE, BoolConstant.FALSE,
                            stepBounds = PropertyInterval(lower = IntConstant(1))
                    ),
            """{"op":"U","left":true,"right":false,"step-bounds":{"upper":1,"upper-exclusive":false}}""" isJsonFor
                    BinaryPathOp.U.of(
                            BoolConstant.TRUE, BoolConstant.FALSE,
                            stepBounds = PropertyInterval(upper = IntConstant(1))
                    ),
            """{"op":"U","left":true,"right":false,"reward-bounds":[]}""" isJsonFor
                    BinaryPathOp.U.of(BoolConstant.TRUE, BoolConstant.FALSE),

            """{"op":"F","exp":true,"step-bounds":{"lower":1,"lower-exclusive":false}}""" isJsonFor
                    UnaryPathOp.F.of(BoolConstant.TRUE, stepBounds = PropertyInterval(lower = IntConstant(1))),
            """{"op":"F","exp":true,"step-bounds":{"upper":1,"upper-exclusive":false}}""" isJsonFor
                    UnaryPathOp.F.of(BoolConstant.TRUE, stepBounds = PropertyInterval(upper = IntConstant(1))),
            """{"op":"F","exp":true,"reward-bounds":[]}""" isJsonFor UnaryPathOp.F.of(BoolConstant.TRUE)
    )!!

    @Suppress("unused")
    fun deserializationSerializedTopLevelTypeDataProvider() = Stream.concat(
            serializedTopLevelTypeDataProvider(),
            deserializationOnlySerializedTopLevelTypeDataProvider()
    )!!
}