/**
 * See below, this document describes an overview of the
 * common-library.
 *
 * The common-lib provides high level classes used by RnR clients.
 * Those high level classes have different implementations. One of them
 * is based on the java core reflection library and the other one
 * is based on RnR.
 * <img src="doc-files/common-lib-overview.png">
 * @author SNI
 */
/*
 * @startuml doc-files/common-lib-overview.png
 * title Overview of common-lib
 * org.RnR.Field <|-- org.RnR.reflection.ReflectionField
 * org.RnR.Field <|-- org.RnR.no_reflection.NoReflectionField
 * org.RnR.FieldFinder <|-- org.RnR.reflection.ReflectionFieldFinder
 * org.RnR.FieldFinder <|-- org.RnR.no_reflection.NoReflectionFieldFinder
 * package org.RnR {
 *   class Field
 * note top : Provides the same API as java.lang.Field
 *   class FieldFinder
 * }
 * @enduml
 */
package org.reflection_no_reflection;