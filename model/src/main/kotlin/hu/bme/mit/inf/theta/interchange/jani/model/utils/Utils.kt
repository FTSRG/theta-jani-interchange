package hu.bme.mit.inf.theta.interchange.jani.model.utils

fun String.upperSnakeToLowerKebabCase(): String = toLowerCase().replace("_", "-")
