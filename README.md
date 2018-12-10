# Theta JANI Interchange

This library provides serialization and deserialization of [JANI models](http://www.jani-spec.org/)
on the JVM using [Jackson](https://github.com/FasterXML/jackson).

Currently, there is support for manipulating and (de)serializing `jani-model` files (including extensions)
as an AST, which can primarily aid in the development of JANI transformers and middleware.
We plan adding semantic helpers for `jani-model`, such as validation, scoping,
and resolution of identifiers in the near future,
as well as (de)serialization for `jani-interaction` messages.
We would also like to create a JVM helper from writing command line and WebSocket
JANI clients and servers.

This library is developed as part of the [Theta](https://inf.mit.bme.hu/en/theta)
model checking framework. However, there is no direct dependency to Theta.
We aim the library to be not only useful for adding JANI frontends to Theta,
but for general use in JANI-based model checkers and middleware on the JVM.

## Usage and development

Theta JANI Interchange is written in [Kotlin](https://kotlinlang.org/), a concise and safe language.
In particular, data classes are used throughout for representing ASTs.
However, the library can be also used from Java and other JVM languages.
Only the JVM backend of Kotlin is supported at the moment, because JSON handling depends on Jackson,
a library written for the JVM.

To install the library to your local Maven repository, run

```
./gradlew publishToMavenLocal
```

Before submitting patches for `jani-model`, please run its test suite by invoking

```
./gradlew test
```

At the moment, the test suite contains tests for all JANI expression AST nodes.
In addition,
the [library of formal models in jani-model format](https://github.com/ahartmanns/jani-models/)
maintained by Arnd Hartmanns is referenced for smoke testing (de)serialization of whole models.

## License

Copyright 2018 Contributors to the Theta project

The contributors to the Theta project are listed in
[CONTRIBUTORS.md](https://github.com/FTSRG/theta-jani-interchange/blob/master/CONTRIBUTORS.md).

Theta JANI Interchange is licensed under the Apache License, Version 2.0.
For more information, see [LICENSE](https://github.com/FTSRG/theta-jani-interchange/blob/master/LICENSE)
and the corresponding notices in [NOTICE](https://github.com/FTSRG/theta-jani-interchange/blob/master/NOTICE).

## Acknowledgement

We thank members of the [JANI specification joint project](http://www.jani-spec.org/impressum.html).

The project was partially supported by the
[MTA-BME Lendület Cyber-Physical Systems Research Group](http://lendulet.inf.mit.bme.hu/)
and the [Fault Tolerant Systems Research Group](https://inf.mit.bme.hu/en)
of the Department of Measurement and Information Systems,
Budapest University of Technology and Economics.