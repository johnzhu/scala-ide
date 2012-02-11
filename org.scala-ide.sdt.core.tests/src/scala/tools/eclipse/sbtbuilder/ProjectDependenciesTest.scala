package scala.tools.eclipse
package sbtbuilder

import org.junit.Test
import org.eclipse.jdt.core.IJavaProject
import org.eclipse.jdt.core.IClasspathEntry
import org.eclipse.jdt.core.JavaCore
import org.junit.Assert
import util.EclipseUtils._

class ProjectDependenciesTest {

  val simulator = new EclipseUserSimulator
  
  @Test def transitive_dependencies_no_export() {

    val Seq(prjA, prjB, prjC) = createProjects("A", "B", "C")
    
    addToClasspath(prjB, JavaCore.newProjectEntry(prjA.underlying.getFullPath, false))
    addToClasspath(prjC, JavaCore.newProjectEntry(prjB.underlying.getFullPath, false))
    
    Assert.assertEquals("No dependencies for base project", Seq(), prjA.transitiveDependencies)
    Assert.assertEquals("One direct dependency for B", Seq(prjA.underlying), prjB.transitiveDependencies)
    Assert.assertEquals("One transitive dependency for C", Seq(prjB.underlying), prjC.transitiveDependencies)
    
    deleteProjects(prjA, prjB, prjC)
  }
  
  
  @Test def transitive_dependencies_with_export() {

    val Seq(prjA, prjB, prjC) = createProjects("A", "B", "C")
    
    addToClasspath(prjB, JavaCore.newProjectEntry(prjA.underlying.getFullPath, true))
    addToClasspath(prjC, JavaCore.newProjectEntry(prjB.underlying.getFullPath, false))
    
    Assert.assertEquals("No dependencies for base project", Seq(), prjA.transitiveDependencies)
    Assert.assertEquals("One direct dependency for B", Seq(prjA.underlying), prjB.transitiveDependencies)
    Assert.assertEquals("Two transitive dependencies for C", Seq(prjB.underlying, prjA.underlying), prjC.transitiveDependencies)
    
    deleteProjects(prjA, prjB, prjC)
  }
  
  
  private def addToClasspath(prj: ScalaProject, entries: IClasspathEntry*) {
    val existing = prj.javaProject.getRawClasspath
    prj.javaProject.setRawClasspath(existing ++ entries, null)
  }
  
  private def createProjects(names: String*): Seq[ScalaProject] = 
    names map (n => simulator.createProjectInWorkspace(n, false))
  
  private def deleteProjects(projects: ScalaProject*) {
    workspaceRunnableIn(ScalaPlugin.plugin.workspaceRoot.getWorkspace) { _ =>
      projects foreach (_.underlying.delete(true, null))
    } 
  }
}