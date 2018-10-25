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
package hu.bme.mit.inf.theta.interchange.jani.model

import hu.bme.mit.inf.theta.interchange.jani.model.json.JaniModelMapper
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DistributionsTest {
    private val objectMapper = JaniModelMapper()

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `serialize distribution sampling expressions as Expression`(
        testCase: SerializationTestCase<DistributionSampling>
    ) {
        testCase.assertSerialized(objectMapper, Expression::class.java)
    }

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `serialize distribution sampling expressions as DistributionSampling`(
        testCase: SerializationTestCase<DistributionSampling>
    ) {
        testCase.assertSerialized(objectMapper, DistributionSampling::class.java)
    }

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `deserialize distribution sampling expressions as Expression`(
        testCase: SerializationTestCase<DistributionSampling>
    ) {
        testCase.assertDeserialized(objectMapper, Expression::class.java)
    }

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `deserialize distribution sampling expressions as DistributionSampling`(
        testCase: SerializationTestCase<DistributionSampling>
    ) {
        testCase.assertDeserialized(objectMapper, DistributionSampling::class.java)
    }

    @Suppress("unused")
    fun serializedTopLevelTypeDataProvider() = Stream.of<SerializationTestCase<DistributionSampling>>(
        """{"distribution":"DiscreteUniform","args":[1,2]}""" isJsonFor
            DiscreteUniform(IntConstant(1), IntConstant(2)),
        """{"distribution":"Bernoulli","args":[0.5]}""" isJsonFor Bernoulli(RealConstant(0.5)),
        """{"distribution":"Binomial","args":[0.5,2]}""" isJsonFor
            Binomial(RealConstant(0.5), IntConstant(2)),
        """{"distribution":"NegativeBinomial","args":[0.5,2]}""" isJsonFor
            NegativeBinomial(RealConstant(0.5), IntConstant(2)),
        """{"distribution":"Poisson","args":[2.0]}""" isJsonFor Poisson(RealConstant(2.0)),
        """{"distribution":"Geometric","args":[0.5]}""" isJsonFor Geometric(RealConstant(0.5)),
        """{"distribution":"Hypergeometric","args":[10,5,2]}""" isJsonFor
            Hypergeometric(IntConstant(10), IntConstant(5), IntConstant(2)),
        """{"distribution":"ConwayMaxwellPoisson","args":[1.0,2.0]}""" isJsonFor
            ConwayMaxwellPoisson(RealConstant(1.0), RealConstant(2.0)),
        """{"distribution":"Zipf","args":[2.0,1]}""" isJsonFor
            Zipf(RealConstant(2.0), IntConstant(1)),
        """{"distribution":"Uniform","args":[1.0,2.0]}""" isJsonFor
            Uniform(RealConstant(1.0), RealConstant(2.0)),
        """{"distribution":"Normal","args":[1.0,2.0]}""" isJsonFor
            Normal(RealConstant(1.0), RealConstant(2.0)),
        """{"distribution":"LogNormal","args":[1.0,2.0]}""" isJsonFor
            LogNormal(RealConstant(1.0), RealConstant(2.0)),
        """{"distribution":"Beta","args":[1.0,2.0]}""" isJsonFor
            Beta(RealConstant(1.0), RealConstant(2.0)),
        """{"distribution":"Cauchy","args":[1.0,2.0]}""" isJsonFor
            Cauchy(RealConstant(1.0), RealConstant(2.0)),
        """{"distribution":"Chi","args":[1]}""" isJsonFor Chi(IntConstant(1)),
        """{"distribution":"ChiSquared","args":[1]}""" isJsonFor ChiSquared(IntConstant(1)),
        """{"distribution":"Erlang","args":[2,0.5]}""" isJsonFor
            Erlang(IntConstant(2), RealConstant(0.5)),
        """{"distribution":"Exponential","args":[0.5]}""" isJsonFor Exponential(RealConstant(0.5)),
        """{"distribution":"FisherSnedecor","args":[1,2]}""" isJsonFor
            FisherSnedecor(IntConstant(1), IntConstant(2)),
        """{"distribution":"Gamma","args":[1.0,2.0]}""" isJsonFor
            Gamma(RealConstant(1.0), RealConstant(2.0)),
        """{"distribution":"InverseGamma","args":[1.0,2.0]}""" isJsonFor
            InverseGamma(RealConstant(1.0), RealConstant(2.0)),
        """{"distribution":"Laplace","args":[1.0,2.0]}""" isJsonFor
            Laplace(RealConstant(1.0), RealConstant(2.0)),
        """{"distribution":"Pareto","args":[1.0,2.0]}""" isJsonFor
            Pareto(RealConstant(1.0), RealConstant(2.0)),
        """{"distribution":"Rayleigh","args":[0.5]}""" isJsonFor Rayleigh(RealConstant(0.5)),
        """{"distribution":"Stable","args":[2.0,1.0,3.0,4.0]}""" isJsonFor Stable(
            RealConstant(2.0), RealConstant(1.0), RealConstant(3.0), RealConstant(4.0)
        ),
        """{"distribution":"StudentT","args":[1.0,2.0,3.0]}""" isJsonFor
            StudentT(RealConstant(1.0), RealConstant(2.0), RealConstant(3.0)),
        """{"distribution":"Weibull","args":[1.0,2.0]}""" isJsonFor
            Weibull(RealConstant(1.0), RealConstant(2.0)),
        """{"distribution":"Triangular","args":[1.0,2.0,3.0]}""" isJsonFor
            Triangular(RealConstant(1.0), RealConstant(2.0), RealConstant(3.0))
    )!!
}
