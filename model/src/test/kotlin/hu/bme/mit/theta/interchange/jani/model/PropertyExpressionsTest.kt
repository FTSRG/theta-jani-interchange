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
class PropertyExpressionsTest {
    private val objectMapper = JaniModelMapper()

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `serialize property expressions`(testCase: SerializationTestCase<PropertyExpression>) {
        testCase.assertSerialized(objectMapper, PropertyExpression::class.java)
    }

    @ParameterizedTest
    @MethodSource("deserializationSerializedTopLevelTypeDataProvider")
    fun `deserialize property expressions`(testCase: SerializationTestCase<PropertyExpression>) {
        testCase.assertDeserialized(objectMapper, PropertyExpression::class.java)
    }

    @Suppress("unused")
    fun serializedTopLevelTypeDataProvider() = Stream.of(
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
        """{"op":"Emax","exp":"a","accumulate":["steps"]}""" isJsonFor
            ExpectationOp.MAX.of(Identifier("a"), accumulate = setOf(RewardAccumulation.STEPS)),
        """{"op":"Emin","exp":"a","accumulate":["time"]}""" isJsonFor
            ExpectationOp.MIN.of(Identifier("a"), accumulate = setOf(RewardAccumulation.TIME)),
        """{"op":"Emin","exp":"a","accumulate":["steps","time"]}""" isJsonFor ExpectationOp.MIN.of(
            Identifier("a"), accumulate = setOf(RewardAccumulation.STEPS, RewardAccumulation.TIME)
        ),
        """{"op":"Emin","exp":"a","reach":true}""" isJsonFor
            ExpectationOp.MIN.of(Identifier("a"), reach = BoolConstant.TRUE),
        """{"op":"Emin","exp":"a","step-instant":1}""" isJsonFor
            ExpectationOp.MIN.of(Identifier("a"), stepInstant = IntConstant(1)),
        """{"op":"Emin","exp":"a","time-instant":1.0}""" isJsonFor
            ExpectationOp.MIN.of(Identifier("a"), timeInstant = RealConstant(1.0)),
        """{"op":"Emin","exp":"a","reward-instants":[{"exp":"b","accumulate":["steps"],"instant":1}]}""" isJsonFor
            ExpectationOp.MIN.of(
                Identifier("a"),
                rewardInstants = listOf(
                    RewardInstant(
                        Identifier("b"), setOf(RewardAccumulation.STEPS),
                        IntConstant(1)
                    )
                )
            ),
        """{"op":"Emin","exp":"a","reward-instants":[{"exp":"b","accumulate":["time"],"instant":1.0}]}""" isJsonFor
            ExpectationOp.MIN.of(
                Identifier("a"),
                rewardInstants = listOf(
                    RewardInstant(
                        Identifier("b"), setOf(RewardAccumulation.TIME),
                        RealConstant(1.0)
                    )
                )
            ),
        """{"op":"Emin","exp":"a","reward-instants":[{"exp":"b","accumulate":["steps","time"],"instant":1.0}]}""" isJsonFor
            ExpectationOp.MIN.of(
                Identifier("a"),
                rewardInstants = listOf(
                    RewardInstant(
                        Identifier("b"),
                        setOf(RewardAccumulation.STEPS, RewardAccumulation.TIME),
                        RealConstant(1.0)
                    )
                )
            ),
        """{"op":"Emin","exp":"a","reward-instants":[{"exp":"b","accumulate":["steps"],"instant":1},{"exp":"c","accumulate":["time"],"instant":1.0}]}""" isJsonFor
            ExpectationOp.MIN.of(
                Identifier("a"),
                rewardInstants = listOf(
                    RewardInstant(
                        Identifier("b"), setOf(RewardAccumulation.STEPS),
                        IntConstant(1)),
                    RewardInstant(
                        Identifier("c"), setOf(RewardAccumulation.TIME),
                        RealConstant(1.0)
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
        """{"op":"U","left":true,"right":false,"reward-bounds":[{"exp":"a","accumulate":["steps"],"bounds":{"lower":1}}]}""" isJsonFor
            BinaryPathOp.U.of(
                BoolConstant.TRUE, BoolConstant.FALSE,
                rewardBounds = listOf(
                    RewardBound(
                        Identifier("a"), setOf(RewardAccumulation.STEPS),
                        PropertyInterval(lower = IntConstant(1))
                    )
                )
            ),
        """{"op":"U","left":true,"right":false,"reward-bounds":[{"exp":"a","accumulate":["steps","time"],"bounds":{"lower":1}}]}""" isJsonFor
            BinaryPathOp.U.of(
                BoolConstant.TRUE, BoolConstant.FALSE,
                rewardBounds = listOf(
                    RewardBound(
                        Identifier("a"),
                        setOf(RewardAccumulation.STEPS, RewardAccumulation.TIME),
                        PropertyInterval(lower = IntConstant(1))
                    )
                )
            ),
        """{"op":"U","left":true,"right":false,"reward-bounds":[{"exp":"a","accumulate":["steps"],"bounds":{"lower":1}},{"exp":"b","accumulate":["time"],"bounds":{"upper":1.0,"upper-exclusive":true}}]}""" isJsonFor
            BinaryPathOp.U.of(
                BoolConstant.TRUE, BoolConstant.FALSE,
                rewardBounds = listOf(
                    RewardBound(
                        Identifier("a"), setOf(RewardAccumulation.STEPS),
                        PropertyInterval(lower = IntConstant(1))
                    ),
                    RewardBound(
                        Identifier("b"), setOf(RewardAccumulation.TIME),
                        PropertyInterval(upper = RealConstant(1.0), upperExclusive = true)
                    )
                )
            ),

        """{"op":"Smin","exp":true}""" isJsonFor SteadyStateOp.MIN.of(BoolConstant.TRUE),
        """{"op":"Smax","exp":true}""" isJsonFor SteadyStateOp.MAX.of(BoolConstant.TRUE),

        """{"op":"initial"}""" isJsonFor StatePredicate.INITIAL,
        """{"op":"deadlock"}""" isJsonFor StatePredicate.DEADLOCK,
        """{"op":"timelock"}""" isJsonFor StatePredicate.TIMELOCK,

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
        """{"op":"F","exp":true,"reward-bounds":[{"exp":"a","accumulate":["steps"],"bounds":{"lower":1}}]}""" isJsonFor
            UnaryPathOp.F.of(
                BoolConstant.TRUE,
                rewardBounds = listOf(
                    RewardBound(
                        Identifier("a"), setOf(RewardAccumulation.STEPS),
                        PropertyInterval(lower = IntConstant(1))
                    )
                )
            ),

        """{"op":"Emin","exp":"a","accumulate":["exit"]}""" isJsonFor
            ExpectationOp.MIN.of(Identifier("a"), accumulate = setOf(RewardAccumulation.EXIT)),
        """{"op":"Emin","exp":"a","reward-instants":[{"exp":"b","accumulate":["exit"],"instant":1.0}]}""" isJsonFor
            ExpectationOp.MIN.of(
                Identifier("a"),
                rewardInstants = listOf(
                    RewardInstant(
                        Identifier("b"), setOf(RewardAccumulation.EXIT), RealConstant(1.0)
                    )
                )
            ),
        """{"op":"U","left":true,"right":false,"reward-bounds":[{"exp":"a","accumulate":["exit"],"bounds":{"lower":1}}]}""".trimMargin() isJsonFor
            BinaryPathOp.U.of(
                BoolConstant.TRUE, BoolConstant.FALSE,
                rewardBounds = listOf(
                    RewardBound(
                        Identifier("a"), setOf(RewardAccumulation.EXIT),
                        PropertyInterval(lower = IntConstant(1))
                    )
                )
            )
    )!!

    private fun deserializationOnlySerializedTopLevelTypeDataProvider() = Stream.of(
        """{"op":"Emin","exp":"a","accumulate":[]}""" isJsonFor ExpectationOp.MIN.of(Identifier("a")),
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
