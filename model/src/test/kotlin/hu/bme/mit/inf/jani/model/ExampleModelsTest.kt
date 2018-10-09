package hu.bme.mit.inf.jani.model

import hu.bme.mit.inf.jani.model.json.JaniModelMapper
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExampleModelsTest {
    private val objectMapper = JaniModelMapper()

    @ParameterizedTest
    @MethodSource("exampleModelPathsTypeDataProvider")
    fun `deserialize and serialize example models`(modelPath: String) {
        val modelUri = ExampleModelsTest::class.java.getResource(modelPath)
        val model = objectMapper.readValue(modelUri, Model::class.java)
        OutputStreamWriter(ByteArrayOutputStream()).use { writer ->
            objectMapper.writeValue(writer, model)
            // We can't assert that it round-trips, because formatting, order of fields, etc. may change.
        }
    }

    @Suppress("unused")
    fun exampleModelPathsTypeDataProvider() = Stream.of(
            "/3TandemQueue/3tandem_queue-iosa.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00100_010_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00100_010_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00100_050_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00100_050_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00100_100_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00100_100_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00100_500_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00100_500_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00500_010_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00500_010_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00500_050_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00500_050_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00500_100_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00500_100_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00500_500_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_00500_500_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_01000_010_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_01000_010_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_01000_050_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_01000_050_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_01000_100_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_01000_100_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_01000_500_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_01000_500_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_05000_010_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_05000_010_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_05000_050_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_05000_050_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_05000_100_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_05000_100_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_05000_500_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_05000_500_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_10000_010_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_10000_010_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_10000_050_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_10000_050_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_10000_100_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_10000_100_quadrant.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_10000_500_full.jani",
            "/ApproxPi/NaiveRejectionSampling/approx_pi_10000_500_quadrant.jani",
            "/beb-modest/beb-4-3-3.jani",
            "/BRP/brp.jani",
            "/CouponCollector/MultiAllowed/coupon_m_03_02.jani",
            "/CouponCollector/MultiAllowed/coupon_m_03_03.jani",
            "/CouponCollector/MultiAllowed/coupon_m_03_04.jani",
            "/CouponCollector/MultiAllowed/coupon_m_05_02.jani",
            "/CouponCollector/MultiAllowed/coupon_m_05_03.jani",
            "/CouponCollector/MultiAllowed/coupon_m_05_04.jani",
            "/CouponCollector/MultiAllowed/coupon_m_07_02.jani",
            "/CouponCollector/MultiAllowed/coupon_m_07_03.jani",
            "/CouponCollector/MultiAllowed/coupon_m_07_04.jani",
            "/CouponCollector/MultiAllowed/coupon_m_09_02.jani",
            "/CouponCollector/MultiAllowed/coupon_m_09_03.jani",
            "/CouponCollector/MultiAllowed/coupon_m_09_04.jani",
            "/CouponCollector/MultiAllowed/coupon_m_11_02.jani",
            "/CouponCollector/MultiAllowed/coupon_m_11_03.jani",
            "/CouponCollector/MultiAllowed/coupon_m_11_04.jani",
            "/CouponCollector/MultiAllowed/coupon_m_13_02.jani",
            "/CouponCollector/MultiAllowed/coupon_m_13_03.jani",
            "/CouponCollector/MultiAllowed/coupon_m_13_04.jani",
            "/CouponCollector/MultiAllowed/coupon_m_15_02.jani",
            "/CouponCollector/MultiAllowed/coupon_m_15_03.jani",
            "/CouponCollector/MultiAllowed/coupon_m_15_04.jani",
            "/CouponCollector/MultiAllowed/coupon_m_17_02.jani",
            "/CouponCollector/MultiAllowed/coupon_m_17_03.jani",
            "/CouponCollector/MultiAllowed/coupon_m_17_04.jani",
            "/CouponCollector/MultiAllowed/coupon_m_19_02.jani",
            "/CouponCollector/MultiAllowed/coupon_m_19_03.jani",
            "/CouponCollector/MultiAllowed/coupon_m_19_04.jani",
            "/Database/database_R2.jani",
            "/Database/database_R3.jani",
            "/Database/database_R4.jani",
            "/Database/database_R5.jani",
            "/DiningCryptographers/dining_crypt3.jani",
            "/DiningCryptographers/dining_crypt4.jani",
            "/DiningCryptographers/dining_crypt5.jani",
            "/DiningPhilosophers/phil3.jani",
            "/DiningPhilosophers/phil4.jani",
            "/DiningPhilosophers/phil5.jani",
            "/MutualExclusion/mutual3.jani",
            "/MutualExclusion/mutual4.jani",
            "/MutualExclusion/mutual5.jani",
            "/QueuesAndWorkflows/SimpleWorkflow/SimpleWorkflow.jani",
            "/QueueWithBreakdowns/queue_with_breakdowns-iosa.jani",
            "/Rabin/rabin3.jani",
            "/RandomWalk/randomWalk.jani",
            "/TandemQueue/tandem_queue-iosa.jani"
    )!!
}