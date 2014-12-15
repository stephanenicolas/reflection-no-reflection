/**
 * Overview of the Reflection No Reflection build cycle.
 * <img src="doc-files/build-cycle.png">
 * @author SNI
 */
/*
 * @startuml doc-files/build-cycle.png
 * skinparam activity {
 *   BackgroundColor<< Result >> Olive
 *   BackgroundColor<< Anno >> Aquamarine
 *   BackgroundColor<< Weaving >> Coral
 * }
 *
 * (*) --> "Java source files" << Result >>
 * "Java source files" << Result >> --> "Compile Java sources\n without annotation processing"
 * --> "Java class files" << Result >>
 * "Java source files" << Result >> --> "Compile Java sources\n with annotation processing only" << Anno >>
 * --> "Annotation database source" << Result >>
 * "Annotation database source" --> "Compile annotation database only"
 * --> "Annotation database class file" << Result >>
 * "Java class files" << Result >> --> "Weave byte code" << Weaving >>
 * "Weave byte code" --> "Weaved Java class files" << Result >>
 *  --> [in classpath] "Compile annotation database only"
 * "Annotation database class file" << Result >> --> "copy"
 * "Weaved Java class files" << Result >> --> "copy"
 * "copy" --> "Java classes \n+ Annotation Database" << Result >>
 * "Java classes \n+ Annotation Database" << Result >> --> "dex"
 * "dex" --> "Dex file" << Result >>
 * @enduml
 */
package org.reflection_no_reflection.plugin;
