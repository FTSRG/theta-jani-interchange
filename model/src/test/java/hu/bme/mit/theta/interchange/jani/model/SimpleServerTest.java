package hu.bme.mit.theta.interchange.jani.model;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import hu.bme.mit.theta.interchange.jani.model.json.*;
import org.junit.jupiter.api.*;

import java.util.*;

public class SimpleServerTest {
    @Test
    public void testSimpleServer() throws JsonProcessingException {
        final Model model = new Model(
                1,
                "simple-server",
                new Metadata(
                        "0.1",
                        "Kristóf Marussy",
                        "Basic example model",
                        null,
                        null
                ),
                ModelType.CTMC,
                Collections.emptySet(),
                Collections.emptyList(),
                Arrays.asList(
                        new ConstantDeclaration(
                                "requestRate",
                                new BoundedType(
                                        RealType.INSTANCE,
                                        new RealConstant(0.15),
                                        new RealConstant(15)
                                ),
                                null /* new RealConstant(1.5) */,
                                null
                        ),
                        new ConstantDeclaration(
                                "serviceTime",
                                new BoundedType(
                                        RealType.INSTANCE,
                                        new RealConstant(0.025),
                                        new RealConstant(2.5)
                                ),
                                null /* new RealConstant(0.25) */,
                                null
                        )
                ),
                Arrays.asList(
                        new VariableDeclaration(
                                "idle",
                                new BoundedType(
                                        IntType.INSTANCE,
                                        new IntConstant(0),
                                        null
                                ),
                                false,
                                new IntConstant(1),
                                null
                        ),
                        new VariableDeclaration(
                                "serving",
                                new BoundedType(
                                        IntType.INSTANCE,
                                        new IntConstant(0),
                                        null
                                ),
                                false,
                                new IntConstant(0),
                                null
                        )
                ),
                null,
                Arrays.asList(
                        new Property(
                                "Idle",
                                Filter.MIN.of(
                                        SteadyStateOp.MIN.of(new Identifier("idle")),
                                        BoolConstant.TRUE
                                ),
                                null
                        ),
                        new Property(
                                "ServedRequests",
                                Filter.MIN.of(
                                        SteadyStateOp.MIN.of(
                                                new Ite(
                                                        BinaryOp.LEQ.of(
                                                                new IntConstant(1),
                                                                new Identifier("serving")
                                                        ),
                                                        BinaryOp.DIV.of(
                                                                new RealConstant(1),
                                                                new Identifier("serviceTime")
                                                        ),
                                                        new RealConstant(0)
                                                )
                                        ),
                                        BoolConstant.TRUE
                                ),
                                null
                        )
                ),
                Collections.singletonList(
                        new Automaton(
                                "simple-server-automaton",
                                Collections.emptyList(),
                                null,
                                Collections.singletonList(
                                        new Location(
                                                "loc",
                                                null,
                                                Collections.emptyList(),
                                                null
                                        )
                                ),
                                Collections.singletonList("loc"),
                                Arrays.asList(
                                        new Edge(
                                                "loc",
                                                null,
                                                new CommentedExpression(
                                                        new Identifier("requestRate"),
                                                        null
                                                ),
                                                new CommentedExpression(
                                                        BinaryOp.LEQ.of(
                                                                new IntConstant(1),
                                                                new Identifier("idle")
                                                        ),
                                                        null
                                                ),
                                                Collections.singletonList(
                                                        new Destination(
                                                                "loc",
                                                                new CommentedExpression(
                                                                        new RealConstant(1),
                                                                        null
                                                                ),
                                                                Arrays.asList(
                                                                        new Assignment(
                                                                                new Identifier("idle"),
                                                                                BinaryOp.SUB.of(
                                                                                        new Identifier("idle"),
                                                                                        new IntConstant(1)
                                                                                ),
                                                                                0,
                                                                                null
                                                                        ),
                                                                        new Assignment(
                                                                                new Identifier("serving"),
                                                                                BinaryOp.ADD.of(
                                                                                        new Identifier("serving"),
                                                                                        new IntConstant(1)
                                                                                ),
                                                                                0,
                                                                                null
                                                                        )
                                                                ),
                                                                null
                                                        )
                                                ),
                                                null,
                                                null
                                        ),
                                        new Edge(
                                                "loc",
                                                null,
                                                new CommentedExpression(
                                                        BinaryOp.DIV.of(
                                                                new RealConstant(1),
                                                                new Identifier("serviceTime")
                                                        ),
                                                        null
                                                ),
                                                new CommentedExpression(
                                                        BinaryOp.LEQ.of(
                                                                new IntConstant(1),
                                                                new Identifier("serving")
                                                        ),
                                                        null
                                                ),
                                                Collections.singletonList(
                                                        new Destination(
                                                                "loc",
                                                                new CommentedExpression(
                                                                        new RealConstant(1),
                                                                        null
                                                                ),
                                                                Arrays.asList(
                                                                        new Assignment(
                                                                                new Identifier("serving"),
                                                                                BinaryOp.SUB.of(
                                                                                        new Identifier("serving"),
                                                                                        new IntConstant(1)
                                                                                ),
                                                                                0,
                                                                                null
                                                                        ),
                                                                        new Assignment(
                                                                                new Identifier("idle"),
                                                                                BinaryOp.ADD.of(
                                                                                        new Identifier("idle"),
                                                                                        new IntConstant(1)
                                                                                ),
                                                                                0,
                                                                                null
                                                                        )
                                                                ),
                                                                null
                                                        )
                                                ),
                                                null,
                                                null
                                        )
                                ),
                                null,
                                Collections.emptyList()
                        )
                ),
                new Composition(
                        Collections.singletonList(
                                new AutomatonInstance(
                                        "simple-server-automaton",
                                        Collections.emptyList(),
                                        null
                                )
                        ),
                        Collections.emptyList(),
                        null
                ),
                Collections.emptyList(),
                Collections.emptyList()
        );

        final JaniModelMapper janiModelMapper = new JaniModelMapper();
        janiModelMapper.enable(SerializationFeature.INDENT_OUTPUT);

        final String json = janiModelMapper.writeValueAsString(model);

        final String expected = "{\n" +
                "  \"jani-version\" : 1,\n" +
                "  \"name\" : \"simple-server\",\n" +
                "  \"metadata\" : {\n" +
                "    \"version\" : \"0.1\",\n" +
                "    \"author\" : \"Kristóf Marussy\",\n" +
                "    \"description\" : \"Basic example model\"\n" +
                "  },\n" +
                "  \"type\" : \"ctmc\",\n" +
                "  \"constants\" : [ {\n" +
                "    \"name\" : \"requestRate\",\n" +
                "    \"type\" : {\n" +
                "      \"kind\" : \"bounded\",\n" +
                "      \"base\" : \"real\",\n" +
                "      \"lower-bound\" : 0.15,\n" +
                "      \"upper-bound\" : 15.0\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"name\" : \"serviceTime\",\n" +
                "    \"type\" : {\n" +
                "      \"kind\" : \"bounded\",\n" +
                "      \"base\" : \"real\",\n" +
                "      \"lower-bound\" : 0.025,\n" +
                "      \"upper-bound\" : 2.5\n" +
                "    }\n" +
                "  } ],\n" +
                "  \"variables\" : [ {\n" +
                "    \"name\" : \"idle\",\n" +
                "    \"type\" : {\n" +
                "      \"kind\" : \"bounded\",\n" +
                "      \"base\" : \"int\",\n" +
                "      \"lower-bound\" : 0\n" +
                "    },\n" +
                "    \"transient\" : false,\n" +
                "    \"initial-value\" : 1\n" +
                "  }, {\n" +
                "    \"name\" : \"serving\",\n" +
                "    \"type\" : {\n" +
                "      \"kind\" : \"bounded\",\n" +
                "      \"base\" : \"int\",\n" +
                "      \"lower-bound\" : 0\n" +
                "    },\n" +
                "    \"transient\" : false,\n" +
                "    \"initial-value\" : 0\n" +
                "  } ],\n" +
                "  \"properties\" : [ {\n" +
                "    \"name\" : \"Idle\",\n" +
                "    \"expression\" : {\n" +
                "      \"op\" : \"filter\",\n" +
                "      \"fun\" : \"min\",\n" +
                "      \"values\" : {\n" +
                "        \"op\" : \"Smin\",\n" +
                "        \"exp\" : \"idle\"\n" +
                "      },\n" +
                "      \"states\" : true\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"name\" : \"ServedRequests\",\n" +
                "    \"expression\" : {\n" +
                "      \"op\" : \"filter\",\n" +
                "      \"fun\" : \"min\",\n" +
                "      \"values\" : {\n" +
                "        \"op\" : \"Smin\",\n" +
                "        \"exp\" : {\n" +
                "          \"op\" : \"ite\",\n" +
                "          \"if\" : {\n" +
                "            \"op\" : \"≤\",\n" +
                "            \"left\" : 1,\n" +
                "            \"right\" : \"serving\"\n" +
                "          },\n" +
                "          \"then\" : {\n" +
                "            \"op\" : \"/\",\n" +
                "            \"left\" : 1.0,\n" +
                "            \"right\" : \"serviceTime\"\n" +
                "          },\n" +
                "          \"else\" : 0.0\n" +
                "        }\n" +
                "      },\n" +
                "      \"states\" : true\n" +
                "    }\n" +
                "  } ],\n" +
                "  \"automata\" : [ {\n" +
                "    \"name\" : \"simple-server-automaton\",\n" +
                "    \"locations\" : [ {\n" +
                "      \"name\" : \"loc\"\n" +
                "    } ],\n" +
                "    \"initial-locations\" : [ \"loc\" ],\n" +
                "    \"edges\" : [ {\n" +
                "      \"location\" : \"loc\",\n" +
                "      \"rate\" : {\n" +
                "        \"exp\" : \"requestRate\"\n" +
                "      },\n" +
                "      \"guard\" : {\n" +
                "        \"exp\" : {\n" +
                "          \"op\" : \"≤\",\n" +
                "          \"left\" : 1,\n" +
                "          \"right\" : \"idle\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"destinations\" : [ {\n" +
                "        \"location\" : \"loc\",\n" +
                "        \"probability\" : {\n" +
                "          \"exp\" : 1.0\n" +
                "        },\n" +
                "        \"assignments\" : [ {\n" +
                "          \"ref\" : \"idle\",\n" +
                "          \"value\" : {\n" +
                "            \"op\" : \"-\",\n" +
                "            \"left\" : \"idle\",\n" +
                "            \"right\" : 1\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"ref\" : \"serving\",\n" +
                "          \"value\" : {\n" +
                "            \"op\" : \"+\",\n" +
                "            \"left\" : \"serving\",\n" +
                "            \"right\" : 1\n" +
                "          }\n" +
                "        } ]\n" +
                "      } ]\n" +
                "    }, {\n" +
                "      \"location\" : \"loc\",\n" +
                "      \"rate\" : {\n" +
                "        \"exp\" : {\n" +
                "          \"op\" : \"/\",\n" +
                "          \"left\" : 1.0,\n" +
                "          \"right\" : \"serviceTime\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"guard\" : {\n" +
                "        \"exp\" : {\n" +
                "          \"op\" : \"≤\",\n" +
                "          \"left\" : 1,\n" +
                "          \"right\" : \"serving\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"destinations\" : [ {\n" +
                "        \"location\" : \"loc\",\n" +
                "        \"probability\" : {\n" +
                "          \"exp\" : 1.0\n" +
                "        },\n" +
                "        \"assignments\" : [ {\n" +
                "          \"ref\" : \"serving\",\n" +
                "          \"value\" : {\n" +
                "            \"op\" : \"-\",\n" +
                "            \"left\" : \"serving\",\n" +
                "            \"right\" : 1\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"ref\" : \"idle\",\n" +
                "          \"value\" : {\n" +
                "            \"op\" : \"+\",\n" +
                "            \"left\" : \"idle\",\n" +
                "            \"right\" : 1\n" +
                "          }\n" +
                "        } ]\n" +
                "      } ]\n" +
                "    } ]\n" +
                "  } ],\n" +
                "  \"system\" : {\n" +
                "    \"elements\" : [ {\n" +
                "      \"automaton\" : \"simple-server-automaton\"\n" +
                "    } ]\n" +
                "  }\n" +
                "}";

        Assertions.assertEquals(expected, json);
    }
}
