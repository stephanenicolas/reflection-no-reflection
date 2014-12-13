/**
 * See below, this document describes an overview of the
 * Reflection No Reflection library.
 *
 * Demonstration for PlantUML.
 * <p>
 * Example of use:
 * <p>
 * <img src="doc-files/image1.png">
 * @author SNI
 */
/*
 * @startuml doc-files/image1.png
 * title Overview of Reflection No Reflection components
 * [common-lib]
 * [common-lib] <-down- [annotation-processor]: use
 * [common-lib] <-down- [weaver]: use
 * [gradle-plugin] -down-> [sample] : applied during build
 * [gradle-plugin] .right.> [weaver] : activate
 * [gradle-plugin] .left.> [annotation-processor] : activate
 * @enduml
 *
 * You can now browse each package to find more on the role of each component.
 */
package org.reflection_no_reflection;